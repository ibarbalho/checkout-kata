package com.checkoutkata.controller;

import com.checkoutkata.domain.Item;
import com.checkoutkata.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/items")
public class ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Retrieves all items from the inventory.
     * @return ResponseEntity containing list of all items
     */
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    /**
     * Retrieves a specific item by its ID.
     * @param id ID of the item to retrieve
     * @return ResponseEntity with item if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new item in the inventory.
     * @param item Item to create
     * @return ResponseEntity with created item and 201 status
     */
    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        Item created = itemService.createItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Updates the price of an existing item.
     * @param id ID of the item to update
     * @param itemBody Item object containing new price
     * @return ResponseEntity with updated item, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItemPrice(@PathVariable Long id, @RequestBody Item itemBody) {
        try {
            Item updated = itemService.updatePrice(id, itemBody.getUnitPrice());
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Updates the name of an existing item.
     * @param id ID of the item to update
     * @param item Item object containing new name
     * @return ResponseEntity with updated item, or 404 if not found
     */
    @PutMapping("/{id}/name")
    public ResponseEntity<Item> updateItemName(@PathVariable Long id, @RequestBody Item item) {
        try {
            Item updated = itemService.updateName(id, item.getName());
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes an item from the inventory.
     * @param id ID of the item to delete
     * @return ResponseEntity with 204 if deleted, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (itemService.deleteItemById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
