package com.example.we_tried.revenue.service;

import com.example.we_tried.order.model.FoodOrder;
import com.example.we_tried.order.model.OrderStatus;
import com.example.we_tried.order.repository.OrderRepository;
import com.example.we_tried.revenue.RevenueResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class RevenueService {

    private final OrderRepository orderRepository;

    public RevenueService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<RevenueResponse> getRevenueByPeriod(String period) {
        List<FoodOrder> completedOrders = orderRepository.findByOrderStatus(OrderStatus.COMPLETED);

        return switch (period.toLowerCase()) {
            case "daily"   -> groupByDay(completedOrders);
            case "weekly"  -> groupByWeek(completedOrders);
            case "monthly" -> groupByMonth(completedOrders);
            case "yearly"  -> groupByYear(completedOrders);
            default        -> throw new IllegalArgumentException("Invalid period: " + period);
        };
    }

    private List<RevenueResponse> groupByDay(List<FoodOrder> orders) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return orders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getOrderDate().toLocalDate().format(fmt),
                        TreeMap::new,
                        Collectors.mapping(
                                FoodOrder::getTotalPrice,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ))
                .entrySet().stream()
                .map(e -> new RevenueResponse(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private List<RevenueResponse> groupByWeek(List<FoodOrder> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        o -> {
                            LocalDate date = o.getOrderDate().toLocalDate();
                            int week = date.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                            int year = date.getYear();
                            return String.format("Week %d (%d)", week, year);
                        },
                        TreeMap::new,
                        Collectors.mapping(
                                FoodOrder::getTotalPrice,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ))
                .entrySet().stream()
                .map(e -> new RevenueResponse(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private List<RevenueResponse> groupByMonth(List<FoodOrder> orders) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return orders.stream()
                .collect(Collectors.groupingBy(
                        o -> YearMonth.from(o.getOrderDate()).format(formatter),
                        TreeMap::new,
                        Collectors.mapping(
                                FoodOrder::getTotalPrice,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ))
                .entrySet().stream()
                .map(e -> new RevenueResponse(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    private List<RevenueResponse> groupByYear(List<FoodOrder> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(
                        o -> String.valueOf(o.getOrderDate().getYear()),
                        TreeMap::new,
                        Collectors.mapping(
                                FoodOrder::getTotalPrice,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ))
                .entrySet().stream()
                .map(e -> new RevenueResponse(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
