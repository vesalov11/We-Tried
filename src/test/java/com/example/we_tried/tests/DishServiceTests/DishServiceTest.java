package com.example.we_tried.tests.DishServiceTests;

import com.example.we_tried.dish.CreateDishRequest;
import com.example.we_tried.dish.UpdateDishRequest;
import com.example.we_tried.dish.model.Dish;
import com.example.we_tried.dish.repository.DishRepository;
import com.example.we_tried.dish.service.DishService;
import com.example.we_tried.restaurant.model.Restaurant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DishServiceTest {

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private DishService dishService;

    @Test
    public void testCreateDish() {
        CreateDishRequest createDishRequest = new CreateDishRequest();
        createDishRequest.setName("Pizza");
        createDishRequest.setDescription("Delicious cheese pizza");

        Restaurant restaurant = new Restaurant();
        restaurant.setId(UUID.randomUUID());

        String imagePath = "path/to/pizza-image.jpg";

        dishService.createDish(createDishRequest, restaurant, imagePath);

        verify(dishRepository, times(1)).save(any(Dish.class));
    }

    @Test
    public void testHandleImageUpload() throws IOException {
        MultipartFile mockImage = mock(MultipartFile.class);
        String imageName = "pizza-image.jpg";
        when(mockImage.getOriginalFilename()).thenReturn(imageName);
        when(mockImage.isEmpty()).thenReturn(false);

        String result = dishService.handleImageUpload(mockImage);

        assertEquals("/img/pizza-image.jpg", result);
        verify(mockImage, times(1)).transferTo(any(Path.class));
    }

    @Test
    public void testHandleImageUploadEmptyFile() throws IOException {
        MultipartFile mockImage = mock(MultipartFile.class);
        when(mockImage.isEmpty()).thenReturn(true);

        String result = dishService.handleImageUpload(mockImage);

        assertNull(result);
        verify(mockImage, never()).transferTo(any(Path.class));
    }

    @Test
    public void testUpdateDish() {
        UUID dishId = UUID.randomUUID();
        UpdateDishRequest updateRequest = new UpdateDishRequest();
        updateRequest.setName("Updated Pizza");
        updateRequest.setDescription("Updated cheese pizza");

        Dish existingDish = new Dish();
        existingDish.setId(dishId);
        existingDish.setName("Pizza");
        existingDish.setDescription("Delicious pizza");

        when(dishRepository.findById(dishId)).thenReturn(Optional.of(existingDish));

        String imagePath = "path/to/updated-pizza-image.jpg";

        dishService.updateDish(dishId, updateRequest, imagePath);

        assertEquals("Updated Pizza", existingDish.getName());
        assertEquals("Updated cheese pizza", existingDish.getDescription());
        assertEquals(null, existingDish.getPrice());
        assertEquals(null, existingDish.getDishType());
        assertEquals(imagePath, existingDish.getDishImage());
        verify(dishRepository, times(1)).save(existingDish);
    }

    @Test
    public void testGetById() {
        UUID dishId = UUID.randomUUID();
        Dish dish = new Dish();
        dish.setId(dishId);
        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));

        Dish result = dishService.getById(dishId);

        assertNotNull(result);
        assertEquals(dishId, result.getId());
    }

    @Test
    public void testGetByIdNotFound() {
        UUID dishId = UUID.randomUUID();
        when(dishRepository.findById(dishId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> dishService.getById(dishId));
    }

    @Test
    public void testDelete() {
        UUID dishId = UUID.randomUUID();
        Dish dish = new Dish();
        dish.setId(dishId);

        when(dishRepository.findById(dishId)).thenReturn(Optional.of(dish));

        dishService.delete(dishId);

        verify(dishRepository, times(1)).delete(dish);
    }

    @Test
    public void testDeleteNotFound() {
        UUID dishId = UUID.randomUUID();
        when(dishRepository.findById(dishId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> dishService.delete(dishId));
    }
}
