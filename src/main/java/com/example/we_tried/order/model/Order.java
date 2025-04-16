package com.example.we_tried.order.model;


import com.example.we_tried.cart.model.Cart;
import com.example.we_tried.deliverer.model.Deliverer;
import com.example.we_tried.restaurant.model.Restaurant;
// import com.example.we_tried.User.model.User; // Uncomment this if the User class exists
import com.example.we_tried.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Order {

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

    private String paymentMethod;

    private LocalDateTime acceptedAt;

    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @ManyToOne
    private User owner;

    @ManyToOne
    private Restaurant restaurant;

    @ManyToOne
    private Cart cart;

    @ManyToOne
    private Deliverer deliverer;

}
