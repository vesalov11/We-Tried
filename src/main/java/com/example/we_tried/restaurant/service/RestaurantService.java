package com.example.we_tried.restaurant.service;

import com.example.we_tried.restaurant.controller.CreateRestaurantRequest;
import com.example.we_tried.restaurant.controller.UpdateRestaurantRequest;
import com.example.we_tried.restaurant.model.Restaurant;
import com.example.we_tried.restaurant.model.RestaurantType;
import com.example.we_tried.restaurant.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
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

    public List<Restaurant> getByType(RestaurantType type) {
        return restaurantRepository.findByRestaurantType(type);
    }

    public List<Restaurant> searchByName(String query) {
        return restaurantRepository.findByNameContainingIgnoreCase(query);
    }

    public Restaurant getByIdWithDishes(UUID id) {
        return restaurantRepository.findByIdWithDishes(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    public void create(CreateRestaurantRequest request) throws IOException {
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findByName(request.getName());
        if (optionalRestaurant.isPresent()) {
            throw new RuntimeException("Restaurant already exists");
        }

        String imageName = storeImage(request.getRestaurantPicture());

        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .restaurantPicture(imageName)
                .restaurantType(request.getRestaurantType())
                .openingTime(request.getOpeningTime())
                .closingTime(request.getClosingTime())
                .build();

        restaurantRepository.save(restaurant);
    }

    public String storeImage(MultipartFile image) throws IOException {
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
