package com.example.we_tried.tests.cartServiceTests;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.we_tried.cart.model.Cart;
import com.example.we_tried.cart.repository.CartRepository;
import com.example.we_tried.cart.service.CartService;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private DishRepository dishRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private UUID userId;
    private UUID dishId;
    private User user;
    private Dish dish;
    private Restaurant restaurant;
    private Cart cart;
    private FoodOrder order;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        dishId = UUID.randomUUID();
        user = new User();
        user.setId(userId);

        restaurant = new Restaurant();
        restaurant.setId(UUID.randomUUID());

        dish = new Dish();
        dish.setId(dishId);
        dish.setPrice(BigDecimal.TEN);
        dish.setRestaurant(restaurant);

        cart = new Cart();
        cart.setOwner(user);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setOrders(new ArrayList<>());

        order = new FoodOrder();
        order.setId(UUID.randomUUID());
        order.setRestaurant(restaurant);
        order.setOrderStatus(OrderStatus.WAITING_FOR_DELIVERY);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(BigDecimal.ZERO);
        order.setCart(cart);
        order.setOrderItems(new ArrayList<>());

        orderItem = new OrderItem();
        orderItem.setId(UUID.randomUUID());
        orderItem.setDish(dish);
        orderItem.setPrice(dish.getPrice());
        orderItem.setQuantity(1);
        orderItem.setOrder(order);
    }

    @Test
    void addToCart_shouldCreateNewCartWhenNotExists() {
        UUID userId = UUID.randomUUID();
        UUID dishId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Dish dish = new Dish();
        dish.setId(dishId);
        dish.setPrice(new BigDecimal("15.00"));

        Cart cart = new Cart();
        cart.setOwner(user);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setOrders(new ArrayList<>());

        FoodOrder order = new FoodOrder();
        order.setRestaurant(new Restaurant());
        order.setOrderItems(new ArrayList<>());
        order.setOrderStatus(OrderStatus.WAITING_FOR_DELIVERY);
        order.setCart(cart);

        OrderItem orderItem = new OrderItem();
        orderItem.setDish(dish);
        orderItem.setPrice(dish.getPrice());
        orderItem.setQuantity(2);
        orderItem.setOrder(order);

        when(cartRepository.findByOwnerId(userId)).thenReturn(Optional.empty());
        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(orderRepository.save(any(FoodOrder.class))).thenReturn(order);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);

        cartService.addToCart(dishId, 2, userId);

        verify(cartRepository).save(any(Cart.class));
        verify(orderRepository).save(any(FoodOrder.class));
        verify(orderItemRepository).save(any(OrderItem.class));
        assertEquals(2, orderItem.getQuantity());
        assertEquals(new BigDecimal("30.00"), order.getTotalPrice());
        assertEquals(new BigDecimal("30.00"), cart.getTotalPrice());
    }


    @Test
    void addToCart_shouldAddQuantityToExistingItem() {
        order.getOrderItems().add(orderItem);
        cart.getOrders().add(order);

        when(cartRepository.findByOwnerId(userId)).thenReturn(Optional.of(cart));
        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));

        cartService.addToCart(dishId, 3, userId);

        assertEquals(4, orderItem.getQuantity());
        verify(orderItemRepository).save(orderItem);
    }

    @Test
    void getOrCreateCart_shouldReturnExistingCart() {
        when(cartRepository.findByOwnerId(userId)).thenReturn(Optional.of(cart));

        Cart result = cartService.getOrCreateCart(userId);

        assertEquals(cart, result);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void getOrCreateCart_shouldCreateNewCartWhenNotExists() {
        when(cartRepository.findByOwnerId(userId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart result = cartService.getOrCreateCart(userId);

        assertNotNull(result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void checkout_shouldProcessValidCart() {
        UUID userId = UUID.randomUUID();
        Cart cart = new Cart();
        User user = new User();
        user.setId(userId);

        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(1);
        orderItem.setPrice(new BigDecimal("10.00"));

        FoodOrder order = new FoodOrder();
        order.setOrderItems(new ArrayList<>());
        order.getOrderItems().add(orderItem);
        order.setRestaurant(new Restaurant());
        order.setOrderStatus(OrderStatus.WAITING_FOR_DELIVERY);
        order.setDeliveryAddress("Test Address");
        order.setPaymentMethod(PaymentMethod.CARD);
        cart.setOrders(new ArrayList<>());
        cart.getOrders().add(order);
        cart.setOwner(user);
        cart.setTotalPrice(new BigDecimal("10.00"));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.findByOwnerId(userId)).thenReturn(Optional.of(cart));

        cartService.checkout(userId, "Ivan Mihailov N49", "CARD");

        assertNull(order.getCart());
        assertEquals(OrderStatus.WAITING_FOR_DELIVERY, order.getOrderStatus());
        assertEquals("Ivan Mihailov N49", order.getDeliveryAddress());
        assertEquals(PaymentMethod.CARD, order.getPaymentMethod());
        assertTrue(cart.getOrders().isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
    }

    @Test
    void increaseQuantity_shouldIncrementQuantity() {
        when(orderItemRepository.findById(orderItem.getId())).thenReturn(Optional.of(orderItem));

        cartService.increaseQuantity(orderItem.getId());

        assertEquals(2, orderItem.getQuantity());
        verify(orderItemRepository).save(orderItem);
    }

    @Test
    void decreaseQuantity_shouldDecrementQuantityWhenGreaterThanOne() {
        orderItem.setQuantity(2);
        when(orderItemRepository.findById(orderItem.getId())).thenReturn(Optional.of(orderItem));

        cartService.decreaseQuantity(orderItem.getId());

        assertEquals(1, orderItem.getQuantity());
        verify(orderItemRepository).save(orderItem);
    }

    @Test
    void decreaseQuantity_shouldRemoveItemWhenQuantityIsOne() {
        when(orderItemRepository.findById(orderItem.getId())).thenReturn(Optional.of(orderItem));

        cartService.decreaseQuantity(orderItem.getId());

        verify(orderItemRepository).delete(orderItem);
        assertFalse(order.getOrderItems().contains(orderItem));
    }

    @Test
    void updateOrderTotal_shouldCalculateCorrectTotal() {
        OrderItem item2 = new OrderItem();
        item2.setPrice(BigDecimal.valueOf(5));
        item2.setQuantity(2);
        order.getOrderItems().addAll(List.of(orderItem, item2));

        cartService.updateOrderTotal(order);

        assertEquals(BigDecimal.valueOf(20), order.getTotalPrice());
    }

    @Test
    void updateCartTotal_shouldCalculateCorrectTotal() {
        FoodOrder order2 = new FoodOrder();
        order2.setTotalPrice(BigDecimal.valueOf(15));
        cart.getOrders().addAll(List.of(order, order2));

        cartService.updateCartTotal(cart);

        assertEquals(BigDecimal.valueOf(15), cart.getTotalPrice());
    }
}