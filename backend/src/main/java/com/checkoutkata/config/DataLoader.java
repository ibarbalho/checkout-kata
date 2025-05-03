package com.checkoutkata.config;

import com.checkoutkata.controller.CartController;
import com.checkoutkata.domain.Item;
import com.checkoutkata.domain.Offer;
import com.checkoutkata.repository.ItemRepository;
import com.checkoutkata.repository.OfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final ItemRepository itemRepository;
    private final OfferRepository offerRepository;

    public DataLoader(ItemRepository itemRepository, OfferRepository offerRepository1) {
        this.itemRepository = itemRepository;
        this.offerRepository = offerRepository1;
    }

    @Override
    public void run(String... args) {
        logger.info("Running DataLoader");

        // Items
        Item apple = itemRepository.save(new Item("Apple", BigDecimal.valueOf(0.30)));
        Item banana = itemRepository.save(new Item("Banana", BigDecimal.valueOf(0.50)));
        Item peach = itemRepository.save(new Item("Peach", BigDecimal.valueOf(0.60)));
        Item kiwi = itemRepository.save(new Item("Kiwi", BigDecimal.valueOf(0.20)));

        // Offers
        offerRepository.save(new Offer(apple, 2, BigDecimal.valueOf(0.45)));
        offerRepository.save(new Offer(banana, 3, BigDecimal.valueOf(1.30)));
    }
}
