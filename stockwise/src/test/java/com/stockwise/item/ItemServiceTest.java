package com.stockwise.item;

import com.stockwise.item.dto.ItemCreateRequest;
import com.stockwise.item.dto.ItemUpdateRequest;
import com.stockwise.item.exception.DuplicateSkuException;
import com.stockwise.item.exception.ItemNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ItemService.
 *
 * I am mocking the repository here so I can test service logic by itself.
 * This lets me verify business rules without needing the full application
 * context or a running database.
 */
@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    /**
     * getAllItems should return whatever the repository provides.
     */
    @Test
    void getAllItems_returnsList() {
        List<Item> items = List.of(
                new Item("Hammer", "SKU-001", 10, new BigDecimal("9.99")),
                new Item("Gloves", "SKU-002", 20, new BigDecimal("4.50"))
        );

        when(itemRepository.findAll()).thenReturn(items);

        List<Item> result = itemService.getAllItems();

        assertEquals(2, result.size());
        verify(itemRepository).findAll();
    }

    /**
     * getRequiredItem should return the item when found.
     */
    @Test
    void getRequiredItem_whenFound_returnsItem() {
        Item item = new Item("Hammer", "SKU-001", 10, new BigDecimal("9.99"));

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Item result = itemService.getRequiredItem(1L);

        assertEquals("Hammer", result.getName());
        verify(itemRepository).findById(1L);
    }

    /**
     * getRequiredItem should throw when the item does not exist.
     */
    @Test
    void getRequiredItem_whenMissing_throwsNotFound() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.getRequiredItem(999L));
        verify(itemRepository).findById(999L);
    }

    /**
     * createItem should save a valid new item when the SKU does not already exist.
     */
    @Test
    void createItem_whenSkuIsUnique_savesItem() {
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("Drill");
        request.setSku("SKU-900");
        request.setQuantityOnHand(5);
        request.setUnitCost(new BigDecimal("49.99"));
        request.setActive(true);

        when(itemRepository.existsBySku("SKU-900")).thenReturn(false);
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Item result = itemService.createItem(request);

        assertEquals("Drill", result.getName());
        assertEquals("SKU-900", result.getSku());
        assertEquals(5, result.getQuantityOnHand());
        assertEquals(new BigDecimal("49.99"), result.getUnitCost());
        assertTrue(result.isActive());

        verify(itemRepository).existsBySku("SKU-900");
        verify(itemRepository).save(any(Item.class));
    }

    /**
     * createItem should throw if the SKU already exists.
     */
    @Test
    void createItem_whenSkuExists_throwsDuplicateSku() {
        ItemCreateRequest request = new ItemCreateRequest();
        request.setName("Duplicate Drill");
        request.setSku("SKU-001");
        request.setQuantityOnHand(3);
        request.setUnitCost(new BigDecimal("19.99"));
        request.setActive(true);

        when(itemRepository.existsBySku("SKU-001")).thenReturn(true);

        assertThrows(DuplicateSkuException.class, () -> itemService.createItem(request));

        verify(itemRepository).existsBySku("SKU-001");
        verify(itemRepository, never()).save(any(Item.class));
    }

    /**
     * updateItem should apply new values and save when the item exists.
     */
    @Test
    void updateItem_whenValid_updatesAndSaves() {
        Item existing = new Item("Hammer", "SKU-001", 10, new BigDecimal("9.99"));

        ItemUpdateRequest request = new ItemUpdateRequest();
        request.setName("Updated Hammer");
        request.setSku("SKU-001");
        request.setQuantityOnHand(30);
        request.setUnitCost(new BigDecimal("11.49"));
        request.setActive(true);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Item result = itemService.updateItem(1L, request);

        assertEquals("Updated Hammer", result.getName());
        assertEquals("SKU-001", result.getSku());
        assertEquals(30, result.getQuantityOnHand());
        assertEquals(new BigDecimal("11.49"), result.getUnitCost());
        assertTrue(result.isActive());

        verify(itemRepository).findById(1L);
        verify(itemRepository).save(existing);
    }

    /**
     * updateItem should throw if the target item does not exist.
     */
    @Test
    void updateItem_whenMissing_throwsNotFound() {
        ItemUpdateRequest request = new ItemUpdateRequest();
        request.setName("Ghost Item");
        request.setSku("SKU-404");
        request.setQuantityOnHand(1);
        request.setUnitCost(new BigDecimal("1.00"));
        request.setActive(true);

        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.updateItem(999L, request));

        verify(itemRepository).findById(999L);
        verify(itemRepository, never()).save(any(Item.class));
    }

    /**
     * updateItem should throw if the SKU is being changed to one that already exists.
     */
    @Test
    void updateItem_whenNewSkuAlreadyExists_throwsDuplicateSku() {
        Item existing = new Item("Hammer", "SKU-001", 10, new BigDecimal("9.99"));

        ItemUpdateRequest request = new ItemUpdateRequest();
        request.setName("Hammer Renamed");
        request.setSku("SKU-002");
        request.setQuantityOnHand(10);
        request.setUnitCost(new BigDecimal("9.99"));
        request.setActive(true);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(itemRepository.existsBySku("SKU-002")).thenReturn(true);

        assertThrows(DuplicateSkuException.class, () -> itemService.updateItem(1L, request));

        verify(itemRepository).findById(1L);
        verify(itemRepository).existsBySku("SKU-002");
        verify(itemRepository, never()).save(any(Item.class));
    }

    /**
     * deleteRequiredItem should delete the existing item.
     */
    @Test
    void deleteRequiredItem_whenFound_deletesItem() {
        Item existing = new Item("Hammer", "SKU-001", 10, new BigDecimal("9.99"));

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));

        itemService.deleteRequiredItem(1L);

        verify(itemRepository).findById(1L);
        verify(itemRepository).delete(existing);
    }

    /**
     * deleteRequiredItem should throw if the item does not exist.
     */
    @Test
    void deleteRequiredItem_whenMissing_throwsNotFound() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.deleteRequiredItem(999L));

        verify(itemRepository).findById(999L);
        verify(itemRepository, never()).delete(any(Item.class));
    }
}