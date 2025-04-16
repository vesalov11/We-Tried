package com.example.we_tried.deliverer;

import com.example.we_tried.deliverer.model.Deliverer;
import com.example.we_tried.deliverer.service.DelivererService;
import com.example.we_tried.order.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/deliveries")
public class DelivererController {

    private final DelivererService delivererService;

    @Autowired
    public DelivererController(DelivererService delivererService) {
        this.delivererService = delivererService;
    }

    @GetMapping("/profile/{delivererId}")
    public ModelAndView getDelivererProfile(@PathVariable UUID delivererId) {
        Deliverer deliverer = delivererService.getDelivererById(delivererId);
        List<Order> deliveredOrders = delivererService.getCompletedOrders(delivererId);
        BigDecimal monthlyRevenue = delivererService.calculateMonthlyRevenue(delivererId);
        boolean hasBonus = delivererService.isEligibleForBonus(delivererId);

        ModelAndView modelAndView = new ModelAndView("profile-delivery");
        modelAndView.addObject("deliverer", deliverer);
        modelAndView.addObject("deliveredOrders", deliveredOrders);
        modelAndView.addObject("monthlyRevenue", monthlyRevenue);
        modelAndView.addObject("hasBonus", hasBonus);

        return modelAndView;
    }

    @GetMapping("/all")
    public ModelAndView getAllOrders() {
        List<Order> allOrders = delivererService.getAllOrders();

        ModelAndView modelAndView = new ModelAndView("all-orders");
        modelAndView.addObject("orders", allOrders);

        return modelAndView;
    }

    @GetMapping("/my-orders/{delivererId}")
    public ModelAndView getMyOrders(@PathVariable UUID delivererId) {
        List<Order> delivererOrders = delivererService.getDelivererOrders(delivererId);

        ModelAndView modelAndView = new ModelAndView("my-orders");
        modelAndView.addObject("delivererOrders", delivererOrders);

        return modelAndView;
    }



}
