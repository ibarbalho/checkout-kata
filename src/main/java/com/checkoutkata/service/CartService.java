package com.checkoutkata.service;

import com.checkoutkata.domain.CartItem;
import com.checkoutkata.domain.Item;
import com.checkoutkata.domain.Offer;
import com.checkoutkata.repository.CartItemRepository;
import com.checkoutkata.repository.ItemRepository;
import com.checkoutkata.repository.OfferRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final OfferRepository offerRepository;

    public CartService(ItemRepository itemRepository, CartItemRepository cartItemRepository, OfferRepository offerRepository) {
        this.itemRepository = itemRepository;
        this.cartItemRepository = cartItemRepository;
        this.offerRepository = offerRepository;
    }

    public CartItem addToCart(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        return cartItemRepository.findByItemId(itemId)
                .map(this::incrementQuantity)
                .orElseGet(() -> createNewCartItem(item));
    }

    public List<CartItem> getCartContents() {
        return cartItemRepository.findAll();
    }

    public int calculateTotal() {
        return cartItemRepository.findAll().stream()
                .mapToInt(this::calculateItemTotal)
                .sum();
    }

    private CartItem incrementQuantity(CartItem item) {
        item.setQuantity(item.getQuantity() + 1);
        return cartItemRepository.save(item);
    }

    private CartItem createNewCartItem(Item item) {
        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(1);
        return cartItemRepository.save(cartItem);
    }

    private int calculateItemTotal(CartItem cartItem) {
        Item item = cartItem.getItem();
        int quantity = cartItem.getQuantity();

        return offerRepository.findByItem(item)
                .map(offer -> calculateWithOffer(offer, quantity, item.getUnitPrice()))
                .orElseGet(() -> quantity * item.getUnitPrice());
    }

    private int calculateWithOffer(Offer offer, int quantity, int unitPrice) {
        int offerGroups = quantity / offer.getQuantity();
        int remainder = quantity % offer.getQuantity();
        return (offerGroups * offer.getTotalPrice()) + (remainder * unitPrice);
    }

    public void clearCart() {
        cartItemRepository.deleteAll();
    }

}
