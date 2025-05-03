package com.checkoutkata.controller;

import com.checkoutkata.domain.CartItem;
import com.checkoutkata.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
/**
 * CartController handles requests related to the shopping cart.
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Adds an item to the cart by scanning its ID.
     *
     * @param id the ID of the item to be added
     * @return the added CartItem
     */
    @PostMapping("scan/{id}")
    public ResponseEntity<CartItem> scanItem(@PathVariable Long id) {
        CartItem cartItem = cartService.addToCart(id);
        return ResponseEntity.ok(cartItem);
    }

    /**
     * Retrieves all items currently in the shopping cart.
     *
     * @return ResponseEntity containing a list of CartItems in the cart
     * @see CartItem
     * @see CartService#getCartContents()
     */
    @GetMapping("/contents")
    public ResponseEntity<List<CartItem>> getCartContents() {
        List<CartItem> contents = cartService.getCartContents();
        return ResponseEntity.ok(contents);
    }

    /**
     * Retrieves the total price of the items in the cart.
     *
     * @return a map containing the total price
     */
    @GetMapping("/total")
    public ResponseEntity<Map<String, BigDecimal>> getCartTotal() {
        return ResponseEntity.ok(Map.of("total", cartService.calculateTotal()));
    }

    /**
     * Clears the cart.
     *
     * @return a ResponseEntity with no content
     */
    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a specific item from the cart.
     *
     * @param id the ID of the item to be deleted
     * @return a ResponseEntity with no content
     */
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
        cartService.deleteCartItem(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a specific item from the cart by reducing its quantity.
     *
     * @param id the ID of the item to be reduced
     * @param quantity the quantity to reduce
     * @return the updated CartItem or no content if not found
     */
    @PutMapping("/items/{id}/decrease")
    public ResponseEntity<?> deleteCartItemByQuantity(@PathVariable Long id, @RequestParam int quantity) {
        CartItem result = cartService.deleteCartItemByQuantity(id, quantity);
        return result == null ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(result);
    }

}
