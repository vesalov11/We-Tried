package com.example.we_tried.cart;

import com.example.we_tried.cart.model.Cart;
import com.example.we_tried.cart.service.CartService;
import com.example.we_tried.user.model.User;
import com.example.we_tried.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @Autowired
    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("{userId}")
    public ModelAndView getShoppingCart(@PathVariable UUID userId) {

        User user = userService.getById(userId);
        Cart cart = cartService.getOrCreateCart(user);

        ModelAndView model = new ModelAndView("shopping-cart");
        model.addObject("cart", cart);
        model.addObject("orders", cart.getOrders());
        model.addObject("totalPrice", cartService.getTotalPrice(cart));

        return model;
    }

    @PostMapping("/{userId}/add/{dishId}")
    public String addToCart(@PathVariable UUID dishId, @PathVariable UUID userId, @RequestParam(defaultValue = "1") int quantity) {

        User user = userService.getById(userId);

        cartService.addToCart(dishId, quantity, user);

        return "redirect:/cart/" + userId;
    }

    @PostMapping("/{userId}/checkout")
    public String checkout(
            @PathVariable UUID userId,
            @RequestParam String deliveryAddress,
            @RequestParam String paymentMethod) {

        User user = userService.getById(userId);
        cartService.checkout(user, deliveryAddress, paymentMethod);
        return "redirect:/cart/" + userId;
    }


}
