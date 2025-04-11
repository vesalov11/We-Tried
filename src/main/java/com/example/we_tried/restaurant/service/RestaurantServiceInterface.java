package com.example.we_tried.restaurant.service;

import com.example.we_tried.restaurant.model.Restaurant;

import java.util.List;
import java.util.UUID;

public interface RestaurantServiceInterface {
    List<Restaurant> getAll();
    Restaurant getById(UUID id);
    Restaurant create(Restaurant restaurant);
    Restaurant update(UUID id, Restaurant restaurant);
    void delete(UUID id);
}
