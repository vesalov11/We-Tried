package com.example.we_tried.tests.OrderServiceTests;


import com.example.we_tried.order.model.Order;
import com.example.we_tried.order.repository.OrderRepository;
import com.example.we_tried.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @InjectMocks private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllOrders_shouldReturnAllOrders() {
        List<Order> orders = List.of(new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void getDelivererOrders_shouldReturnDelivererOrders() {
        UUID delivererId = UUID.randomUUID();
        List<Order> orders = List.of(new Order());
        when(orderRepository.findByDelivererId(delivererId)).thenReturn(orders);

        List<Order> result = orderService.getDelivererOrders(delivererId);

        assertEquals(1, result.size());
        verify(orderRepository).findByDelivererId(delivererId);
    }
}