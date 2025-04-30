package com.checkoutkata.controller;

import com.checkoutkata.domain.Item;
import com.checkoutkata.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/items")
public class ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        Item created = itemService.createItem(item);
        logger.info("Created new item: {} with price: {}", created.getName(), created.getUnitPrice());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItemPrice(@PathVariable Long id, @RequestBody Item itemBody) {
        try {
            Item updated = itemService.updatePrice(id, itemBody.getUnitPrice());
            logger.info("Updated price for item: {}", updated.getName());
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException ex) {
            logger.warn("Item not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

}
