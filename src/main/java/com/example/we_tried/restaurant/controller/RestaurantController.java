package com.example.we_tried.restaurant.controller;
import com.example.we_tried.dish.model.DishType;
import com.example.we_tried.restaurant.model.Restaurant;
import com.example.we_tried.restaurant.model.RestaurantType;
import com.example.we_tried.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public ModelAndView getAllRestaurants(
            @RequestParam(required = false) RestaurantType type,
            @RequestParam(required = false) String search
    ) {
        List<Restaurant> restaurants;

        if(type != null) {
            restaurants = restaurantService.getByType(type);
        } else if(search != null && !search.isEmpty()) {
            restaurants = restaurantService.searchByName(search);
        } else {
            restaurants = restaurantService.getAll();
        }

        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("restaurants", restaurants);
        modelAndView.addObject("restaurantTypes", RestaurantType.values());
        modelAndView.addObject("searchQuery", search);
        return modelAndView;
    }


    @GetMapping("/{restaurantId}")
    public ModelAndView getRestaurantDetails(@PathVariable UUID restaurantId) {

        Restaurant restaurant = restaurantService.getByIdWithDishes(restaurantId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("restaurant");
        modelAndView.addObject("restaurant", restaurant);
        modelAndView.addObject("dishTypes", DishType.values());
        return modelAndView;
    }

    @DeleteMapping("/delete/{restaurantId}")
    public String deleteRestaurant(@PathVariable UUID restaurantId) {

        restaurantService.delete(restaurantId);

        return "redirect:/restaurants";
    }

    @GetMapping("/new")
    public ModelAndView getNewRestaurantPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-restaurant");
        modelAndView.addObject("createRestaurantRequest", new CreateRestaurantRequest());
        modelAndView.addObject("restaurantType", RestaurantType.values());
        return modelAndView;
    }

    @PostMapping("/new")
    public String createRestaurant(@Valid @ModelAttribute("createRestaurantRequest") CreateRestaurantRequest restaurant,
                                   @RequestParam("image") MultipartFile image,
                                   BindingResult bindingResult) throws IOException {

        if (bindingResult.hasErrors()) {
            return "add-restaurant";
        }

        String imagePath = restaurantService.handleImageUpload(image);

        restaurantService.createRestaurant(restaurant, imagePath);

        return "redirect:/restaurants";
    }

    @GetMapping("/update/{restaurantId}")
    public ModelAndView getUpdateRestaurantPage(@PathVariable UUID restaurantId) {

        Restaurant restaurant = restaurantService.getById(restaurantId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("update-restaurant");
        modelAndView.addObject("restaurant", restaurant);
        modelAndView.addObject("updateRestaurantRequest", new UpdateRestaurantRequest());
        modelAndView.addObject("restaurantType", RestaurantType.values());

        return modelAndView;
    }

    @PostMapping("/update/{restaurantId}")
    public String updateRestaurant(@PathVariable UUID restaurantId,
                                   @Valid @ModelAttribute("updateRestaurantRequest") UpdateRestaurantRequest updateRestaurantRequest,
                                   @RequestParam(value = "image", required = false) MultipartFile image,
                                   BindingResult bindingResult) throws IOException {

        if (bindingResult.hasErrors()) {
            return "update-restaurant";
        }

        String imagePath = null;
        if (!image.isEmpty()) {
            imagePath = restaurantService.handleImageUpload(image);
        }

        restaurantService.updateRestaurant(restaurantId, updateRestaurantRequest);

        return "redirect:/restaurants";
    }

}