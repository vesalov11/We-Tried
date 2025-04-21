package com.example.we_tried.dish.service;

import com.example.we_tried.dish.CreateDishRequest;
import com.example.we_tried.dish.model.Dish;
import com.example.we_tried.dish.repository.DishRepository;
import com.example.we_tried.restaurant.model.Restaurant;
import com.example.we_tried.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DishService {

    private final DishRepository dishRepository;
    private final RestaurantRepository restaurantRepository;

    public void createDish(CreateDishRequest createDishRequest, Restaurant restaurant) throws IOException {
        String imageName = storeImage(createDishRequest.getDishImage());

        Dish dish = Dish.builder()
                .name(createDishRequest.getName())
                .description(createDishRequest.getDescription())
                .price(createDishRequest.getPrice())
                .dishType(createDishRequest.getDishType())
                .dishImage(imageName)
                .restaurant(restaurant)
                .build();

        dishRepository.save(dish);
    }

    private String storeImage(MultipartFile image) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
        Path uploadPath = Paths.get("src/main/resources/static/images");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = image.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return fileName;
    }
}
