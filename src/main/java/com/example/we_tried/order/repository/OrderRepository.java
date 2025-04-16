package com.example.we_tried.order.repository;

import com.example.we_tried.order.model.Order;
import com.example.we_tried.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.deliverer.id = :delivererId AND o.orderStatus = :status AND o.deliveredAt BETWEEN :start AND :end")
    Optional<BigDecimal> sumTotalByDelivererIdAndCompletedBetween(
            @Param("delivererId") UUID delivererId,
            @Param("status") OrderStatus status, // Променено на OrderStatus
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<Order> findByDelivererIdAndOrderStatus(UUID delivererId, OrderStatus status); // Променено на OrderStatus

    List<Order> findByDelivererId(UUID delivererId);

    List<Order> findByOrderStatus(OrderStatus status);
}