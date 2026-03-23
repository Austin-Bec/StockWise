package com.stockwise.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findBySku(String sku);

    boolean existsBySku(String sku);

    List<Item> findByQuantityOnHandLessThan(Integer threshold);

    List<Item> findByActiveTrue();

    @Query("SELECT i FROM Item i WHERE i.active = true AND i.quantityOnHand <= i.reorderThreshold")
    List<Item> findLowStockItems();
}