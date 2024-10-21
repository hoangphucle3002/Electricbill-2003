package com.example.electric.controller;

import android.content.Context;
import android.database.Cursor;
import com.example.electric.model.Customer;
import com.example.electric.model.DatabaseManage;

import java.util.ArrayList;
import java.util.List;

public class CustomerController {

    private final DatabaseManage dbHelper; // Quản lý cơ sở dữ liệu

    // Constructor để khởi tạo DatabaseManage
    public CustomerController(Context context) {
        dbHelper = new DatabaseManage(context);
    }

    // Phương thức để thêm khách hàng vào cơ sở dữ liệu
    public void addCustomer(Customer customer) {
        dbHelper.addCustomer(customer);
    }

    // Phương thức để lấy tất cả khách hàng từ cơ sở dữ liệu
    public List<Customer> getAllCustomers() {
        List<Customer> customerList = new ArrayList<>();
        try (Cursor cursor = dbHelper.getAllCustomers()) { // Dùng Cursor để duyệt qua dữ liệu
            if (cursor.moveToFirst()) { // Di chuyển đến dòng đầu tiên
                do {
                    // Tạo đối tượng Customer từ Cursor
                    Customer customer = createCustomerFromCursor(cursor);
                    customerList.add(customer); // Thêm Customer vào danh sách
                } while (cursor.moveToNext()); // Tiếp tục cho đến hết các hàng
            }
        }
        return customerList; // Trả về danh sách khách hàng
    }

    // Tạo đối tượng Customer từ Cursor
    private Customer createCustomerFromCursor(Cursor cursor) {
        // Lấy dữ liệu từ các cột trong Cursor
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
        int yyyymm = cursor.getInt(cursor.getColumnIndexOrThrow("YYYYMM"));
        String address = cursor.getString(cursor.getColumnIndexOrThrow("ADDRESS"));
        double usedElectric = cursor.getDouble(cursor.getColumnIndexOrThrow("USED_NUM_ELECTRIC"));
        int userTypeId = cursor.getInt(cursor.getColumnIndexOrThrow("ELEC_USER_TYPE_ID"));

        // Trả về đối tượng Customer với dữ liệu từ Cursor
        return new Customer(id, name, yyyymm, address, usedElectric, userTypeId);
    }
}
