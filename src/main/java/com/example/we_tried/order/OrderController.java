package com.example.we_tried.order;

import com.example.we_tried.deliverer.model.Deliverer;
import com.example.we_tried.deliverer.repository.DelivererRepository;
import com.example.we_tried.deliverer.service.DelivererService;
import com.example.we_tried.order.model.FoodOrder;
import com.example.we_tried.order.service.OrderService;
import com.example.we_tried.security.AuthenticationMetaData;
import com.example.we_tried.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthenticationMetaData userDetails = (AuthenticationMetaData) authentication.getPrincipal();

        UUID delivererId = userDetails.getId();

        List<FoodOrder> allOrders = orderService.getAllOrders();

        ModelAndView modelAndView = new ModelAndView("all-orders");
        modelAndView.addObject("allOrders", allOrders);
        modelAndView.addObject("delivererId", delivererId);

        return modelAndView;
    }

    @GetMapping("/deliverer-orders")
    public ModelAndView getDelivererOrders() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        AuthenticationMetaData userDetails = (AuthenticationMetaData) authentication.getPrincipal();

        UUID delivererId = userDetails.getId();

        List<FoodOrder> delivererOrders = orderService.getDelivererOrders(delivererId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("deliverer-orders");
        modelAndView.addObject("delivererOrders", delivererOrders);
        modelAndView.addObject("delivererId", delivererId);

        return modelAndView;
    }


    @PostMapping("/deliverer-orders/add")
    public String addOrdersToDeliverer(@RequestParam("selectedOrders") List<UUID> selectedOrderIds,
                                       @RequestParam("delivererId") UUID delivererId) {
        Deliverer deliverer = delivererService.getDelivererById(delivererId);

        for (UUID orderId : selectedOrderIds) {
            FoodOrder order = orderService.getOrderById(orderId);
            order.setDeliverer(deliverer);
            orderService.save(order);
        }

        return "redirect:/{delivererId}/deliverer-orders";
    }

}

