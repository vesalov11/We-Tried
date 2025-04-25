package com.example.we_tried.tests.DelivererServiceTests;

import com.example.we_tried.deliverer.model.Deliverer;
import com.example.we_tried.deliverer.repository.DelivererRepository;
import com.example.we_tried.deliverer.service.DelivererService;
import com.example.we_tried.order.model.FoodOrder;
import com.example.we_tried.order.model.OrderStatus;
import com.example.we_tried.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DelivererServiceTest {

    @Mock private DelivererRepository delivererRepository;
    @Mock private OrderRepository orderRepository;
    @InjectMocks private DelivererService delivererService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDelivererById_shouldReturnDeliverer() {
        UUID id = UUID.randomUUID();
        Deliverer deliverer = new Deliverer();
        when(delivererRepository.findById(id)).thenReturn(Optional.of(deliverer));

        Deliverer result = delivererService.getDelivererById(id);

        assertEquals(deliverer, result);
    }

    @Test
    void getDelivererById_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();
        when(delivererRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> delivererService.getDelivererById(id));
    }

    @Test
    void getCompletedOrders_shouldReturnOrders() {
        UUID id = UUID.randomUUID();
        List<FoodOrder> orders = List.of(new FoodOrder(), new FoodOrder());
        when(orderRepository.findByDelivererIdAndOrderStatus(id, OrderStatus.COMPLETED)).thenReturn(orders);

        List<FoodOrder> result = delivererService.getCompletedOrders(id);

        assertEquals(2, result.size());
    }

    @Test
    void calculateMonthlyRevenue_shouldReturnCorrectSum() {
        UUID id = UUID.randomUUID();
        when(orderRepository.sumTotalByDelivererIdAndCompletedBetween(
                eq(id), eq(OrderStatus.COMPLETED), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(new BigDecimal("500")));

        BigDecimal result = delivererService.calculateMonthlyRevenue(id);

        assertEquals(new BigDecimal("500"), result);
    }

    @Test
    void calculateMonthlyRevenue_shouldReturnZeroIfNoData() {
        UUID id = UUID.randomUUID();
        when(orderRepository.sumTotalByDelivererIdAndCompletedBetween(
                eq(id), eq(OrderStatus.COMPLETED), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        BigDecimal result = delivererService.calculateMonthlyRevenue(id);

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void getAllOrders_shouldReturnAllOrders() {
        List<FoodOrder> orders = List.of(new FoodOrder(), new FoodOrder(), new FoodOrder());
        when(orderRepository.findAll()).thenReturn(orders);

        List<FoodOrder> result = delivererService.getAllOrders();

        assertEquals(3, result.size());
    }

    @Test
    void getDelivererOrders_shouldReturnOrders() {
        UUID id = UUID.randomUUID();
        List<FoodOrder> orders = List.of(new FoodOrder(), new FoodOrder());
        when(orderRepository.findByDelivererId(id)).thenReturn(orders);

        List<FoodOrder> result = delivererService.getDelivererOrders(id);

        assertEquals(2, result.size());
    }

    @Test
    void isEligibleForBonus_shouldReturnTrueIfEligible() {
        UUID id = UUID.randomUUID();
        when(orderRepository.sumTotalByDelivererIdAndCompletedBetween(
                eq(id), eq(OrderStatus.COMPLETED), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(new BigDecimal("350")));

        boolean result = delivererService.isEligibleForBonus(id);

        assertTrue(result);
    }

    @Test
    void isEligibleForBonus_shouldReturnFalseIfNotEligible() {
        UUID id = UUID.randomUUID();
        when(orderRepository.sumTotalByDelivererIdAndCompletedBetween(
                eq(id), eq(OrderStatus.COMPLETED), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(new BigDecimal("250")));

        boolean result = delivererService.isEligibleForBonus(id);

        assertFalse(result);
    }

    @Test
    void calculateTotalOrdersValue_shouldReturnSum() {
        UUID id = UUID.randomUUID();
        FoodOrder o1 = new FoodOrder();
        o1.setTotalPrice(new BigDecimal("100"));
        FoodOrder o2 = new FoodOrder();
        o2.setTotalPrice(new BigDecimal("200"));

        when(orderRepository.findByDelivererId(id)).thenReturn(List.of(o1, o2));

        BigDecimal result = delivererService.calculateTotalOrdersValue(id);

        assertEquals(new BigDecimal("300"), result);
    }
}