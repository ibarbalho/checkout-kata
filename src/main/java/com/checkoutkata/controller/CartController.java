package com.checkoutkata.controller;

import com.checkoutkata.domain.CartItem;
import com.checkoutkata.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("scan/{id}")
    public ResponseEntity<Map<String, Object>> scanItem(@PathVariable("id") Long itemId) {
        CartItem cartItem = cartService.addToCart(itemId);

        logger.info("Item '{}' scanned. Quantity in cart: {}",
                cartItem.getItem().getName(), cartItem.getQuantity());

        return ResponseEntity.ok(Map.of(
                "itemId", cartItem.getItem().getId(),
                "itemName", cartItem.getItem().getName(),
                "quantity", cartItem.getQuantity()
        ));
    }

    @GetMapping("/contents")
    public ResponseEntity<List<Map<String, Object>>> getCartContents() {
        List<Map<String, Object>> contents = cartService.getCartContents().stream()
                .map(item -> new HashMap<String, Object>() {{
                    put("itemId", item.getItem().getId());
                    put("itemName", item.getItem().getName());
                    put("quantity", item.getQuantity());
                    put("unitPrice", item.getItem().getUnitPrice());
                }})
                .collect(Collectors.toList());

        logger.info("{} item(s) currently in cart", contents.size());

        return ResponseEntity.ok(contents);
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, Integer>> getCartTotal() {
        int total = cartService.calculateTotal();

        logger.info("Total (cents): {}", total);

        return ResponseEntity.ok(Map.of("total", total));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> clearCart() {
        cartService.clearCart();

        logger.info("Cart cleared");

        return ResponseEntity.ok(Map.of("message", "Cart cleared"));
    }

}
