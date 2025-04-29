package com.example.we_tried.deliverer;

import com.example.we_tried.deliverer.model.Deliverer;
import com.example.we_tried.deliverer.service.DelivererService;
import com.example.we_tried.order.model.FoodOrder;
import com.example.we_tried.order.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/deliveries")
public class DelivererController {

    private final DelivererService delivererService;

    @Autowired
    public DelivererController(DelivererService delivererService) {
        this.delivererService = delivererService;
    }

    @GetMapping("/{delivererId}/profile")
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

    @DeleteMapping("/{orderId}/delete")
    public String deleteOrder(@PathVariable UUID orderId) {
        delivererService.deleteOrderById(orderId);
        return "redirect:/deliveries/{delivererId}/my-orders";
    }




}
