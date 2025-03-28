package com.example.we_tried.Order.model;

import com.example.we_tried.Dish.model.Dish;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Dish dish;
}