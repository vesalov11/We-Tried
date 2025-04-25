package com.example.we_tried.dish;

import com.example.we_tried.dish.model.DishType;
import com.example.we_tried.dish.service.DishService;
import com.example.we_tried.restaurant.model.Restaurant;
import com.example.we_tried.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.UUID;

@Controller
@RequestMapping("/dishes")

public class DishController {

    private final DishService dishService;
    private final RestaurantService restaurantService;

    @Autowired
    public DishController(DishService dishService, RestaurantService restaurantService) {
        this.dishService = dishService;
        this.restaurantService = restaurantService;
    }

    @GetMapping("/add/{restaurantId}")
    public ModelAndView createNewDish(@PathVariable UUID restaurantId) {
        ModelAndView modelAndView = new ModelAndView("add-dish");
        modelAndView.addObject("createDishRequest", new CreateDishRequest());
        modelAndView.addObject("dishTypes", DishType.values());
        modelAndView.addObject("restaurantId", restaurantId);
        return modelAndView;
    }

    @PostMapping("/add/{restaurantId}")
    public String submitNewDish(@PathVariable UUID restaurantId,
                                @Valid @ModelAttribute("createDishRequest") CreateDishRequest request,
                                BindingResult bindingResult) throws IOException {

        if (bindingResult.hasErrors()) {
            return "add-dish";
        }

        Restaurant restaurant = restaurantService.getById(restaurantId);
        dishService.createDish(request, restaurant);

        return "redirect:/restaurants/" + restaurantId;
    }

}
