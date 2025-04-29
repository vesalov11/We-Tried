package com.example.we_tried.tests.DelivererServiceTests;
import com.example.we_tried.deliverer.model.Deliverer;
import com.example.we_tried.deliverer.repository.DelivererRepository;
import com.example.we_tried.deliverer.service.DelivererService;
import com.example.we_tried.order.model.FoodOrder;
import com.example.we_tried.order.model.OrderStatus;
import com.example.we_tried.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DelivererServiceTest {

    @Mock
    private DelivererRepository delivererRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private DelivererService delivererService;

    @Test
    public void testFindById() {
        UUID delivererId = UUID.randomUUID();
        Deliverer deliverer = new Deliverer();
        when(delivererRepository.findById(delivererId)).thenReturn(Optional.of(deliverer));

        Optional<Deliverer> result = delivererService.findById(delivererId);

        assertTrue(result.isPresent());
        assertEquals(deliverer, result.get());
    }

    @Test
    public void testGetCompletedOrders() {
        UUID delivererId = UUID.randomUUID();
        FoodOrder order = new FoodOrder();
        order.setOrderStatus(OrderStatus.COMPLETED);
        when(orderRepository.findByDeliverer_IdAndOrderStatus(delivererId, OrderStatus.COMPLETED)).thenReturn(List.of(order));

        List<FoodOrder> result = delivererService.getCompletedOrders(delivererId);

        assertEquals(1, result.size());
        assertEquals(OrderStatus.COMPLETED, result.get(0).getOrderStatus());
    }

    @Test
    public void testCalculateMonthlyRevenue() {
        UUID delivererId = UUID.randomUUID();
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        BigDecimal expectedRevenue = new BigDecimal("150.00");

        when(orderRepository.sumTotalByDelivererIdAndCompletedBetween(delivererId, OrderStatus.COMPLETED, startOfMonth, endOfMonth))
                .thenReturn(Optional.of(expectedRevenue));

        BigDecimal result = delivererService.calculateMonthlyRevenue(delivererId);

        assertEquals(expectedRevenue, result);
    }

    @Test
    public void testGetAllOrders() {
        FoodOrder order = new FoodOrder();
        when(orderRepository.findAll()).thenReturn(List.of(order));

        List<FoodOrder> result = delivererService.getAllOrders();

        assertEquals(1, result.size());
        assertEquals(order, result.get(0));
    }

    @Test
    public void testGetDelivererOrders() {
        UUID delivererId = UUID.randomUUID();
        FoodOrder order = new FoodOrder();
        when(orderRepository.findByDeliverer_Id(delivererId)).thenReturn(List.of(order));

        List<FoodOrder> result = delivererService.getDelivererOrders(delivererId);

        assertEquals(1, result.size());
        assertEquals(order, result.get(0));
    }

    @Test
    public void testIsEligibleForBonus() {
        UUID delivererId = UUID.randomUUID();
        BigDecimal revenue = new BigDecimal("350.00");
        when(orderRepository.sumTotalByDelivererIdAndCompletedBetween(eq(delivererId), eq(OrderStatus.COMPLETED), any(), any()))
                .thenReturn(Optional.of(revenue));

        boolean result = delivererService.isEligibleForBonus(delivererId);

        assertTrue(result);
    }

    @Test
    public void testCalculateTotalOrdersValue() {
        UUID delivererId = UUID.randomUUID();
        FoodOrder order = new FoodOrder();
        order.setTotalPrice(new BigDecimal("50.00"));
        when(orderRepository.findByDeliverer_Id(delivererId)).thenReturn(List.of(order));

        BigDecimal result = delivererService.calculateTotalOrdersValue(delivererId);

        assertEquals(new BigDecimal("50.00"), result);
    }

    @Test
    public void testDeleteOrderById() {
        UUID orderId = UUID.randomUUID();
        doNothing().when(orderRepository).deleteById(orderId);

        delivererService.deleteOrderById(orderId);

        verify(orderRepository, times(1)).deleteById(orderId);
    }
}