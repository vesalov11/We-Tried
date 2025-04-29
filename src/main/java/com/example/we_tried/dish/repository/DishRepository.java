package com.example.we_tried.dish.repository;

import com.example.we_tried.dish.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DishRepository extends JpaRepository<Dish, UUID> {

}
