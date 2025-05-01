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
        Item apple = itemRepository.save(new Item("Apple", 30));    // €0.30
        Item banana = itemRepository.save(new Item("Banana", 50));   // €0.50
        Item peach = itemRepository.save(new Item("Peach", 60));
        Item kiwi = itemRepository.save(new Item("Kiwi", 20));

        // Offers
        offerRepository.save(new Offer(apple, 2, 45));   // 2 Apples for €0.45
        offerRepository.save(new Offer(banana, 3, 130)); // 3 Bananas for €1.30

    }
}
