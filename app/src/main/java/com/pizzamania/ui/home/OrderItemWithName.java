package com.pizzamania.ui.home;

public class OrderItemWithName {
    private String name;
    private int qty;
    private int unitPriceCents;

    public OrderItemWithName(String name, int qty, int unitPriceCents) {
        this.name = name;
        this.qty = qty;
        this.unitPriceCents = unitPriceCents;
    }

    public String getName() { return name; }
    public int getQty() { return qty; }
    public int getUnitPriceCents() { return unitPriceCents; }
}
