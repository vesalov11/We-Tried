package com.example.we_tried.cart.service;

import com.example.we_tried.cart.model.Cart;
import com.example.we_tried.cart.repository.CartRepository;
import com.example.we_tried.dish.model.Dish;
import com.example.we_tried.dish.repository.DishRepository;
import com.example.we_tried.order.model.Order;
import com.example.we_tried.order.model.OrderItem;
import com.example.we_tried.order.model.OrderStatus;
import com.example.we_tried.order.repository.OrderItemRepository;
import com.example.we_tried.order.repository.OrderRepository;
import com.example.we_tried.restaurant.model.Restaurant;
import com.example.we_tried.user.model.User;
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

    @Autowired
    public CartService(CartRepository cartRepository, DishRepository dishRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.cartRepository = cartRepository;
        this.dishRepository = dishRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public void addToCart(UUID dishId, int quantity, User user) {
        Cart cart = cartRepository.findByOwner(user)
                .orElseGet(() -> createNewCart(user));

        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new RuntimeException("Dish not found"));
        Restaurant restaurant = dish.getRestaurant();

        Order order = cart.getOrders().stream()
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

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByOwner(user)
                .orElseGet(() -> createNewCart(user));
    }

    private Cart createNewCart(User user) {
        Cart newCart = new Cart();
        newCart.setOwner(user);
        newCart.setTotalPrice(BigDecimal.ZERO);
        newCart.setOrders(new ArrayList<>());
        return cartRepository.save(newCart);
    }

    private Order createNewOrder(Cart cart, Restaurant restaurant) {
        Order newOrder = orderRepository.save(Order.builder()
                .restaurant(restaurant)
                .orderStatus(OrderStatus.WAITING_FOR_DELIVERY)
                .orderDate(LocalDateTime.now())
                .totalPrice(BigDecimal.ZERO)
                .cart(cart)
                .build());

        cart.getOrders().add(newOrder);
        return newOrder;
    }

    private OrderItem createNewOrderItem(Order order, Dish dish) {
        return orderItemRepository.save(OrderItem.builder()
                .dish(dish)
                .price(dish.getPrice())
                .quantity(0)
                .order(order)
                .build());
    }

    private void updateOrderTotal(Order order) {
        BigDecimal total = order.getOrderItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(total);
        orderRepository.save(order);
    }

    private void updateCartTotal(Cart cart) {
        BigDecimal total = cart.getOrders().stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(total);
        cartRepository.save(cart);
    }

    public BigDecimal getTotalPrice(Cart cart) {
        return cart.getTotalPrice();
    }

    // CartService.java
    @Transactional
    public void checkout(User user, String deliveryAddress, String paymentMethod) {
        Cart cart = cartRepository.findByOwner(user)
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

        for (Order order : cart.getOrders()) {
            order.setDeliveryAddress(deliveryAddress);
            order.setPaymentMethod(paymentMethod);
            order.setOrderStatus(OrderStatus.WAITING_FOR_DELIVERY);
            order.setCart(null);
            orderRepository.save(order);
        }

        cart.getOrders().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }
}