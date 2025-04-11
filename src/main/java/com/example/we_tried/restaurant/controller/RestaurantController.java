package com.example.we_tried.restaurant.controller;
import org.springframework.ui.Model;
import com.example.we_tried.restaurant.model.Restaurant;
import com.example.we_tried.restaurant.service.RestaurantServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {

}
