package com.example.we_tried.tests.OrderServiceTests;


import com.example.we_tried.order.model.FoodOrder;
import com.example.we_tried.order.model.FoodOrder;
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
        List<FoodOrder> orders = List.of(new FoodOrder(), new FoodOrder());
        when(orderRepository.findAll()).thenReturn(orders);

        List<FoodOrder> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void getDelivererOrders_shouldReturnDelivererOrders() {
        UUID delivererId = UUID.randomUUID();
        List<FoodOrder> orders = List.of(new FoodOrder());
        when(orderRepository.findByDeliverer_Id(delivererId)).thenReturn(orders);

        List<FoodOrder> result = orderService.getDelivererOrders(delivererId);

        assertEquals(1, result.size());
        verify(orderRepository).findByDeliverer_Id(delivererId);
    }
}