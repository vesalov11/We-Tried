package com.example.we_tried.deliverer.model;

import com.example.we_tried.order.model.Order;
import com.example.we_tried.restaurant.model.Restaurant;
import com.example.we_tried.user.model.BaseUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Deliverer extends BaseUser {

    private boolean active;

    @Column(nullable = false)
    private Double value;

    private BigDecimal monthlyTarget;

    @ManyToOne
    private Restaurant restaurant;

    @OneToMany(mappedBy = "deliverer", fetch = FetchType.EAGER)
    private List<Order> orders;

}
