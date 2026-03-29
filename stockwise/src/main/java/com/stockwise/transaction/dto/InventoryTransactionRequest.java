package com.stockwise.transaction.dto;

/**
 * DTO used to capture user input when submitting an inventory transaction.
 * This request is used by the transaction form and passed into the service layer.
 */
public class InventoryTransactionRequest {

    /**
     * The id of the item being adjusted.
     */
    private Long itemId;

    /**
     * Type of transaction: STOCK_IN, STOCK_OUT, or ADJUSTMENT.
     */
    private String transactionType;

    /**
     * Quantity entered by the user for the transaction.
     */
    private Integer quantityChanged;

    /**
     * Optional note describing the reason for the transaction.
     */
    private String note;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}