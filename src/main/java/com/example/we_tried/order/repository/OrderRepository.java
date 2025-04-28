package com.example.we_tried.order.repository;

import com.example.we_tried.order.model.FoodOrder;
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
public interface OrderRepository extends JpaRepository<FoodOrder, UUID> {

    @Query("SELECT SUM(o.totalPrice) FROM FoodOrder o WHERE o.deliverer.id = :delivererId AND o.orderStatus = :status AND o.deliveredAt BETWEEN :start AND :end")
    Optional<BigDecimal> sumTotalByDelivererIdAndCompletedBetween(
            @Param("delivererId") UUID delivererId,
            @Param("status") OrderStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<FoodOrder> findByDelivererIdAndOrderStatus(UUID delivererId, OrderStatus status);

    List<FoodOrder> findByDelivererId(UUID delivererId);

    List<FoodOrder> findByOrderStatus(OrderStatus status);
}