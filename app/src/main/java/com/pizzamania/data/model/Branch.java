package com.pizzamania.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Branch implements Parcelable {
    private int branchId;
    private String name;
    private String address;
    private double lat;
    private double lng;
    private String phone;
    private String email;
    private String workingHours;
    private boolean isActive;

    public Branch(int branchId, String name, String address, double lat, double lng, String phone) {
        this.branchId = branchId;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.phone = phone;
        this.email = "";
        this.workingHours = "10:00 AM - 11:00 PM";
        this.isActive = true;
    }

    public Branch(int branchId, String name, String address, double lat, double lng,
                  String phone, String email, String workingHours, boolean isActive) {
        this.branchId = branchId;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.phone = phone;
        this.email = email;
        this.workingHours = workingHours;
        this.isActive = isActive;
    }

    // Parcelable implementation
    protected Branch(Parcel in) {
        branchId = in.readInt();
        name = in.readString();
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        phone = in.readString();
        email = in.readString();
        workingHours = in.readString();
        isActive = in.readByte() != 0;
    }

    public static final Creator<Branch> CREATOR = new Creator<Branch>() {
        @Override
        public Branch createFromParcel(Parcel in) {
            return new Branch(in);
        }

        @Override
        public Branch[] newArray(int size) {
            return new Branch[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(branchId);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(workingHours);
        dest.writeByte((byte) (isActive ? 1 : 0));
    }

    // Getters and Setters
    public int getBranchId() { return branchId; }
    public void setBranchId(int branchId) { this.branchId = branchId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWorkingHours() { return workingHours; }
    public void setWorkingHours(String workingHours) { this.workingHours = workingHours; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // Utility methods
    public String getLocationString() {
        return lat + "," + lng;
    }

    public String getFormattedPhone() {
        return "📞 " + phone;
    }

    @Override
    public String toString() {
        return "Branch{" +
                "branchId=" + branchId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}