package com.checkoutkata.service;

import com.checkoutkata.domain.CartItem;
import com.checkoutkata.domain.Item;
import com.checkoutkata.domain.Offer;
import com.checkoutkata.repository.CartItemRepository;
import com.checkoutkata.repository.ItemRepository;
import com.checkoutkata.repository.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private OfferRepository offerRepository;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService(itemRepository, cartItemRepository, offerRepository);
    }

    @Test
    void shouldAddNewItemToCart() {
        Item item = new Item("Apple", 50);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartItemRepository.findByItemId(1L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any())).thenReturn(new CartItem(item, 1));

        CartItem result = cartService.addToCart(1L);

        assertThat(result.getQuantity()).isEqualTo(1);
        assertThat(result.getItem()).isEqualTo(item);
    }

    @Test
    void shouldCalculateTotalWithOffer() {
        Item item = new Item("Apple", 50);
        CartItem cartItem = new CartItem(item, 3);
        Offer offer = new Offer(item, 2, 80);

        when(cartItemRepository.findAll()).thenReturn(List.of(cartItem));
        when(offerRepository.findByItem(item)).thenReturn(Optional.of(offer));

        int total = cartService.calculateTotal();

        assertThat(total).isEqualTo(130);
    }

    @Test
    void shouldGetCartContents() {
        List<CartItem> items = List.of(
                new CartItem(new Item("Apple", 50), 1),
                new CartItem(new Item("Banana", 30), 2)
        );
        when(cartItemRepository.findAll()).thenReturn(items);

        List<CartItem> result = cartService.getCartContents();

        assertThat(result).hasSize(2);
        verify(cartItemRepository).findAll();
    }

    @Test
    void shouldThrowExceptionForNonExistentItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                cartService.addToCart(1L)
        );
    }

    @Test
    void shouldClearCart() {
        cartService.clearCart();

        verify(cartItemRepository).deleteAll();
    }

}
