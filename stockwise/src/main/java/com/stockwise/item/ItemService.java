package com.stockwise.item;

import com.stockwise.item.dto.ItemCreateRequest;
import com.stockwise.item.dto.ItemUpdateRequest;
import com.stockwise.item.exception.DuplicateSkuException;
import com.stockwise.item.exception.ItemNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service: ItemService
 *
 * Responsibility:
 * - Encapsulate business logic related to items.
 * - Provide higher-level operations for controllers.
 * - Enforce business rules such as unique SKU validation.
 */
@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * Constructor-based dependency injection of ItemRepository.
     */
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Retrieve all items.
     *
     * @return List of all items.
     */
    @Transactional(readOnly = true)
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    /**
     * Retrieve a single item by ID.
     *
     * @param id Item primary key.
     * @return Item or null if not found.
     */
    @Transactional(readOnly = true)
    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElse(null);
    }

    /**
     * Retrieve a single item by ID and require that it exists.
     *
     * @param id Item primary key.
     * @return Existing item.
     * @throws ItemNotFoundException if no item exists for the given ID.
     */
    @Transactional(readOnly = true)
    public Item getRequiredItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
    }

    /**
     * Retrieve a single item by SKU.
     *
     * @param sku Unique SKU value.
     * @return Existing item.
     * @throws ItemNotFoundException if no item exists for the given SKU.
     */
    @Transactional(readOnly = true)
    public Item getItemBySku(String sku) {
        return itemRepository.findBySku(sku)
                .orElseThrow(() -> new ItemNotFoundException(sku));
    }

    /**
     * Create a new item.
     *
     * @param req DTO containing create request data.
     * @return Saved item.
     * @throws DuplicateSkuException if the SKU already exists.
     */
    public Item createItem(ItemCreateRequest req) {
        if (itemRepository.existsBySku(req.getSku())) {
            throw new DuplicateSkuException(req.getSku());
        }

        Item item = new Item(
                req.getName(),
                req.getSku(),
                req.getQuantityOnHand(),
                req.getUnitCost()
        );

        item.setActive(req.getActive() == null || req.getActive());

        return itemRepository.save(item);
    }

    /**
     * Update an existing item.
     *
     * @param id  ID of the item to update.
     * @param req DTO containing updated values.
     * @return Updated item.
     * @throws ItemNotFoundException if the item does not exist.
     * @throws DuplicateSkuException if SKU is changed to one that already exists.
     */
    public Item updateItem(Long id, ItemUpdateRequest req) {
        Item existing = getRequiredItem(id);

        if (!existing.getSku().equals(req.getSku()) && itemRepository.existsBySku(req.getSku())) {
            throw new DuplicateSkuException(req.getSku());
        }

        existing.setName(req.getName());
        existing.setSku(req.getSku());
        existing.setQuantityOnHand(req.getQuantityOnHand());
        existing.setUnitCost(req.getUnitCost());
        existing.setActive(req.getActive());

        return itemRepository.save(existing);
    }

    /**
     * Delete an item and require that it exists first.
     *
     * @param id ID of the item to delete.
     * @throws ItemNotFoundException if the item does not exist.
     */
    public void deleteRequiredItem(Long id) {
        Item existing = getRequiredItem(id);
        itemRepository.delete(existing);
    }
}