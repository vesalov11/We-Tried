package com.example.we_tried.cart.service;

import com.example.we_tried.cart.model.Cart;
import com.example.we_tried.cart.repository.CartRepository;
import com.example.we_tried.dish.model.Dish;
import com.example.we_tried.dish.repository.DishRepository;
import com.example.we_tried.order.model.FoodOrder;
import com.example.we_tried.order.model.OrderItem;
import com.example.we_tried.order.model.OrderStatus;
import com.example.we_tried.order.model.PaymentMethod;
import com.example.we_tried.order.repository.OrderItemRepository;
import com.example.we_tried.order.repository.OrderRepository;
import com.example.we_tried.restaurant.model.Restaurant;
import com.example.we_tried.user.model.User;
import com.example.we_tried.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final DishRepository dishRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    @Autowired
    public CartService(CartRepository cartRepository, DishRepository dishRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.dishRepository = dishRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void addToCart(UUID dishId, int quantity, UUID userId) {
        Cart cart = cartRepository.findByOwnerId(userId)
                .orElseGet(() -> createNewCart(userId));

        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new RuntimeException("Dish not found"));
        Restaurant restaurant = dish.getRestaurant();

        FoodOrder order = cart.getOrders().stream()
                .filter(o -> o.getRestaurant().equals(restaurant))
                .findFirst()
                .orElseGet(() -> createNewOrder(cart, restaurant));


        OrderItem item = order.getOrderItems().stream()
                .filter(i -> i.getDish().equals(dish))
                .findFirst()
                .orElseGet(() -> createNewOrderItem(order, dish));

        item.setQuantity(item.getQuantity() + quantity);
        orderItemRepository.save(item);

        updateOrderTotal(order);
        updateCartTotal(cart);
    }

    public Cart getOrCreateCart(UUID userId) {
        return cartRepository.findByOwnerId(userId)
                .orElseGet(() -> createNewCart(userId));
    }

    private Cart createNewCart(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id [%s] not found".formatted(userId)));

        Cart newCart = new Cart();
        newCart.setOwner(user);
        newCart.setTotalPrice(BigDecimal.ZERO);
        newCart.setOrders(new ArrayList<>());
        return cartRepository.save(newCart);
    }

    private FoodOrder createNewOrder(Cart cart, Restaurant restaurant) {
        FoodOrder newOrder = orderRepository.save(FoodOrder.builder()
                .restaurant(restaurant)
                .orderStatus(OrderStatus.WAITING_FOR_DELIVERY)
                .orderDate(LocalDateTime.now())
                .totalPrice(getTotalPrice(cart))
                .orderItems(new ArrayList<>())
                .cart(cart)
                .build());

        cart.getOrders().add(newOrder);

        return newOrder;
    }


    private OrderItem createNewOrderItem(FoodOrder order, Dish dish) {
        OrderItem newOrderItem = OrderItem.builder()
                .dish(dish)
                .price(dish.getPrice())
                .quantity(0)
                .order(order)
                .build();

        newOrderItem = orderItemRepository.save(newOrderItem);
        order.getOrderItems().add(newOrderItem);

        // Обнови сумите
        updateOrderTotal(order);
        if (order.getCart() != null) {
            updateCartTotal(order.getCart());
        }

        return newOrderItem;
    }

    public void updateOrderTotal(FoodOrder order) {
        BigDecimal total = order.getOrderItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(total);
        orderRepository.save(order);
    }

    public void updateCartTotal(Cart cart) {
        BigDecimal total = cart.getOrders().stream()
                .map(FoodOrder::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(total);
        cartRepository.save(cart);
    }

    public BigDecimal getTotalPrice(Cart cart) {
        return cart.getTotalPrice();
    }

    @Transactional
    public void checkout(UUID userId, String deliveryAddress, String paymentMethod) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByOwnerId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getOrders().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            throw new RuntimeException("Delivery address is required");
        }

        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new RuntimeException("Payment method is required");
        }

        for (FoodOrder order : cart.getOrders()) {
            order.setOwner(user);
            order.setDeliveryAddress(deliveryAddress);
            order.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));
            order.setOrderStatus(OrderStatus.WAITING_FOR_DELIVERY);
            updateOrderTotal(order);
            order.setCart(null);
            orderRepository.save(order);
        }

        cart.getOrders().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    @Transactional
    public void increaseQuantity(UUID orderItemId) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));

        item.setQuantity(item.getQuantity() + 1);
        orderItemRepository.save(item);

        updateOrderTotal(item.getOrder());
        updateCartTotal(item.getOrder().getCart());
    }

    @Transactional
    public void decreaseQuantity(UUID orderItemId) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            orderItemRepository.save(item);

            updateOrderTotal(item.getOrder());
            updateCartTotal(item.getOrder().getCart());
        } else {

            FoodOrder order = item.getOrder();
            order.getOrderItems().remove(item);
            orderItemRepository.delete(item);

            updateOrderTotal(order);
            updateCartTotal(order.getCart());
        }
    }

}