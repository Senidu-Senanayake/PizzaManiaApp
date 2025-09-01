package com.pizzamania.data.model;

public class OrderItem {
    private int orderItemId;
    private int orderId;
    private int itemId;
    private int qty;
    private int unitPriceCents;

    public OrderItem(int orderItemId, int orderId, int itemId, int qty, int unitPriceCents) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.itemId = itemId;
        this.qty = qty;
        this.unitPriceCents = unitPriceCents;
    }

    public int getOrderItemId() { return orderItemId; }
    public int getOrderId() { return orderId; }
    public int getItemId() { return itemId; }
    public int getQty() { return qty; }
    public int getUnitPriceCents() { return unitPriceCents; }
}
