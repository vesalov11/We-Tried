package com.example.we_tried.tests.lomboktest;

import com.example.we_tried.cart.model.Cart;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;

public class CartTest {
    @Test
    public void testCartGettersAndSetters() {
        UUID testId = UUID.randomUUID();
        BigDecimal testPrice = new BigDecimal("100.50");

        Cart cart = Cart.builder()
                .id(testId)
                .totalPrice(testPrice)
                .build();

        assertNotNull(cart);
        assertEquals(testId, cart.getId());
        assertEquals(testPrice, cart.getTotalPrice());
    }
}
