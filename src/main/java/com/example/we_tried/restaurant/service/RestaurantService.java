package com.example.we_tried.restaurant.service;

import com.example.we_tried.restaurant.controller.CreateRestaurantRequest;
import com.example.we_tried.restaurant.controller.UpdateRestaurantRequest;
import com.example.we_tried.restaurant.model.Restaurant;
import com.example.we_tried.restaurant.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public List<Restaurant> getAll() {
        return restaurantRepository.findAll();
    }

    public Restaurant getById(UUID id) {
        return restaurantRepository.findById(id).orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    public void create(CreateRestaurantRequest request) {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findByName(request.getName());
        if (optionalRestaurant.isPresent()) {
            throw new RuntimeException("Restaurant already exists");
        }

        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .restaurantPicture(request.getRestaurantPicture())
                .restaurantType(request.getRestaurantType())
                .openingTime(request.getOpeningTime())
                .closingTime(request.getClosingTime())
                .build();

        restaurantRepository.save(restaurant);
    }

    public void update(UUID id, UpdateRestaurantRequest request) {
        Restaurant restaurant = getById(id);

        restaurant.setName(request.getName());
        restaurant.setAddress(request.getAddress());
        restaurant.setPhoneNumber(request.getPhoneNumber());
        restaurant.setOpeningTime(request.getOpeningTime());
        restaurant.setClosingTime(request.getClosingTime());
        restaurant.setRestaurantType(request.getRestaurantType());

        restaurantRepository.save(restaurant);
    }

    public void delete(UUID id) {

        Restaurant restaurant = getById(id);

        restaurantRepository.delete(restaurant);
    }
}
