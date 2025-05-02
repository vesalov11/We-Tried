package com.example.we_tried.order.model;


import com.example.we_tried.cart.model.Cart;
import com.example.we_tried.deliverer.model.Deliverer;
import com.example.we_tried.restaurant.model.Restaurant;
import com.example.we_tried.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FoodOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private String deliveryAddress;

    private PaymentMethod paymentMethod;

    private LocalTime acceptedAt;

    private LocalTime deliveredAt;

    private String phoneNumber;

    @OneToMany(mappedBy = "order",fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne
    private User owner;

    @ManyToOne
    private Restaurant restaurant;

    @ManyToOne
    private Cart cart;

    @ManyToOne
    private Deliverer deliverer;

}
