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

import java.math.BigDecimal;
import java.util.List;

/*
 * CartService is responsible for managing the shopping cart.
 * It allows adding items, calculating totals, applying offers,
 * and managing cart contents.
 */
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

    /**
     * Adds an item to the cart. If the item already exists, increments its quantity.
     * @param itemId ID of the item to add
     * @return The updated or created cart item
     * @throws IllegalArgumentException if item not found
     */
    public CartItem addToCart(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        CartItem cartItem = cartItemRepository.findByItemId(itemId)
                .map(this::incrementQuantity)
                .orElseGet(() -> createNewCartItem(item));

        logger.info("Added item to cart: {}, quantity: {}", item.getName(), cartItem.getQuantity());
        return cartItem;
    }

    /**
     * Retrieves all items currently in the cart.
     * @return List of cart items
     */
    public List<CartItem> getCartContents() {
        List<CartItem> contents = cartItemRepository.findAll();
        logger.info("Retrieved {} items from cart", contents.size());
        return contents;
    }

    /**
     * Calculates the total price of all items in the cart, applying any available offers.
     * @return Total price as BigDecimal
     */
    public BigDecimal calculateTotal() {
        BigDecimal total = cartItemRepository.findAll().stream()
                .map(this::calculateItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.info("Calculated cart total: {}", total);
        return total;
    }

    /**
     * Increments the quantity of an existing cart item by 1.
     * @param item The cart item to update
     * @return The updated cart item after saving
     */
    private CartItem incrementQuantity(CartItem item) {
        item.setQuantity(item.getQuantity() + 1);
        CartItem updated = cartItemRepository.save(item);
        logger.info("Incremented quantity for item: {} to {}", item.getItem().getName(), updated.getQuantity());
        return updated;
    }

    /**
     * Creates a new cart item with quantity 1.
     * @param item The item to add to cart
     * @return The newly created and saved cart item
     */
    private CartItem createNewCartItem(Item item) {
        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setQuantity(1);
        CartItem saved = cartItemRepository.save(cartItem);
        logger.info("Created new cart item for: {}", item.getName());
        return saved;
    }

    /**
     * Calculates the total price for a cart item, applying any available offers.
     * @param cartItem The cart item to calculate total for
     * @return The total price for this item considering quantity and offers
     */
    private BigDecimal calculateItemTotal(CartItem cartItem) {
        Item item = cartItem.getItem();
        int quantity = cartItem.getQuantity();
        BigDecimal total = offerRepository.findByItem(item)
                .map(offer -> calculateWithOffer(offer, quantity, item.getUnitPrice()))
                .orElseGet(() -> item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));

        logger.info("Calculated total for item: {}, quantity: {}, total: {}",
                item.getName(), quantity, total);
        return total;
    }

    /**
     * Calculates the total price for items when an offer is applicable.
     * Handles partial offer applications by calculating both the offer price
     * for complete groups and regular price for remaining items.
     *
     * @param offer     The offer to apply (contains quantity and special price)
     * @param quantity  Total quantity of items
     * @param unitPrice Regular price per item
     * @return Total price after applying the offer
     */
    private BigDecimal calculateWithOffer(Offer offer, int quantity, BigDecimal unitPrice) {
        int offerGroups = quantity / offer.getQuantity();
        int remainder = quantity % offer.getQuantity();

        BigDecimal offerTotal = BigDecimal.valueOf(offerGroups)
                .multiply(offer.getTotalPrice());
        BigDecimal remainderTotal = BigDecimal.valueOf(remainder)
                .multiply(unitPrice);

        BigDecimal total = offerTotal.add(remainderTotal);

        logger.info("Applied offer: {} groups of {}, remainder: {}, total: {}",
                offerGroups, offer.getQuantity(), remainder, total);
        return total;
    }

    /**
     * Removes all items from the cart.
     */
    public void clearCart() {
        cartItemRepository.deleteAll();
        logger.info("Cart cleared");
    }

    /**
     * Completely removes an item from the cart regardless of quantity.
     * @param itemId ID of the item to remove
     * @throws IllegalArgumentException if item not found in cart
     */
    public void deleteCartItem(Long itemId) {
        CartItem cartItem = cartItemRepository.findByItemId(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart: " + itemId));

        String itemName = cartItem.getItem().getName();
        cartItemRepository.delete(cartItem);
        logger.info("Removed item from cart: {}", itemName);
    }

    /**
     * Decreases the quantity of an item in the cart by the specified amount.
     * Removes the item if quantity reaches zero.
     * @param itemId ID of the item
     * @param decreaseBy Amount to decrease
     * @return Updated cart item or null if item was removed
     * @throws IllegalArgumentException if item not found or decreaseBy <= 0
     */
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
