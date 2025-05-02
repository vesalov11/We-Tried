package com.example.we_tried.restaurant.service;

import com.example.we_tried.restaurant.controller.CreateRestaurantRequest;
import com.example.we_tried.restaurant.controller.UpdateRestaurantRequest;
import com.example.we_tried.restaurant.model.Restaurant;
import com.example.we_tried.restaurant.model.RestaurantType;
import com.example.we_tried.restaurant.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@Service
public class RestaurantService {


    private static final String UPLOAD_DIR = "src/main/resources/static/img/";

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

    public void createRestaurant(CreateRestaurantRequest restaurantRequest, String imagePath) {

        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantRequest.getName());
        restaurant.setAddress(restaurantRequest.getAddress());
        restaurant.setPhoneNumber(restaurantRequest.getPhoneNumber());
        restaurant.setOpeningTime(restaurantRequest.getOpeningTime());
        restaurant.setClosingTime(restaurantRequest.getClosingTime());
        restaurant.setRestaurantType(restaurantRequest.getRestaurantType());
        restaurant.setRestaurantPicture(imagePath);

        restaurantRepository.save(restaurant);
    }

    public void updateRestaurant(UUID restaurantId, UpdateRestaurantRequest restaurantRequest, String imagePath) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RuntimeException("Restaurant not found"));

        restaurant.setName(restaurantRequest.getName());
        restaurant.setAddress(restaurantRequest.getAddress());
        restaurant.setPhoneNumber(restaurantRequest.getPhoneNumber());
        restaurant.setOpeningTime(restaurantRequest.getOpeningTime());
        restaurant.setClosingTime(restaurantRequest.getClosingTime());
        restaurant.setRestaurantType(restaurantRequest.getRestaurantType());

        if (imagePath != null) {
            restaurant.setRestaurantPicture(imagePath);
        }

        restaurantRepository.save(restaurant);
    }

    public void delete(UUID id) {

        Restaurant restaurant = getById(id);

        restaurantRepository.delete(restaurant);
    }
}
