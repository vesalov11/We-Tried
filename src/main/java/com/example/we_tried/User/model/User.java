package com.example.we_tried.User.model;

import com.example.we_tried.Order.model.Order;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String confirmPassword;

    private String firstName;

    private String lastName;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String profilePictureUrl;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Order> orders;
}
