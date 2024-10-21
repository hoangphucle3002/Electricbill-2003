package com.example.electric.model;

public class Customer {

    // Các thuộc tính của khách hàng
    private int id;
    private String name;
    private int yyyymm; // Tháng và năm dạng YYYYMM
    private String address;
    private double electricUsage; // Số điện đã sử dụng
    private int userTypeId; // Loại người dùng (Private/Business)

    // Constructor để khởi tạo đối tượng Customer
    public Customer(int id, String name, int yyyymm, String address, double electricUsage, int userTypeId) {
        this.id = id;
        this.name = name;
        this.yyyymm = yyyymm;
        this.address = address;
        this.electricUsage = electricUsage;
        this.userTypeId = userTypeId;
    }

    // Getter cho id
    public int getId() {
        return id;
    }

    // Setter cho id
    public void setId(int id) {
        this.id = id;
    }

    // Getter cho name (Tên khách hàng)
    public String getName() {
        return name;
    }

    // Setter cho name
    public void setName(String name) {
        this.name = name;
    }

    // Getter cho yyyymm (Tháng và năm)
    public int getYyyymm() {
        return yyyymm;
    }

    // Getter cho address (Địa chỉ)
    public String getAddress() {
        return address;
    }

    // Setter cho address
    public void setAddress(String address) {
        this.address = address;
    }

    // Getter cho electricUsage (Số điện sử dụng)
    public double getElectricUsage() {
        return electricUsage;
    }

    // Getter cho userTypeId (Loại người dùng điện)
    public int getUserTypeId() {
        return userTypeId;
    }

    // Setter cho userTypeId
    public void setUserTypeId(int userTypeId) {
        this.userTypeId = userTypeId;
    }
}
