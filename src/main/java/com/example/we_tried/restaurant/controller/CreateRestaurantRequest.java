package com.example.we_tried.restaurant.controller;

import com.example.we_tried.restaurant.model.RestaurantType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalTime;

@Data
public class CreateRestaurantRequest {

    @NotBlank(message = "Restaurant name cannot be empty!")
    @Size(min = 3, max = 20, message = "Restaurant name must be between 5 and 20 symbols!")
    private String name;

    @NotBlank(message = "Restaurant address cannot be empty!")
    @Size(min = 3, max = 20, message = "Restaurant name must be between 10 and 50 symbols!")
    private String address;

    @NotBlank(message = "Restaurant number cannot be empty!")
    private String phoneNumber;

    private LocalTime openingTime;

    private LocalTime closingTime;

    @NotNull(message = "Restaurant type cannot be empty!")
    private RestaurantType restaurantType;

    private String restaurantPicture;

}
