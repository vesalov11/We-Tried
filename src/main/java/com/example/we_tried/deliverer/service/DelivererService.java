package com.example.we_tried.deliverer.service;

import com.example.we_tried.deliverer.model.Deliverer;
import com.example.we_tried.deliverer.repository.DelivererRepository;
import com.example.we_tried.order.model.Order;
import com.example.we_tried.order.model.OrderStatus; // Добавяне на OrderStatus
import com.example.we_tried.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DelivererService {

    private final DelivererRepository delivererRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public DelivererService(DelivererRepository delivererRepository, OrderRepository orderRepository) {
        this.delivererRepository = delivererRepository;
        this.orderRepository = orderRepository;
    }

    public Deliverer getDelivererById(UUID delivererId) {
        return delivererRepository.findById(delivererId)
                .orElseThrow(() -> new IllegalArgumentException("Deliverer not found with ID: " + delivererId));
    }

    public List<Order> getCompletedOrders(UUID delivererId) {
        // Подава се OrderStatus вместо стринг
        return orderRepository.findByDelivererIdAndOrderStatus(delivererId, OrderStatus.COMPLETED); // Променено на OrderStatus
    }

    public BigDecimal calculateMonthlyRevenue(UUID delivererId) {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);

        // Подава се OrderStatus вместо стринг
        return orderRepository.sumTotalByDelivererIdAndCompletedBetween(delivererId, OrderStatus.COMPLETED, startOfMonth, endOfMonth) // Променено на OrderStatus
                .orElse(BigDecimal.ZERO);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getDelivererOrders(UUID delivererId) {
        return orderRepository.findByDelivererId(delivererId);
    }

    public boolean isEligibleForBonus(UUID delivererId) {
        BigDecimal revenue = calculateMonthlyRevenue(delivererId);
        BigDecimal bonusThreshold = new BigDecimal("300"); // зададена сума

        return revenue.compareTo(bonusThreshold) >= 0;
    }

}
