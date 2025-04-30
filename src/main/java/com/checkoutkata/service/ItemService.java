package com.checkoutkata.service;

import com.checkoutkata.domain.Item;
import com.checkoutkata.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item updatePrice(Long id, int newPrice) {
        if (id == null) {
            throw new IllegalArgumentException("Item id cannot be null");
        }
        if (newPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        return itemRepository.findById(id)
                .map(item -> {
                    item.setUnitPrice(newPrice);
                    return itemRepository.save(item);
                })
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));
    }

    public Item createItem(Item item) {
        Objects.requireNonNull(item, "Item cannot be null");
        if (item.getUnitPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        item.setId(null);
        return itemRepository.save(item);
    }
}
