package com.checkoutkata.controller;

import com.checkoutkata.dto.CartItemResponse;
import com.checkoutkata.dto.CartTotalResponse;
import com.checkoutkata.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("scan/{id}")
    public ResponseEntity<CartItemResponse> scanItem(@PathVariable("id") Long itemId){
        CartItemResponse response = cartService.scanItem(itemId);

        logger.info("Item '{}' scanned. Quantity in cart: {}",
                response.getItemName(), response.getQuantity());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/contents")
        public ResponseEntity<List<CartItemResponse>> getCartContents(){
        List<CartItemResponse> contents = cartService.getCartContents();

        logger.info("{} item(s) currently in cart", contents.size());

        return ResponseEntity.ok(contents);
    }

    @GetMapping("/total")
    public ResponseEntity<CartTotalResponse> getCartTotal() {
        CartTotalResponse response = cartService.calculateTotal();

        logger.info("Total (cents): {}", response.getTotal());

        return ResponseEntity.ok(response);
    }


}
