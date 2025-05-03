package com.checkoutkata.service;

import com.checkoutkata.domain.Item;
import com.checkoutkata.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
/*
 * ItemService is responsible for managing items in the inventory.
 * It allows creating, updating, retrieving, and deleting items.
 */
@Service
@Transactional
public class ItemService {

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Updates an item's price.
     * @param id Item ID
     * @param newPrice New price to set
     * @return Updated item
     * @throws EntityNotFoundException if item not found
     * @throws IllegalArgumentException if id null or price negative
     */
    public Item updatePrice(Long id, BigDecimal newPrice) {
        validateId(id);
        validatePrice(newPrice);

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));

        item.setUnitPrice(newPrice);
        Item updatedItem = itemRepository.save(item);
        logger.info("Updated price for item: {} to: {}", updatedItem.getName(), newPrice);
        return updatedItem;
    }

    /**
     * Updates an item's name.
     * @param id Item ID
     * @param newName New name to set
     * @return Updated item
     * @throws EntityNotFoundException if item not found
     * @throws IllegalArgumentException if id null or name empty
     */
    public Item updateName(Long id, String newName) {
        validateId(id);
        validateName(newName);

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));

        item.setName(newName.trim());
        Item updatedItem = itemRepository.save(item);
        logger.info("Updated name for item with id: {} to: {}", id, item.getName());
        return updatedItem;
    }

    /**
     * Creates a new item in the inventory.
     * @param item Item to create
     * @return Created item with generated ID
     * @throws NullPointerException if item is null
     * @throws IllegalArgumentException if price negative
     */
    public Item createItem(Item item) {
        Objects.requireNonNull(item, "Item cannot be null");
        validatePrice(item.getUnitPrice());

        item.setId(null);
        Item savedItem = itemRepository.save(item);
        logger.info("Created new item: {} with price: {}", savedItem.getName(), savedItem.getUnitPrice());
        return savedItem;
    }

    /**
     * Retrieves all items in the inventory.
     * @return List of all items
     */
    public List<Item> getAllItems() {
        List<Item> items = itemRepository.findAll();
        logger.info("Found {} items", items.size());
        return items;
    }

    /**
     * Retrieves an item by its ID.
     * @param id Item ID
     * @return Optional containing the item if found, empty otherwise
     * @throws IllegalArgumentException if id null
     */
    public Optional<Item> getItemById(Long id) {
        validateId(id);
        logger.info("Fetching item with id: {}", id);
        return itemRepository.findById(id);
    }

    /**
     * Deletes an item by its ID.
     * @param id Item ID
     * @return true if item was deleted, false if not found
     * @throws IllegalArgumentException if id null
     */
    public boolean deleteItemById(Long id) {
        validateId(id);

        if (!itemRepository.existsById(id)) {
            logger.warn("Item not found with id: {}", id);
            return false;
        }

        itemRepository.deleteById(id);
        logger.info("Successfully deleted item with id: {}", id);
        return true;
    }

    /**
     * Validates item ID is not null.
     * @param id Item ID to validate
     * @throws IllegalArgumentException if null
     */
    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Item id cannot be null");
        }
    }

    /**
     * Validates item price is not negative.
     * @param price Price to validate
     * @throws IllegalArgumentException if negative
     */
    private void validatePrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    /**
     * Validates item name is not empty.
     * @param name Name to validate
     * @throws IllegalArgumentException if empty
     */
    private void validateName(String name) {
        Objects.requireNonNull(name, "Name cannot be null");
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }
}
