package com.stockwise.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for reading and writing inventory transaction records.
 */
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    /**
     * Returns all transactions for a given item ordered by most recent first.
     *
     * @param itemId the item id
     * @return transaction history for the item
     */
    List<InventoryTransaction> findByItemIdOrderByTimestampDesc(Long itemId);
}