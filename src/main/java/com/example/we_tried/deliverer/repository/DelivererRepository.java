package com.example.we_tried.deliverer.repository;

import com.example.we_tried.deliverer.model.Deliverer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DelivererRepository extends JpaRepository<Deliverer, UUID> {

}
