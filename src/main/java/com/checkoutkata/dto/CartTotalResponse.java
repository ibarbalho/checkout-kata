package com.checkoutkata.dto;

public class CartTotalResponse {

    private final int total;

    public CartTotalResponse(int total) {
        this.total = total;
    }

    public int getTotal() {
        return total;
    }
}
