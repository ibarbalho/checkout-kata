package com.checkoutkata.config;

import com.checkoutkata.controller.CartController;
import com.checkoutkata.domain.Item;
import com.checkoutkata.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final ItemRepository itemRepository;

    public DataLoader(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) {
        logger.info("Running DataLoader");
        itemRepository.save(new Item("Apple", 30));
        itemRepository.save(new Item("Banana", 50));
        itemRepository.save(new Item("Peach", 60));
        itemRepository.save(new Item("Kiwi", 20));
    }
}
