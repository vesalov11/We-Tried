package com.example.we_tried.deliverer;

import com.example.we_tried.deliverer.service.DelivererService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.UUID;

@Controller
@RequestMapping("/deliveries")
public class DelivererController {

    private final DelivererService delivererService;

    @Autowired
    public DelivererController(DelivererService delivererService) {
        this.delivererService = delivererService;
    }


    @DeleteMapping("/orders/{orderId}/delete")
    public String deleteOrder(@PathVariable UUID orderId) {

        delivererService.deleteOrderById(orderId);

        return "redirect:/deliverer-orders";
    }





}
