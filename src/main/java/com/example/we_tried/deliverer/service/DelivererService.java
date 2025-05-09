package com.example.we_tried.deliverer.service;

import com.example.we_tried.deliverer.model.Deliverer;
import com.example.we_tried.deliverer.repository.DelivererRepository;
import com.example.we_tried.order.model.FoodOrder;
import com.example.we_tried.order.model.OrderStatus;
import com.example.we_tried.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    public Optional<Deliverer> findById(UUID userId) {
        return delivererRepository.findById(userId);
    }

    public List<FoodOrder> getCompletedOrders(UUID delivererId) {

        return orderRepository.findByDeliverer_IdAndOrderStatus(delivererId, OrderStatus.COMPLETED);
    }

    public BigDecimal calculateMonthlyRevenue(UUID delivererId) {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);

        return orderRepository.sumTotalByDelivererIdAndCompletedBetween(delivererId, OrderStatus.COMPLETED, startOfMonth, endOfMonth)
                .orElse(BigDecimal.ZERO);
    }

    public List<FoodOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<FoodOrder> getDelivererOrders(UUID delivererId) {
        return orderRepository.findByDeliverer_Id(delivererId);
    }

    public boolean isEligibleForBonus(UUID delivererId) {
        BigDecimal revenue = calculateMonthlyRevenue(delivererId);
        BigDecimal bonusThreshold = new BigDecimal("300");

        return revenue.compareTo(bonusThreshold) >= 0;
    }

    public BigDecimal calculateTotalOrdersValue(UUID delivererId) {

        List<FoodOrder> orders = orderRepository.findByDeliverer_Id(delivererId);

        return orders.stream()
                .map(FoodOrder::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void deleteOrderById(UUID orderId) {
        orderRepository.deleteById(orderId);
    }


}
