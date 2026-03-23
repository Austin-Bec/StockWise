package com.stockwise.transaction;

import com.stockwise.item.Item;
import com.stockwise.item.ItemRepository;
import com.stockwise.transaction.dto.InventoryTransactionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;
    private final ItemRepository itemRepository;

    public InventoryTransactionService(InventoryTransactionRepository transactionRepository,
                                       ItemRepository itemRepository) {
        this.transactionRepository = transactionRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public void processTransaction(InventoryTransactionRequest request) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        Integer requestedQty = request.getQuantityChanged();
        if (requestedQty == null || requestedQty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        int quantityBefore = item.getQuantityOnHand();
        int quantityAfter;

        switch (request.getTransactionType()) {
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
        transaction.setTransactionType(request.getTransactionType());
        transaction.setQuantityChanged(requestedQty);
        transaction.setQuantityBefore(quantityBefore);
        transaction.setQuantityAfter(quantityAfter);
        transaction.setNote(request.getNote());
        transaction.setTimestamp(LocalDateTime.now());

        itemRepository.save(item);
        transactionRepository.save(transaction);
    }

    public List<InventoryTransaction> getTransactionsForItem(Long itemId) {
        return transactionRepository.findByItemIdOrderByTimestampDesc(itemId);
    }
}