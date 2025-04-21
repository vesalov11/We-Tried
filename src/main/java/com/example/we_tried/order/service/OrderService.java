package com.example.we_tried.order.service;

import org.springframework.stereotype.Service;
import com.example.we_tried.order.model.Order;
import com.example.we_tried.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {

        return orderRepository.findAll();
    }

    public List<Order> getDelivererOrders(UUID delivererId) {
        return orderRepository.findByDelivererId(delivererId);
    }
}
