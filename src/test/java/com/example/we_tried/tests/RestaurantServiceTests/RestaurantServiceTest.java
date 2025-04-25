package com.example.we_tried.tests.RestaurantServiceTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.we_tried.restaurant.controller.CreateRestaurantRequest;
import com.example.we_tried.restaurant.controller.UpdateRestaurantRequest;
import com.example.we_tried.restaurant.model.Restaurant;
import com.example.we_tried.restaurant.model.RestaurantType;
import com.example.we_tried.restaurant.repository.RestaurantRepository;
import com.example.we_tried.restaurant.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private UUID restaurantId;
    private Restaurant restaurant;
    private CreateRestaurantRequest createRequest;
    private UpdateRestaurantRequest updateRequest;

    @BeforeEach
    void setUp() {
        restaurantId = UUID.randomUUID();
        restaurant = Restaurant.builder()
                .id(restaurantId)
                .name("Restaurant Bulgarian House")
                .address("ul.Ivan Mihailov N45")
                .phoneNumber("0888123456")
                .restaurantType(RestaurantType.BULGARIAN)
                .openingTime(LocalTime.of(9, 0))
                .closingTime(LocalTime.of(23, 0))
                .build();

        createRequest = new CreateRestaurantRequest();
        createRequest.setName("Restaurant Rodopi");
        createRequest.setAddress("ul Vitoshka N23");
        createRequest.setPhoneNumber("0877654321");
        createRequest.setRestaurantType(RestaurantType.BULGARIAN);
        createRequest.setOpeningTime(LocalTime.of(10, 0));
        createRequest.setClosingTime(LocalTime.of(22, 0));

        updateRequest = new UpdateRestaurantRequest();
        updateRequest.setName("Restaurant Chernomorets");
        updateRequest.setAddress("ul.Sozopol N7");
        updateRequest.setPhoneNumber("0899555666");
        updateRequest.setRestaurantType(RestaurantType.BULGARIAN);
        updateRequest.setOpeningTime(LocalTime.of(8, 0));
        updateRequest.setClosingTime(LocalTime.of(20, 0));
    }

    @Test
    void getAll_shouldReturnAllRestaurants() {
        when(restaurantRepository.findAll()).thenReturn(List.of(restaurant));

        List<Restaurant> result = restaurantService.getAll();

        assertEquals(1, result.size());
        assertEquals(restaurant, result.get(0));
    }

    @Test
    void getById_shouldReturnRestaurant() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        Restaurant result = restaurantService.getById(restaurantId);

        assertEquals(restaurant, result);
    }

    @Test
    void getById_shouldThrowExceptionWhenNotFound() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> restaurantService.getById(restaurantId));
    }

    @Test
    void getByType_shouldReturnRestaurantsOfType() {
        when(restaurantRepository.findByType(RestaurantType.BULGARIAN)).thenReturn(List.of(restaurant));

        List<Restaurant> result = restaurantService.getByType(RestaurantType.BULGARIAN);

        assertEquals(1, result.size());
        assertEquals(restaurant, result.get(0));
    }

    @Test
    void searchByName_shouldReturnMatchingRestaurants() {
        when(restaurantRepository.findByNameContainingIgnoreCase("bulgarian")).thenReturn(List.of(restaurant));

        List<Restaurant> result = restaurantService.searchByName("bulgarian");

        assertEquals(1, result.size());
        assertEquals(restaurant, result.get(0));
    }

    @Test
    void getByIdWithDishes_shouldReturnRestaurantWithDishes() {
        when(restaurantRepository.findByIdWithDishes(restaurantId)).thenReturn(Optional.of(restaurant));

        Restaurant result = restaurantService.getByIdWithDishes(restaurantId);

        assertEquals(restaurant, result);
    }

    @Test
    void getByIdWithDishes_shouldThrowExceptionWhenNotFound() {
        when(restaurantRepository.findByIdWithDishes(restaurantId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> restaurantService.getByIdWithDishes(restaurantId));
    }

    @Test
    void create_shouldSaveNewRestaurant() {
        when(restaurantRepository.findByName("Restaurant Rodopi")).thenReturn(Optional.empty());
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        restaurantService.create(createRequest);

        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void create_shouldThrowExceptionWhenRestaurantExists() {
        when(restaurantRepository.findByName("Restaurant Rodopi")).thenReturn(Optional.of(restaurant));

        assertThrows(RuntimeException.class, () -> restaurantService.create(createRequest));
    }

    @Test
    void update_shouldUpdateExistingRestaurant() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        restaurantService.update(restaurantId, updateRequest);

        assertEquals("Restaurant Chernomorets", restaurant.getName());
        assertEquals("ul.Sozopol N7", restaurant.getAddress());
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void delete_shouldRemoveRestaurant() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        restaurantService.delete(restaurantId);

        verify(restaurantRepository).delete(restaurant);
    }
}