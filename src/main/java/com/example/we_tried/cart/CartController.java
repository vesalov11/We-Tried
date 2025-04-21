package com.example.we_tried.cart;

import com.example.we_tried.cart.model.Cart;
import com.example.we_tried.cart.service.CartService;
import com.example.we_tried.security.AuthenticationMetaData;
import com.example.we_tried.user.model.User;
import com.example.we_tried.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ModelAndView getShoppingCart(@AuthenticationPrincipal AuthenticationMetaData authenticationMetaData) {

        Cart cart = cartService.getOrCreateCart(authenticationMetaData.getId());

        ModelAndView model = new ModelAndView("shopping-cart");
        model.addObject("cart", cart);
        model.addObject("orders", cart.getOrders());
        model.addObject("totalPrice", cartService.getTotalPrice(cart));

        return model;
    }

    @PostMapping("/add/{dishId}")
    public String addToCart(@PathVariable UUID dishId, @AuthenticationPrincipal AuthenticationMetaData authenticationMetaData, @RequestParam(defaultValue = "1") int quantity) {

        cartService.addToCart(dishId, quantity, authenticationMetaData.getId());

        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(
            @AuthenticationPrincipal AuthenticationMetaData authenticationMetaData,
            @RequestParam String deliveryAddress,
            @RequestParam String paymentMethod) {

        cartService.checkout(authenticationMetaData.getId(), deliveryAddress, paymentMethod);
        return "redirect:/cart";
    }

    @PostMapping("/increase/{orderItemId}")
    public String increaseQuantity(@PathVariable UUID orderItemId) {
        cartService.increaseQuantity(orderItemId);
        return "redirect:/cart";
    }

    @PostMapping("/decrease/{orderItemId}")
    public String decreaseQuantity(@PathVariable UUID orderItemId) {
        cartService.decreaseQuantity(orderItemId);
        return "redirect:/cart";
    }


}
