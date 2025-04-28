package com.example.we_tried.tests.DishServiceTests;
import com.example.we_tried.dish.CreateDishRequest;
import com.example.we_tried.dish.model.Dish;
import com.example.we_tried.dish.model.DishType;
import com.example.we_tried.dish.repository.DishRepository;
import com.example.we_tried.dish.service.DishService;
import com.example.we_tried.restaurant.model.Restaurant;
import com.example.we_tried.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DishServiceTest {

    @Mock private DishRepository dishRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @InjectMocks private DishService dishService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDish_shouldSaveDish() throws IOException {
        String fileName = "test.jpg";
        MockMultipartFile image = new MockMultipartFile("file", fileName, "image/jpeg", "dummy".getBytes());
        CreateDishRequest request = new CreateDishRequest();
        request.setName("Pizza");
        request.setDescription("Cheesy");
        request.setPrice(new BigDecimal("10.99"));
        request.setDishType(DishType.MAIN_COURSE);
        request.setDishImage(image);

        Restaurant restaurant = new Restaurant();

        DishService spyService = Mockito.spy(dishService);
        doReturn(fileName).when(spyService).handleImageUpload(any());

        spyService.createDish(request, restaurant, fileName);

        ArgumentCaptor<Dish> dishCaptor = ArgumentCaptor.forClass(Dish.class);
        verify(dishRepository).save(dishCaptor.capture());

        Dish savedDish = dishCaptor.getValue();
        assertEquals("Pizza", savedDish.getName());
        assertEquals("Cheesy", savedDish.getDescription());
        assertEquals(new BigDecimal("10.99"), savedDish.getPrice());
        assertEquals(DishType.MAIN_COURSE, savedDish.getDishType());
        assertEquals(fileName, savedDish.getDishImage());
        assertEquals(restaurant, savedDish.getRestaurant());
    }

    @Test
    void storeImage_shouldStoreAndReturnFileName() throws IOException {
        String fileName = "image.jpg";
        byte[] content = "test-image".getBytes();
        MockMultipartFile image = new MockMultipartFile("image", fileName, "image/jpeg", content);

        Path uploadPath = Paths.get("src/main/resources/static/images");
        Files.createDirectories(uploadPath);
        Files.deleteIfExists(uploadPath.resolve(fileName));

        String storedFileName = dishService.handleImageUpload(image);

        assertEquals(StringUtils.cleanPath(fileName), storedFileName);
        assert(Files.exists(uploadPath.resolve(storedFileName)));

        Files.deleteIfExists(uploadPath.resolve(storedFileName));
    }
}