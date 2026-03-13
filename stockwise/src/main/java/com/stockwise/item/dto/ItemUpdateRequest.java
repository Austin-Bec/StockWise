package com.stockwise.item.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ItemUpdateRequest {

    @NotBlank
    @Size(max = 200)
    private String name;

    @NotBlank
    @Size(max = 50)
    private String sku;

    @NotNull
    @Min(0)
    private Integer quantityOnHand;

    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal unitCost;

    @NotNull
    private Boolean active;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Integer getQuantityOnHand() { return quantityOnHand; }
    public void setQuantityOnHand(Integer quantityOnHand) { this.quantityOnHand = quantityOnHand; }

    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}