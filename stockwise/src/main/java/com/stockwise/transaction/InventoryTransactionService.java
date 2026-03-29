package com.stockwise.transaction;

import com.stockwise.item.Item;
import com.stockwise.item.ItemRepository;
import com.stockwise.transaction.dto.InventoryTransactionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsible for processing inventory transactions and updating item stock levels.
 */
@Service
public class InventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;
    private final ItemRepository itemRepository;

    public InventoryTransactionService(InventoryTransactionRepository transactionRepository,
                                       ItemRepository itemRepository) {
        this.transactionRepository = transactionRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * Processes an inventory transaction and updates the associated item's quantity on hand.
     *
     * Supported transaction types:
     * - STOCK_IN: increases inventory
     * - STOCK_OUT: decreases inventory if enough stock exists
     * - ADJUSTMENT: sets inventory directly to the provided amount
     *
     * @param request the transaction request submitted by the user
     */
    @Transactional
    public void processTransaction(InventoryTransactionRequest request) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (!item.isActive()) {
            throw new IllegalArgumentException("Transactions cannot be processed for inactive items");
        }

        Integer requestedQty = request.getQuantityChanged();
        if (requestedQty == null || requestedQty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        String transactionType = request.getTransactionType();
        if (transactionType == null || transactionType.isBlank()) {
            throw new IllegalArgumentException("Transaction type is required");
        }

        int quantityBefore = item.getQuantityOnHand();
        int quantityAfter;

        switch (transactionType.trim().toUpperCase()) {
            case "STOCK_IN":
                quantityAfter = quantityBefore + requestedQty;
                break;

            case "STOCK_OUT":
                if (requestedQty > quantityBefore) {
                    throw new IllegalArgumentException("Not enough inventory available");
                }
                quantityAfter = quantityBefore - requestedQty;
                break;

            case "ADJUSTMENT":
                quantityAfter = requestedQty;
                break;

            default:
                throw new IllegalArgumentException("Invalid transaction type");
        }

        item.setQuantityOnHand(quantityAfter);

        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setItem(item);
        transaction.setTransactionType(transactionType.trim().toUpperCase());
        transaction.setQuantityChanged(requestedQty);
        transaction.setQuantityBefore(quantityBefore);
        transaction.setQuantityAfter(quantityAfter);
        transaction.setNote(request.getNote());
        transaction.setTimestamp(LocalDateTime.now());

        itemRepository.save(item);
        transactionRepository.save(transaction);
    }

    /**
     * Returns transaction history for a specific item, ordered newest first.
     *
     * @param itemId the item id
     * @return list of transactions for that item
     */
    @Transactional(readOnly = true)
    public List<InventoryTransaction> getTransactionsForItem(Long itemId) {
        return transactionRepository.findByItemIdOrderByTimestampDesc(itemId);
    }
}