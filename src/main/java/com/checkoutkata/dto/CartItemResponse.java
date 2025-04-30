package com.checkoutkata.dto;

public class CartItemResponse {
    private Long itemId;
    private String itemName;
    private int unitPrice;
    private int quantity;

    public CartItemResponse(Long itemId, String itemName, int unitPrice, int quantity) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public Long getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }
}
