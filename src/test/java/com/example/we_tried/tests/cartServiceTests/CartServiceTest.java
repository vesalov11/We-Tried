package com.example.we_tried.tests.cartServiceTests;

import com.example.we_tried.cart.model.Cart;
import com.example.we_tried.cart.repository.CartRepository;
import com.example.we_tried.cart.service.CartService;
import com.example.we_tried.dish.model.Dish;
import com.example.we_tried.dish.repository.DishRepository;
import com.example.we_tried.order.model.*;
import com.example.we_tried.order.repository.OrderItemRepository;
import com.example.we_tried.order.repository.OrderRepository;
import com.example.we_tried.restaurant.model.Restaurant;
import com.example.we_tried.user.model.User;
import com.example.we_tried.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    private UUID restaurantId;
    private User user;
    private Dish dish;
    private Restaurant restaurant;
    private Cart cart;
    private Order order;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        dishId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        restaurant = new Restaurant();
        restaurant.setId(restaurantId);

        dish = new Dish();
        dish.setId(dishId);
        dish.setPrice(new BigDecimal("10.00"));
        dish.setRestaurant(restaurant);

        cart = new Cart();
        cart.setOwner(user);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setOrders(new ArrayList<>());

        order = new Order();
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
        Cart newCart = new Cart();
        newCart.setOwner(user);
        newCart.setOrders(new ArrayList<>());
        newCart.setTotalPrice(BigDecimal.ZERO);

        when(cartRepository.findByOwner(userId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);

        cartService.addToCart(dishId, 1, userId);

        verify(cartRepository).save(argThat(cart ->
                cart.getOrders() != null && cart.getTotalPrice().equals(BigDecimal.ZERO)
        ));
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).save(any(OrderItem.class));
    }

    @Test
    void addToCart_shouldAddToExistingOrderWhenSameRestaurant() {
        when(cartRepository.findByOwner(userId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);

        cartService.addToCart(dishId, 1, userId);

        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).save(any(OrderItem.class));
    }

    @Test
    void getOrCreateCart_shouldReturnExistingCart() {
        when(cartRepository.findByOwner(userId)).thenReturn(Optional.of(cart));

        Cart result = cartService.getOrCreateCart(userId);

        assertEquals(cart, result);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void getOrCreateCart_shouldCreateNewCartWhenNotExists() {
        when(cartRepository.findByOwner(userId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart result = cartService.getOrCreateCart(userId);

        assertNotNull(result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void checkout_shouldThrowWhenCartNotFound() {
        when(cartRepository.findByOwner(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                cartService.checkout(userId, "Address", "CASH"));
    }

    @Test
    void checkout_shouldThrowWhenCartEmpty() {
        when(cartRepository.findByOwner(userId)).thenReturn(Optional.of(cart));

        assertThrows(RuntimeException.class, () ->
                cartService.checkout(userId, "Address", "CASH"));
    }

    @Test
    void checkout_shouldClearCartAndUpdateOrders() {
        cart.getOrders().add(order);
        when(cartRepository.findByOwner(userId)).thenReturn(Optional.of(cart));

        cartService.checkout(userId, "123 Main St", "CARD");

        assertTrue(cart.getOrders().isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
        assertEquals(OrderStatus.WAITING_FOR_DELIVERY, order.getOrderStatus());
        assertEquals("123 Main St", order.getDeliveryAddress());
        assertEquals(PaymentMethod.CARD, order.getPaymentMethod());
        verify(cartRepository).save(cart);
        verify(orderRepository).save(order);
    }

    @Test
    void increaseQuantity_shouldUpdateQuantityAndTotals() {
        when(orderItemRepository.findById(orderItem.getId())).thenReturn(Optional.of(orderItem));

        cartService.increaseQuantity(orderItem.getId());

        assertEquals(2, orderItem.getQuantity());
        verify(orderItemRepository).save(orderItem);
        verify(orderRepository).save(order);
        verify(cartRepository).save(cart);
    }

    @Test
    void decreaseQuantity_shouldDecreaseWhenQuantityAboveOne() {
        orderItem.setQuantity(2);
        when(orderItemRepository.findById(orderItem.getId())).thenReturn(Optional.of(orderItem));

        cartService.decreaseQuantity(orderItem.getId());

        assertEquals(1, orderItem.getQuantity());
        verify(orderItemRepository).save(orderItem);
    }

    @Test
    void decreaseQuantity_shouldRemoveItemWhenQuantityOne() {
        order.getOrderItems().add(orderItem);
        when(orderItemRepository.findById(orderItem.getId())).thenReturn(Optional.of(orderItem));

        cartService.decreaseQuantity(orderItem.getId());

        assertTrue(order.getOrderItems().isEmpty());
        verify(orderItemRepository).delete(orderItem);
    }

    @Test
    void getTotalPrice_shouldReturnCartTotal() {
        cart.setTotalPrice(new BigDecimal("25.50"));

        BigDecimal result = cartService.getTotalPrice(cart);

        assertEquals(new BigDecimal("25.50"), result);
    }

    @Test
    void updateOrderTotal_shouldCalculateCorrectTotal() {
        OrderItem item1 = new OrderItem();
        item1.setPrice(new BigDecimal("10.00"));
        item1.setQuantity(2);

        OrderItem item2 = new OrderItem();
        item2.setPrice(new BigDecimal("5.00"));
        item2.setQuantity(3);

        order.getOrderItems().addAll(List.of(item1, item2));

        cartService.updateOrderTotal(order);

        assertEquals(new BigDecimal("35.00"), order.getTotalPrice());
    }

    @Test
    void updateCartTotal_shouldCalculateCorrectTotal() {
        Order order1 = new Order();
        order1.setTotalPrice(new BigDecimal("15.00"));

        Order order2 = new Order();
        order2.setTotalPrice(new BigDecimal("25.00"));

        cart.getOrders().addAll(List.of(order1, order2));

        cartService.updateCartTotal(cart);

        assertEquals(new BigDecimal("40.00"), cart.getTotalPrice());
    }
}
