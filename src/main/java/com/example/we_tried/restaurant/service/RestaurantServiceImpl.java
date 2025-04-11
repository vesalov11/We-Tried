package com.example.we_tried.restaurant.service;

import com.example.we_tried.restaurant.model.Restaurant;
import com.example.we_tried.restaurant.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RestaurantServiceImpl implements RestaurantServiceInterface{
    private final RestaurantRepository restaurantRepository;
    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public List<Restaurant> getAll() {
        return restaurantRepository.findAll();
    }

    @Override
    public Restaurant getById(UUID id) {
        return restaurantRepository.findById(id).orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    @Override
    public Restaurant create(Restaurant restaurant) {
        if (restaurant.getName() == null || restaurant.getName().isEmpty()) {
            throw new RuntimeException("Restaurant name is empty");
        }
        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant update(UUID id, Restaurant updated) {

        Restaurant existing = getById(id);
        existing.setName(updated.getName());
        return restaurantRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        getById(id);
        restaurantRepository.deleteById(id);
    }
}
