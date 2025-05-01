package com.checkoutkata.domain;

import jakarta.persistence.*;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int unitPrice;

    public Item() {
    }

    public Item(String name, int unitPrice) {
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }
}
