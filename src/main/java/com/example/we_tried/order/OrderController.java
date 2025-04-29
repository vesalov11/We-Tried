package com.example.we_tried.order;

import com.example.we_tried.deliverer.model.Deliverer;
import com.example.we_tried.deliverer.service.DelivererService;
import com.example.we_tried.order.model.FoodOrder;
import com.example.we_tried.order.service.OrderService;
import com.example.we_tried.security.AuthenticationMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final DelivererService delivererService;

    @Autowired
    public OrderController(OrderService orderService, DelivererService delivererService) {
        this.orderService = orderService;
        this.delivererService = delivererService;
    }



    @GetMapping("/all-orders")
    public ModelAndView getAllOrders() {

        List<FoodOrder> allOrders = orderService.getAllOrders();

        ModelAndView modelAndView = new ModelAndView("all-orders");
        modelAndView.addObject("allOrders", allOrders);

        return modelAndView;
    }

}

