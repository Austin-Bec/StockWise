package com.stockwise.transaction;

import com.stockwise.item.Item;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity: InventoryTransaction
 * Responsibility:
 * - Records an inventory movement for a specific item.
 * - Stores the type of transaction, quantity changed, before/after stock levels,
 *   optional notes, and timestamp for audit/history purposes.
 */
@Entity
@Table(name = "inventory_transactions")
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The inventory item associated with this transaction.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /**
     * Transaction type, such as STOCK_IN, STOCK_OUT, or ADJUSTMENT.
     */
    @Column(nullable = false, length = 30)
    private String transactionType;

    /**
     * The quantity entered for this transaction.
     */
    @Column(nullable = false)
    private Integer quantityChanged;

    /**
     * Quantity on hand before the transaction was applied.
     */
    @Column(nullable = false)
    private Integer quantityBefore;

    /**
     * Quantity on hand after the transaction was applied.
     */
    @Column(nullable = false)
    private Integer quantityAfter;

    /**
     * Optional note explaining the reason for the transaction.
     */
    @Column(length = 255)
    private String note;

    /**
     * Date and time the transaction was recorded.
     */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /**
     * Default constructor required by JPA.
     */
    public InventoryTransaction() {
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getQuantityChanged() {
        return quantityChanged;
    }

    public void setQuantityChanged(Integer quantityChanged) {
        this.quantityChanged = quantityChanged;
    }

    public Integer getQuantityBefore() {
        return quantityBefore;
    }

    public void setQuantityBefore(Integer quantityBefore) {
        this.quantityBefore = quantityBefore;
    }

    public Integer getQuantityAfter() {
        return quantityAfter;
    }

    public void setQuantityAfter(Integer quantityAfter) {
        this.quantityAfter = quantityAfter;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}