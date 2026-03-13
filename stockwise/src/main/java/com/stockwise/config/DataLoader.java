package com.stockwise.config;

import com.stockwise.item.Item;
import com.stockwise.item.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * DataLoader
 *
 * I added this so the app has some data immediately after startup.
 * Since H2 is in-memory right now, it resets every time the app restarts.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final ItemRepository itemRepository;

    public DataLoader(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) {
        // I only want to seed when the table is empty.
        if (itemRepository.count() > 0) {
            return;
        }

        itemRepository.save(new Item("Hammer", "SKU-001", 25, new BigDecimal("9.99")));
        itemRepository.save(new Item("Safety Gloves", "SKU-002", 100, new BigDecimal("4.50")));
        itemRepository.save(new Item("Measuring Tape", "SKU-003", 40, new BigDecimal("7.25")));
    }
}