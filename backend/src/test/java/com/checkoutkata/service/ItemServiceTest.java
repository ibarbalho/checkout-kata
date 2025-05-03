package com.checkoutkata.service;

import com.checkoutkata.domain.Item;
import com.checkoutkata.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemService(itemRepository);
    }

    @Test
    void createItem_ShouldSaveAndReturnItem() {
        Item item = new Item("Apple", BigDecimal.valueOf(50));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item result = itemService.createItem(item);

        assertThat(result).isEqualTo(item);
        verify(itemRepository).save(item);
    }

    @Test
    void updatePrice_ShouldUpdateAndReturnItem() {
        Item item = new Item("Apple", BigDecimal.valueOf(50));
        Item updatedItem = new Item("Apple", BigDecimal.valueOf(60));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        Item result = itemService.updatePrice(1L, BigDecimal.valueOf(60));

        assertThat(result.getUnitPrice()).isEqualTo(BigDecimal.valueOf(60));
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void getAllItems_ShouldReturnAllItems() {
        List<Item> items = List.of(
                new Item("Apple", BigDecimal.valueOf(50)),
                new Item("Orange", BigDecimal.valueOf(30))
        );
        when(itemRepository.findAll()).thenReturn(items);

        List<Item> result = itemService.getAllItems();

        assertThat(result).hasSize(2);
        verify(itemRepository).findAll();
    }

    @Test
    void deleteItemById_ShouldReturnTrueWhenSuccessful() {
        when(itemRepository.existsById(1L)).thenReturn(true);

        boolean result = itemService.deleteItemById(1L);

        assertThat(result).isTrue();
        verify(itemRepository).deleteById(1L);
    }

    @Test
    void updatePrice_ShouldThrowExceptionWhenItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                itemService.updatePrice(1L, BigDecimal.valueOf(60))
        );
    }
}
