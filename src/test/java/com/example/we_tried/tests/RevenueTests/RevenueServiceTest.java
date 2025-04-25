package com.example.we_tried.tests.RevenueTests;

import com.example.we_tried.order.model.FoodOrder;
import com.example.we_tried.order.model.OrderStatus;
import com.example.we_tried.order.repository.OrderRepository;
import com.example.we_tried.revenue.RevenueResponse;
import com.example.we_tried.revenue.service.RevenueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RevenueServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private RevenueService revenueService;

    private FoodOrder createOrder(OrderStatus status, BigDecimal price, int year, int month, int day) {
        FoodOrder order = new FoodOrder();
        order.setOrderStatus(status);
        order.setTotalPrice(price);
        order.setOrderDate(LocalDateTime.of(year, month, day, 10, 0));
        return order;
    }

    @Test
    void getRevenueByPeriod_shouldReturnCorrectGroupings() {
        when(orderRepository.findByOrderStatus(OrderStatus.COMPLETED)).thenReturn(List.of(
                createOrder(OrderStatus.COMPLETED, new BigDecimal("100.50"), 2023, 1, 15),
                createOrder(OrderStatus.COMPLETED, new BigDecimal("200.75"), 2023, 1, 15),
                createOrder(OrderStatus.COMPLETED, new BigDecimal("150.00"), 2023, 2, 20)
        ));

        assertAll(
                () -> assertEquals(2, revenueService.getRevenueByPeriod("daily").size()),
                () -> assertEquals(2, revenueService.getRevenueByPeriod("weekly").size()),
                () -> assertEquals(2, revenueService.getRevenueByPeriod("monthly").size()),
                () -> assertEquals(1, revenueService.getRevenueByPeriod("yearly").size())
        );
    }

    @Test
    void getRevenueByPeriod_shouldHandleInvalidPeriod() {
        assertThrows(IllegalArgumentException.class,
                () -> revenueService.getRevenueByPeriod("invalid"));
    }

    @Test
    void getRevenueByPeriod_shouldIgnoreNonCompletedOrders() {
        FoodOrder completedOrder = createOrder(OrderStatus.COMPLETED, new BigDecimal("100.00"), 2023, 1, 1);
        FoodOrder pendingOrder = createOrder(OrderStatus.PICKED_UP, new BigDecimal("50.00"), 2023, 1, 1);

        when(orderRepository.findByOrderStatus(OrderStatus.COMPLETED))
                .thenReturn(List.of(completedOrder));

        List<RevenueResponse> result = revenueService.getRevenueByPeriod("daily");

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("100.00"), result.get(0).getRevenue());
    }

    @Test
    void getRevenueByPeriod_shouldReturnEmptyForNoOrders() {
        when(orderRepository.findByOrderStatus(OrderStatus.COMPLETED)).thenReturn(List.of());
        assertTrue(revenueService.getRevenueByPeriod("daily").isEmpty());
    }
}