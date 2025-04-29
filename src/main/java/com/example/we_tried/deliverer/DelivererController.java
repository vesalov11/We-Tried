package com.example.we_tried.deliverer;

import com.example.we_tried.deliverer.model.Deliverer;
import com.example.we_tried.deliverer.service.DelivererService;
import com.example.we_tried.order.model.FoodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
@PreAuthorize("hasRole('DELIVERER')")
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
        List<FoodOrder> deliveredOrders = delivererService.getCompletedOrders(delivererId);
        BigDecimal monthlyRevenue = delivererService.calculateMonthlyRevenue(delivererId);
        BigDecimal totalOrdersValue = delivererService.calculateTotalOrdersValue(delivererId);
        boolean hasBonus = delivererService.isEligibleForBonus(delivererId);

        ModelAndView modelAndView = new ModelAndView("profile-delivery");
        modelAndView.addObject("deliverer", deliverer);
        modelAndView.addObject("deliveredOrders", deliveredOrders);
        modelAndView.addObject("monthlyRevenue", monthlyRevenue);
        modelAndView.addObject("hasBonus", hasBonus);
        modelAndView.addObject("totalOrdersValue", totalOrdersValue);

        return modelAndView;
    }

    @PreAuthorize("hasRole('DELIVERER')")
    @GetMapping("/all")
    public ModelAndView getAllOrders() {
        List<FoodOrder> allOrders = delivererService.getAllOrders();

        ModelAndView modelAndView = new ModelAndView("all-orders");
        modelAndView.addObject("orders", allOrders);

        return modelAndView;
    }

    @GetMapping("/{delivererId}/my-orders")
    public ModelAndView getMyOrders(@PathVariable UUID delivererId) {
        List<FoodOrder> delivererOrders = delivererService.getDelivererOrders(delivererId);

        ModelAndView modelAndView = new ModelAndView("deliverer-orders");
        modelAndView.addObject("delivererOrders", delivererOrders);

        return modelAndView;
    }



}
