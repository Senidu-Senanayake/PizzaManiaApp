package com.pizzamania.data.model;

public class User {
    private int userId;
    private String fullName;
    private String phone;
    private String email;
    private String passwordHash;
    private String address;
    private double lat;
    private double lng;

    public User(int userId, String fullName, String phone, String email, String passwordHash, String address, double lat, double lng) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.passwordHash = passwordHash;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public int getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getAddress() { return address; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }
}
