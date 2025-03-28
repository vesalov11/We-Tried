package com.example.we_tried.Restaurant.model;

import com.example.we_tried.Dish.model.Dish;
import com.example.we_tried.Order.model.Order;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDateTime workingTime;

    @Enumerated(EnumType.STRING)
    private Type restaurantType;

    private String restaurantPicture;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.EAGER)
    private List<Order> orders;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.EAGER)
    private List<Dish> dishes;

}
