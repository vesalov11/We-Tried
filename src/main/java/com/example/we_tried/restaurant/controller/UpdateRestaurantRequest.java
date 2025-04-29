package com.example.we_tried.restaurant.controller;

import com.example.we_tried.restaurant.model.RestaurantType;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalTime;

@Data
public class UpdateRestaurantRequest {

    @Size(min = 3, max = 20, message = "Restaurant name must be between 3 and 20 symbols!")
    private String name;

    @Size(min = 3, max = 20, message = "Restaurant address must be between 10 and 50 symbols!")
    private String address;

    private String phoneNumber;

    private LocalTime openingTime;

    private LocalTime closingTime;

    private RestaurantType restaurantType;

    private String restaurantPicture;
}
