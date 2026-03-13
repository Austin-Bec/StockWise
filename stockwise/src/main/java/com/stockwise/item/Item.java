package com.stockwise.item;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entity: Item
 * Responsibility:
 * - Represents a stock keeping unit (SKU) in the StockWise system.
 * - Stores basic inventory master data such as name, SKU, quantity and cost.
 */
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(nullable = false)
    private Integer quantityOnHand = 0;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitCost = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean active = true;

    /**
     * Default constructor required by JPA.
     */
    protected Item() {
        // JPA only
    }

    /**
     * Convenience constructor for creating new items.
     */
    public Item(String name, String sku, Integer quantityOnHand, BigDecimal unitCost) {
        this.name = name;
        this.sku = sku;
        this.quantityOnHand = quantityOnHand;
        this.unitCost = unitCost;
        this.active = true;
    }

    // ----- Getters & Setters -----

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
