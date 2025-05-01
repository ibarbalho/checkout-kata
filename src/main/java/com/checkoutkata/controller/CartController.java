package com.checkoutkata.controller;

import com.checkoutkata.domain.CartItem;
import com.checkoutkata.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {


    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("scan/{id}")
    public ResponseEntity<CartItem> scanItem(@PathVariable Long id) {
        CartItem cartItem = cartService.addToCart(id);
        return ResponseEntity.ok(cartItem);
    }

    @GetMapping("/contents")
    public ResponseEntity<List<CartItem>> getCartContents() {
        List<CartItem> contents = cartService.getCartContents();
        return ResponseEntity.ok(contents);
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, Integer>> getCartTotal() {
        return ResponseEntity.ok(Map.of("total", cartService.calculateTotal()));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
        cartService.deleteCartItem(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/items/{id}/decrease")
    public ResponseEntity<?> deleteCartItemByQuantity(@PathVariable Long id, @RequestParam int quantity) {
        CartItem result = cartService.deleteCartItemByQuantity(id, quantity);
        return result == null ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(result);
    }

}
