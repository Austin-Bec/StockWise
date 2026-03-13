package com.stockwise.item.exception;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(Long id) {
        super("Item not found: " + id);
    }

    public ItemNotFoundException(String sku) {
        super("Item not found for SKU: " + sku);
    }
}