package com.checkoutkata.service;

import com.checkoutkata.domain.CartItem;
import com.checkoutkata.domain.Item;
import com.checkoutkata.dto.CartItemResponse;
import com.checkoutkata.dto.CartTotalResponse;
import com.checkoutkata.repository.CartItemRepository;
import com.checkoutkata.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(ItemRepository itemRepository, CartItemRepository cartItemRepository) {
        this.itemRepository = itemRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public CartItemResponse scanItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found, ID: " + itemId));

        CartItem cartItem = cartItemRepository.findByItemId(itemId)
                .map(existingItem -> {
                    existingItem.setQuantity(existingItem.getQuantity() + 1);
                    return existingItem;
                })
                .orElseGet(() -> {
                    CartItem newCartItem = new CartItem();
                    newCartItem.setItem(item);
                    newCartItem.setQuantity(1);
                    return newCartItem;
                });

        CartItem savedItem = cartItemRepository.save(cartItem);

        return new CartItemResponse(
                item.getId(),
                item.getName(),
                item.getUnitPrice(),
                savedItem.getQuantity()
        );

    }

    public List<CartItemResponse> getCartContents() {
        return cartItemRepository.findAll().stream()
                .map(cartItem -> {
                    Item item = cartItem.getItem();
                    return new CartItemResponse(
                            item.getId(),
                            item.getName(),
                            item.getUnitPrice(),
                            cartItem.getQuantity()
                    );
                })
                .toList();

    }

    public CartTotalResponse calculateTotal() {
        List<CartItem> cartItems = cartItemRepository.findAll();
        int total = 0;

        for (CartItem cartItem : cartItems) {
            int unitPrice = cartItem.getItem().getUnitPrice();
            int quantity = cartItem.getQuantity();

            // TODO: apply offers
            total += unitPrice * quantity;
        }

        return new CartTotalResponse(total);
    }


}
