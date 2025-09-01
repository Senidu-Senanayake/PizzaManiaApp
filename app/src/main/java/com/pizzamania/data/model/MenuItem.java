package com.pizzamania.data.model;

import java.io.Serializable;

public class MenuItem implements Serializable {
    private int itemId;
    private String name;
    private String description;
    private int priceCents;
    private String imageUri;
    private String category;
    private boolean available;

    public MenuItem(int itemId, String name, String description, int priceCents, String imageUri, String category, boolean available) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.priceCents = priceCents;
        this.imageUri = imageUri;
        this.category = category;
        this.available = available;
    }

    public int getItemId() { return itemId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPriceCents() { return priceCents; }
    public String getImageUri() { return imageUri; }
    public String getCategory() { return category; }
    public boolean isAvailable() { return available; }
}
