package com.stockwise.item;

import com.stockwise.item.dto.ItemCreateRequest;
import com.stockwise.item.dto.ItemUpdateRequest;
import com.stockwise.item.exception.DuplicateSkuException;
import com.stockwise.item.exception.ItemNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service: ItemService
 *
 * Responsibility:
 * - Handle business logic for inventory items.
 * - Enforce validation and uniqueness rules.
 * - Coordinate repository access for item operations.
 */
@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Retrieve all items.
     */
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    /**
     * Retrieve all active items.
     */
    public List<Item> getActiveItems() {
        return itemRepository.findByActiveTrue();
    }

    /**
     * Retrieve all low-stock active items.
     */
    public List<Item> getLowStockItems() {
        return itemRepository.findLowStockItems();
    }

    /**
     * Count low-stock active items.
     */
    public long getLowStockItemCount() {
        return getLowStockItems().size();
    }

    /**
     * Calculate total inventory value for active items.
     */
    public BigDecimal getTotalInventoryValue() {
        return itemRepository.findByActiveTrue()
                .stream()
                .map(item -> item.getUnitCost().multiply(BigDecimal.valueOf(item.getQuantityOnHand())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Retrieve one required item by ID.
     */
    public Item getRequiredItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
    }

    /**
     * Retrieve one required item by SKU.
     */
    public Item getItemBySku(String sku) {
        return itemRepository.findBySku(sku)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with SKU: " + sku));
    }

    /**
     * Create a new item.
     */
    public Item createItem(ItemCreateRequest request) {
        if (itemRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException(request.getSku());
        }

        Item item = new Item();
        item.setName(request.getName());
        item.setSku(request.getSku());
        item.setQuantityOnHand(request.getQuantityOnHand());
        item.setUnitCost(request.getUnitCost());
        item.setReorderThreshold(request.getReorderThreshold() != null ? request.getReorderThreshold() : 5);
        item.setActive(true);

        return itemRepository.save(item);
    }

    /**
     * Update an existing item.
     */
    public Item updateItem(Long id, ItemUpdateRequest request) {
        Item item = getRequiredItem(id);

        if (!item.getSku().equals(request.getSku()) && itemRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException(request.getSku());
        }

        item.setName(request.getName());
        item.setSku(request.getSku());
        item.setQuantityOnHand(request.getQuantityOnHand());
        item.setUnitCost(request.getUnitCost());
        item.setReorderThreshold(request.getReorderThreshold());
        item.setActive(Boolean.TRUE.equals(request.getActive()));

        return itemRepository.save(item);
    }

    /**
     * Delete an item by ID.
     */
    public void deleteRequiredItem(Long id) {
        Item item = getRequiredItem(id);
        itemRepository.delete(item);
    }
}