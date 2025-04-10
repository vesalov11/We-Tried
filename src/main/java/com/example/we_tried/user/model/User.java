package com.example.we_tried.user.model;

import com.example.we_tried.order.model.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class User extends BaseUser {

    private String address;

    private String profilePictureUrl;

    @Column(nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Order> orders;
}
