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

        CartItem cartItem = cartItemRepository.findByItemId(itemId)
                .map(this::incrementQuantity)
                .orElseGet(() -> createNewCartItem(item));

        logger.info("Added item to cart: {}, quantity: {}", item.getName(), cartItem.getQuantity());
        return cartItem;
    }

    public List<CartItem> getCartContents() {
        List<CartItem> contents = cartItemRepository.findAll();
        logger.info("Retrieved {} items from cart", contents.size());
        return contents;
    }

    public int calculateTotal() {
        int total = cartItemRepository.findAll().stream()
                .mapToInt(this::calculateItemTotal)
                .sum();
        logger.info("Calculated cart total: {}", total);
        return total;
    }

    private CartItem incrementQuantity(CartItem item) {
        item.setQuantity(item.getQuantity() + 1);
        CartItem updated = cartItemRepository.save(item);
        logger.info("Incremented quantity for item: {} to {}", item.getItem().getName(), updated.getQuantity());
        return updated;
    }

    private CartItem createNewCartItem(Item item) {
        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(1);
        CartItem saved = cartItemRepository.save(cartItem);
        logger.info("Created new cart item for: {}", item.getName());
        return saved;
    }

    private int calculateItemTotal(CartItem cartItem) {
        Item item = cartItem.getItem();
        int quantity = cartItem.getQuantity();
        int total = offerRepository.findByItem(item)
                .map(offer -> calculateWithOffer(offer, quantity, item.getUnitPrice()))
                .orElseGet(() -> quantity * item.getUnitPrice());

        logger.info("Calculated total for item: {}, quantity: {}, total: {}",
                item.getName(), quantity, total);
        return total;
    }

    private int calculateWithOffer(Offer offer, int quantity, int unitPrice) {
        int offerGroups = quantity / offer.getQuantity();
        int remainder = quantity % offer.getQuantity();
        int total = (offerGroups * offer.getTotalPrice()) + (remainder * unitPrice);

        logger.info("Applied offer: {} groups of {}, remainder: {}, total: {}",
                offerGroups, offer.getQuantity(), remainder, total);
        return total;
    }

    public void clearCart() {
        cartItemRepository.deleteAll();
        logger.info("Cart cleared");
    }

    public boolean deleteCartItem(Long itemId) {
        CartItem cartItem = cartItemRepository.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart: " + itemId));

        String itemName = cartItem.getItem().getName();
        cartItemRepository.delete(cartItem);
        logger.info("Removed item from cart: {}", itemName);
        return true;
    }

    public CartItem deleteCartItemByQuantity(Long itemId, int decreaseBy) {
        if (decreaseBy <= 0) {
            throw new IllegalArgumentException("Decrease amount must be positive");
        }

        CartItem cartItem = cartItemRepository.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart: " + itemId));

        int newQuantity = cartItem.getQuantity() - decreaseBy;

        if (newQuantity <= 0) {
            cartItemRepository.delete(cartItem);
            logger.info("Removed item from cart: {} (quantity reached zero)", cartItem.getItem().getName());
            return null;
        }

        cartItem.setQuantity(newQuantity);
        CartItem updatedItem = cartItemRepository.save(cartItem);
        logger.info("Decreased quantity for item: {} from {} to {}",
                cartItem.getItem().getName(), cartItem.getQuantity() + decreaseBy, newQuantity);
        return updatedItem;
    }

}
