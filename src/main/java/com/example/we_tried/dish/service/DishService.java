package com.example.we_tried.dish.service;

import com.example.we_tried.dish.CreateDishRequest;
import com.example.we_tried.dish.UpdateDishRequest;
import com.example.we_tried.dish.model.Dish;
import com.example.we_tried.dish.repository.DishRepository;
import com.example.we_tried.restaurant.model.Restaurant;
import com.example.we_tried.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DishService {

    private static final String UPLOAD_DIR = "src/main/resources/static/img/";

    private final DishRepository dishRepository;
    private final RestaurantRepository restaurantRepository;

    public void createDish(CreateDishRequest createDishRequest, UUID restaurantId, String imagePath) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        Dish dish = Dish.builder()
                .name(createDishRequest.getName())
                .description(createDishRequest.getDescription())
                .price(createDishRequest.getPrice())
                .dishType(createDishRequest.getDishType())
                .dishImage(imagePath)
                .restaurant(restaurant)
                .build();

        dishRepository.save(dish);
    }

    public String handleImageUpload(MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            String imageName = image.getOriginalFilename();

            Path path = Paths.get(UPLOAD_DIR + imageName);
            Files.createDirectories(path.getParent());
            image.transferTo(path);

            return "/img/" + imageName;
        }
        return null;
    }

    public void updateDish(UUID dishId, UpdateDishRequest request, String imagePath) {
        Dish dish = dishRepository.findById(dishId).orElseThrow(() -> new IllegalArgumentException("Dish not found"));

        dish.setName(request.getName());
        dish.setDescription(request.getDescription());
        dish.setPrice(request.getPrice());
        dish.setDishType(request.getDishType());

        if (imagePath != null) {
            dish.setDishImage(imagePath);
        }

        dishRepository.save(dish);
    }


    public Dish getById(UUID dishId) {
        return dishRepository.findById(dishId).orElseThrow(() -> new RuntimeException("Dish not found"));
    }

    public void deleteDish(UUID dishId) {

        dishRepository.deleteById(dishId);
    }
}
