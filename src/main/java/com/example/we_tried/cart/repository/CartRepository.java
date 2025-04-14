package com.example.we_tried.cart.repository;

import com.example.we_tried.cart.model.Cart;
import com.example.we_tried.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByOwner(User user);

}
