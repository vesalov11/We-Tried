package com.example.we_tried.dish;

import com.example.we_tried.dish.model.DishType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateDishRequest {

    @NotBlank(message = "Dish name cannot be empty!")
    private String name;

    @NotBlank(message = "Dish description cannot be empty!")
    private String description;

    @NotNull(message = "Dish price cannot be empty!")
    @DecimalMin(value = "0.0", inclusive = false, message = "Dish price must be greater than 0!")
    private BigDecimal price;

    @NotNull(message = "Dish dish type cannot be empty!")
    private DishType dishType;

}
