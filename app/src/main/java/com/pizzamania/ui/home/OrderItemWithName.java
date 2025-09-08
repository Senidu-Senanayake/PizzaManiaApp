package com.pizzamania.ui.home;

public class OrderItemWithName {
    private String name;
    private int qty;
    private int unitPriceCents;
    private String imageUri;
    private String size;
    private String extras;

    public OrderItemWithName(String name, int qty, int unitPriceCents,
                             String imageUri, String size, String extras) {
        this.name = name;
        this.qty = qty;
        this.unitPriceCents = unitPriceCents;
        this.imageUri = imageUri;
        this.size = size;
        this.extras = extras;
    }

    public String getName() { return name; }
    public int getQty() { return qty; }
    public int getUnitPriceCents() { return unitPriceCents; }
    public String getImageUri() { return imageUri; }
    public String getSize() { return size; }
    public String getExtras() { return extras; }
}
