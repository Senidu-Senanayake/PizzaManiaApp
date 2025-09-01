package com.pizzamania.data.model;

public class Order {
    private int orderId;
    private int userId;
    private int branchId;
    private String status;
    private long createdAt;
    private int totalCents;

    public Order(int orderId, int userId, int branchId, String status, long createdAt, int totalCents) {
        this.orderId = orderId;
        this.userId = userId;
        this.branchId = branchId;
        this.status = status;
        this.createdAt = createdAt;
        this.totalCents = totalCents;
    }

    public int getOrderId() { return orderId; }
    public int getUserId() { return userId; }
    public int getBranchId() { return branchId; }
    public String getStatus() { return status; }
    public long getCreatedAt() { return createdAt; }
    public int getTotalCents() { return totalCents; }

    public void setStatus(String status) {
        this.status = status;
    }
}
