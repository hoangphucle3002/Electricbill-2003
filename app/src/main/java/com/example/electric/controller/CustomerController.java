package com.example.electric.controller;

import android.content.Context;
import android.database.Cursor;
import com.example.electric.model.Customer;
import com.example.electric.model.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class CustomerController {

    private final DatabaseHelper dbHelper;

    // Constructor to initialize DatabaseHelper
    public CustomerController(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Method to add a customer to the database
    public void addCustomer(Customer customer) {
        dbHelper.addCustomer(customer);
    }

    // Method to retrieve all customers from the database
    public List<Customer> getAllCustomers() {
        List<Customer> customerList = new ArrayList<>();
        try (Cursor cursor = dbHelper.getAllCustomers()) {
            if (cursor.moveToFirst()) {
                do {
                    Customer customer = createCustomerFromCursor(cursor);
                    customerList.add(customer);
                } while (cursor.moveToNext());
            }
        }
        return customerList;
    }

    // Create a Customer object from a Cursor
    private Customer createCustomerFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
        int yyyymm = cursor.getInt(cursor.getColumnIndexOrThrow("YYYYMM"));
        String address = cursor.getString(cursor.getColumnIndexOrThrow("ADDRESS"));
        double usedElectric = cursor.getDouble(cursor.getColumnIndexOrThrow("USED_NUM_ELECTRIC"));
        int userTypeId = cursor.getInt(cursor.getColumnIndexOrThrow("ELEC_USER_TYPE_ID"));

        return new Customer(id, name, yyyymm, address, usedElectric, userTypeId);
    }
}
