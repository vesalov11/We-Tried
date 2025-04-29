package com.example.we_tried.deliverer.repository;

import com.example.we_tried.deliverer.model.Deliverer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DelivererRepository extends JpaRepository<Deliverer, UUID> {

    Optional<Deliverer> findById(UUID id);
}
