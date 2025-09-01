package com.pizzamania.data.model;

public class Branch {
    private int branchId;
    private String name;
    private String address;
    private double lat;
    private double lng;
    private String phone;

    public Branch(int branchId, String name, String address, double lat, double lng, String phone) {
        this.branchId = branchId;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.phone = phone;
    }

    public int getBranchId() { return branchId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }
    public String getPhone() { return phone; }
}
