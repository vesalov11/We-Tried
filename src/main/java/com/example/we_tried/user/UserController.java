package com.example.we_tried.user;

import com.example.we_tried.order.model.FoodOrder;
import com.example.we_tried.order.repository.OrderRepository;
import com.example.we_tried.security.AuthenticationMetaData;
import com.example.we_tried.user.model.User;
import com.example.we_tried.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final OrderRepository orderRepository;

    @Autowired
    public UserController(UserService userService, OrderRepository orderRepository) {
        this.userService = userService;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/profile")
    public ModelAndView getUserProfile(@AuthenticationPrincipal AuthenticationMetaData authenticationMetaData) {

        User user = userService.getById(authenticationMetaData.getId());
        List<FoodOrder> orders = orderRepository.findAllByOwnerId(authenticationMetaData.getId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile-user");
        modelAndView.addObject("user", user);
        modelAndView.addObject("orders", orders);

        return modelAndView;
    }
}
