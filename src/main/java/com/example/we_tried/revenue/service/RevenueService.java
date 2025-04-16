package com.example.we_tried.revenue.service;

import com.example.we_tried.order.model.Order;
import com.example.we_tried.order.model.OrderStatus;
import com.example.we_tried.order.repository.OrderRepository;
import com.example.we_tried.revenue.RevenueResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RevenueService {

    private final OrderRepository orderRepository;

    public RevenueService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<RevenueResponse> getRevenueByPeriod(String period) {
        List<Order> completedOrders = orderRepository.findByOrderStatus(OrderStatus.COMPLETED);

        return switch (period.toLowerCase()) {
            case "daily" -> groupByDay(completedOrders);
            case "weekly" -> groupByWeek(completedOrders);
            case "monthly" -> groupByMonth(completedOrders);
            case "yearly" -> groupByYear(completedOrders);
            default -> throw new IllegalArgumentException("Invalid period: " + period);
        };
    }

    private List<RevenueResponse> groupByDay(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getDeliveredAt().toLocalDate(),
                        TreeMap::new,
                        Collectors.mapping(Order::getTotalPrice, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ))
                .entrySet().stream()
                .map(e -> new RevenueResponse(e.getKey().toString(), e.getValue()))
                .collect(Collectors.toList());
    }

    private List<RevenueResponse> groupByWeek(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        o -> {
                            LocalDate date = o.getDeliveredAt().toLocalDate();
                            int week = date.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                            int year = date.getYear();
                            return String.format("Week %d (%d)", week, year);
                        },
                        TreeMap::new,
                        Collectors.mapping(Order::getTotalPrice, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ))
                .entrySet().stream()
                .map(e -> new RevenueResponse(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private List<RevenueResponse> groupByMonth(List<Order> orders) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

        return orders.stream()
                .collect(Collectors.groupingBy(
                        o -> YearMonth.from(o.getDeliveredAt()).format(formatter),
                        TreeMap::new,
                        Collectors.mapping(Order::getTotalPrice, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ))
                .entrySet().stream()
                .map(e -> new RevenueResponse(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private List<RevenueResponse> groupByYear(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        o -> String.valueOf(o.getDeliveredAt().getYear()),
                        TreeMap::new,
                        Collectors.mapping(Order::getTotalPrice, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ))
                .entrySet().stream()
                .map(e -> new RevenueResponse(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}