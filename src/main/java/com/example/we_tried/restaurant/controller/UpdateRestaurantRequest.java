package com.example.we_tried.restaurant.controller;

import com.example.we_tried.restaurant.model.RestaurantType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class UpdateRestaurantRequest {

    @NotBlank(message = "Restaurant name cannot be empty!")
    private String name;

    @NotBlank(message = "Restaurant address cannot be empty!")
    private String address;

    @NotBlank(message = "Restaurant number cannot be empty!")
    private String phoneNumber;

    private LocalTime openingTime;

    private LocalTime closingTime;

    @NotNull(message = "Restaurant type cannot be empty!")
    private RestaurantType restaurantType;

    private String restaurantPicture;
}
