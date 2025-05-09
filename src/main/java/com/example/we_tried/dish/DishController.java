package com.example.we_tried.dish;

import com.example.we_tried.dish.model.Dish;
import com.example.we_tried.dish.model.DishType;
import com.example.we_tried.dish.service.DishService;
import com.example.we_tried.restaurant.model.Restaurant;
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

    @GetMapping("/{restaurantId}/add")
    public ModelAndView createNewDish(@PathVariable UUID restaurantId) {
        ModelAndView modelAndView = new ModelAndView("add-dish");
        modelAndView.addObject("createDishRequest", new CreateDishRequest());
        modelAndView.addObject("dishTypes", DishType.values());
        modelAndView.addObject("restaurantId", restaurantId);
        return modelAndView;
    }

    @PostMapping("/{restaurantId}/add")
    public ModelAndView submitNewDish(@PathVariable UUID restaurantId,
                                      @Valid @ModelAttribute("createDishRequest") CreateDishRequest request,
                                      BindingResult bindingResult,
                                      @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {

        ModelAndView mv = new ModelAndView();

        if (bindingResult.hasErrors()) {
            mv.setViewName("add-dish");
            mv.addObject("createDishRequest", request);
            mv.addObject("dishTypes", DishType.values());
            mv.addObject("restaurantId", restaurantId);
        }

        String imagePath = null;
        if (!image.isEmpty()) {
            imagePath = dishService.handleImageUpload(image);
        }

        Restaurant restaurant = restaurantService.getById(restaurantId);
        dishService.createDish(request, restaurant, imagePath);

        mv.setViewName("redirect:/restaurants/" + restaurantId);
        return mv;
    }

    @GetMapping("/{dishId}/update")
    public ModelAndView updateDish(@PathVariable UUID dishId) {
        Dish dish = dishService.getById(dishId);
        ModelAndView modelAndView = new ModelAndView("update-dish");
        modelAndView.addObject("dish", dish);
        modelAndView.addObject("updateDishRequest", new UpdateDishRequest());
        modelAndView.addObject("dishTypes", DishType.values());
        return modelAndView;
    }

    @PostMapping("/{dishId}/update")
    public ModelAndView submitUpdateDish(@PathVariable UUID dishId,
                                         @Valid @ModelAttribute("updateDishRequest") UpdateDishRequest request,
                                         @RequestParam(value = "image", required = false) MultipartFile image,
                                         BindingResult bindingResult) throws IOException {

        ModelAndView mv = new ModelAndView();

        if (bindingResult.hasErrors()) {
            Dish dish = dishService.getById(dishId);
            mv.setViewName("update-dish");
            mv.addObject("dish", dish);
            mv.addObject("dishTypes", DishType.values());
            mv.addObject("updateDishRequest", request);
            return mv;
        }

        String imagePath = null;
        if (!image.isEmpty()) {
            imagePath = dishService.handleImageUpload(image);
        }

        dishService.updateDish(dishId, request, imagePath);
        mv.setViewName("redirect:/restaurants");
        return mv;
    }

    @DeleteMapping("/{dishId}/delete")
    public String deleteDish(@PathVariable UUID dishId) {

        dishService.delete(dishId);

        return "redirect:/restaurants";
    }

}
