package com.example.we_tried.revenue.repository;

import com.example.we_tried.order.model.FoodOrder;
import com.example.we_tried.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RevenueRepository extends JpaRepository<FoodOrder, UUID> {

}
