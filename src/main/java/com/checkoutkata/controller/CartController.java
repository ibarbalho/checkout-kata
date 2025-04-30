package com.checkoutkata.controller;

import com.checkoutkata.dto.CartItemResponse;
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
        logger.info("Scanning item with ID: {}", itemId);
        CartItemResponse response = cartService.scanItem(itemId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contents")
        public ResponseEntity<List<CartItemResponse>> getCartContents(){
        List<CartItemResponse> contents = cartService.getCartContents();
        return ResponseEntity.ok(contents);
    }

}
