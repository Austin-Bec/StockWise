package com.stockwise.item;

import com.stockwise.item.dto.ItemCreateRequest;
import com.stockwise.item.dto.ItemUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller: ItemController
 *
 * Responsibility:
 * - Expose REST endpoints for item management.
 * - Delegate business logic to ItemService.
 * - Keep business rules out of the controller.
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    /**
     * Constructor injection for ItemService.
     * I prefer constructor injection for clarity and easier testing.
     */
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * GET /api/items
     * Retrieve all items currently stored.
     */
    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    /**
     * GET /api/items/{id}
     * Retrieve a single item by ID.
     *
     * If the item does not exist, the service throws
     * ItemNotFoundException, which is handled globally.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getRequiredItem(id));
    }

    /**
     * GET /api/items/sku/{sku}
     * Retrieve a single item by SKU.
     */
    @GetMapping("/sku/{sku}")
    public ResponseEntity<Item> getItemBySku(@PathVariable String sku) {
        return ResponseEntity.ok(itemService.getItemBySku(sku));
    }

    /**
     * POST /api/items
     * Create a new item.
     *
     * Validation is handled through @Valid on the DTO.
     */
    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody ItemCreateRequest request) {
        Item saved = itemService.createItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * PUT /api/items/{id}
     * Update an existing item.
     *
     * The service layer handles:
     * - Not found checks
     * - Duplicate SKU checks
     * - Business rule enforcement
     */
    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id,
                                           @Valid @RequestBody ItemUpdateRequest request) {
        Item updated = itemService.updateItem(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/items/{id}
     * Remove an item by ID.
     *
     * If the item does not exist, the service throws ItemNotFoundException.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteRequiredItem(id);
        return ResponseEntity.noContent().build();
    }
}