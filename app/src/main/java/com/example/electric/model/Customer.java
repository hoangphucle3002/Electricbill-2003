package com.example.electric.model;

public class Customer {

    private int id;
    private String name;
    private int yyyymm;
    private String address;
    private double electricUsage;
    private int userTypeId;

    public Customer(int id, String name, int yyyymm, String address, double electricUsage, int userTypeId) {
        this.id = id;
        this.name = name;
        this.yyyymm = yyyymm;
        this.address = address;
        this.electricUsage = electricUsage;
        this.userTypeId = userTypeId;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYyyymm() {
        return yyyymm;
    }



    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getElectricUsage() {
        return electricUsage;
    }



    public int getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(int userTypeId) {
        this.userTypeId = userTypeId;
    }
}
