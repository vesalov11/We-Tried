package com.example.we_tried.dish.model;


import com.example.we_tried.order.model.OrderItem;
import com.example.we_tried.restaurant.model.Restaurant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(nullable = false)
    private String dishImage;

    @ManyToOne
    private Restaurant restaurant;

    @OneToMany(mappedBy = "dish", fetch = FetchType.EAGER)
    private List<OrderItem> orderItems;
}
