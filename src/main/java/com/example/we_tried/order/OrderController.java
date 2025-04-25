package com.example.we_tried.order;

import com.example.we_tried.order.model.FoodOrder;
import com.example.we_tried.order.service.OrderService;
import com.example.we_tried.security.AuthenticationMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/all-orders")
    public ModelAndView getAllOrders() {

        List<FoodOrder> allOrders = orderService.getAllOrders();

        ModelAndView modelAndView = new ModelAndView("all-orders");
        modelAndView.addObject("allOrders", allOrders);

        return modelAndView;
    }

    @GetMapping("/deliverer-orders")
    public ModelAndView getDelivererOrders(@AuthenticationPrincipal AuthenticationMetaData authenticationMetaData) {

        UUID delivererId = authenticationMetaData.getId();
        List<FoodOrder> delivererOrders = orderService.getDelivererOrders(delivererId);

        ModelAndView modelAndView = new ModelAndView("deliverer-orders");
        modelAndView.addObject("delivererOrders", delivererOrders);

        return modelAndView;
    }
}

