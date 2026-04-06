package com.stockwise.config;

import com.stockwise.item.Item;
import com.stockwise.item.ItemRepository;
import com.stockwise.user.User;
import com.stockwise.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * DataLoader
 *
 * Seeds initial application data at startup.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(ItemRepository itemRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedItems();
    }

    private void seedUsers() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(
                    new User("admin", passwordEncoder.encode("password123"), "ROLE_ADMIN")
            );
        }
    }

    private void seedItems() {
        if (itemRepository.count() > 0) {
            return;
        }

        itemRepository.save(new Item("Hammer", "SKU-001", 25, new BigDecimal("9.99")));
        itemRepository.save(new Item("Safety Gloves", "SKU-002", 100, new BigDecimal("4.50")));
        itemRepository.save(new Item("Measuring Tape", "SKU-003", 40, new BigDecimal("7.25")));
    }
}