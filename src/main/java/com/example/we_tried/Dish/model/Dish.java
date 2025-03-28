package com.example.we_tried.Dish.model;

import com.example.we_tried.Order.model.OrderItem;
import com.example.we_tried.Restaurant.model.Restaurant;
import jakarta.persistence.*;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private DishType dishType;

    @ManyToOne
    private Restaurant restaurant;

    @OneToMany(mappedBy = "dish", fetch = FetchType.EAGER)
    private List<OrderItem> orderItems;
}
