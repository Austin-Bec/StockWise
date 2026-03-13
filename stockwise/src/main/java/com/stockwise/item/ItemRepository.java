package com.stockwise.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for the Item entity.
 * Responsibility:
 * - Provide CRUD operations for items using Spring Data JPA.
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Find an item by its unique SKU.
     *
     * @param sku SKU string to search for.
     * @return Optional containing the item, if found.
     */
    Optional<Item> findBySku(String sku);
    boolean existsBySku(String sku);
}
