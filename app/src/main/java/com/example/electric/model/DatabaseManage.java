package com.example.electric.model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManage extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "electricity.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_CUSTOMER = "Customer";
    private static final String COLUMN_CUSTOMER_ID = "ID";
    private static final String COLUMN_CUSTOMER_NAME = "NAME";
    private static final String COLUMN_CUSTOMER_YYYYMM = "YYYYMM";
    private static final String COLUMN_CUSTOMER_ADDRESS = "ADDRESS";
    private static final String COLUMN_CUSTOMER_USED_NUM_ELECTRIC = "USED_NUM_ELECTRIC";
    private static final String COLUMN_CUSTOMER_ELEC_USER_TYPE_ID = "ELEC_USER_TYPE_ID";

    private static final String TABLE_ELECTRIC_USER_TYPE = "ElectricUserType";
    private static final String COLUMN_TYPE_ID = "ID";
    private static final String COLUMN_TYPE_NAME = "ELEC_USER_TYPE_NAME";
    private static final String COLUMN_UNIT_PRICE = "UNIT_PRICE";

    public DatabaseManage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCustomerTable = "CREATE TABLE " + TABLE_CUSTOMER + "("
                + COLUMN_CUSTOMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CUSTOMER_NAME + " TEXT,"
                + COLUMN_CUSTOMER_YYYYMM + " INTEGER,"
                + COLUMN_CUSTOMER_ADDRESS + " TEXT,"
                + COLUMN_CUSTOMER_USED_NUM_ELECTRIC + " REAL,"
                + COLUMN_CUSTOMER_ELEC_USER_TYPE_ID + " INTEGER)";
        db.execSQL(createCustomerTable);

        String createUserTypeTable = "CREATE TABLE " + TABLE_ELECTRIC_USER_TYPE + "("
                + COLUMN_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TYPE_NAME + " TEXT,"
                + COLUMN_UNIT_PRICE + " REAL)";
        db.execSQL(createUserTypeTable);

        insertInitialData1(db);
    }

    private void insertInitialData1(SQLiteDatabase db) {
        insertUserType(db, "Private", 1000);
        insertUserType(db, "Business", 2000);

        insertCustomer(db, "Ramesh", 202401, "Ahmedabad", 2000.00, 1);
        insertCustomer(db, "Khilan", 202401, "Delhi", 1500.00, 2);
        insertCustomer(db, "Kaushik", 202402, "Kota", 2000.00, 1);
        insertCustomer(db, "Chaitali", 202402, "Mumbai", 6500.00, 1);
        insertCustomer(db, "Hardik", 202403, "Bhopal", 8500.00, 2);
        insertCustomer(db, "Komal", 202403, "MP", 4500.00, 1);
    }

    private void insertUserType(SQLiteDatabase db, String typeName, double unitPrice) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE_NAME, typeName);
        values.put(COLUMN_UNIT_PRICE, unitPrice);
        db.insert(TABLE_ELECTRIC_USER_TYPE, null, values);
    }

    private void insertCustomer(SQLiteDatabase db, String name, int yyyymm, String address, double usedElectric, int userTypeId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOMER_NAME, name);
        values.put(COLUMN_CUSTOMER_YYYYMM, yyyymm);
        values.put(COLUMN_CUSTOMER_ADDRESS, address);
        values.put(COLUMN_CUSTOMER_USED_NUM_ELECTRIC, usedElectric);
        values.put(COLUMN_CUSTOMER_ELEC_USER_TYPE_ID, userTypeId);
        db.insert(TABLE_CUSTOMER, null, values);
    }

    public long addCustomer(Customer customer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOMER_NAME, customer.getName());
        values.put(COLUMN_CUSTOMER_YYYYMM, customer.getYyyymm());
        values.put(COLUMN_CUSTOMER_ADDRESS, customer.getAddress());
        values.put(COLUMN_CUSTOMER_USED_NUM_ELECTRIC, customer.getElectricUsage());
        values.put(COLUMN_CUSTOMER_ELEC_USER_TYPE_ID, customer.getUserTypeId());

        long customerId = db.insert(TABLE_CUSTOMER, null, values);

        if (customerId != -1) {
            customer.setId((int) customerId);
            Log.d("DatabaseManage", "Customer added with ID: " + customerId);
        }

        return customerId;
    }

    public boolean deleteCustomer(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_CUSTOMER, "ID = ?", new String[]{String.valueOf(id)});

        if (rowsDeleted > 0) {
            Log.d("DatabaseManage", "Rows deleted: " + rowsDeleted);
            return true;
        } else {
            Log.e("DatabaseManage", "No rows deleted for ID: " + id);
            return false;
        }
    }

    public Cursor getAllCustomers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CUSTOMER, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ELECTRIC_USER_TYPE);
        onCreate(db);
    }

    public double getUnitPrice(int userTypeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        double unitPrice = 0;

        Cursor cursor = db.rawQuery("SELECT " + COLUMN_UNIT_PRICE + " FROM " + TABLE_ELECTRIC_USER_TYPE + " WHERE " + COLUMN_TYPE_ID + " = ?", new String[]{String.valueOf(userTypeId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_UNIT_PRICE);
                if (columnIndex != -1) {
                    unitPrice = cursor.getDouble(columnIndex);
                } else {
                    Log.e("DatabaseManage", "Column 'UNIT_PRICE' not found in ElectricUserType table.");
                }
            }
            cursor.close();
        }

        return unitPrice;
    }

    public void increaseElectricPrice(int userTypeId, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        double currentPrice = getUnitPrice(userTypeId);
        ContentValues values = new ContentValues();
        values.put(COLUMN_UNIT_PRICE, currentPrice + amount);
        db.update(TABLE_ELECTRIC_USER_TYPE, values, COLUMN_TYPE_ID + " = ?", new String[]{String.valueOf(userTypeId)});
    }

    public List<Customer> searchCustomers(String query) {
        List<Customer> customers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String sqlQuery = "SELECT * FROM " + TABLE_CUSTOMER + " WHERE " + COLUMN_CUSTOMER_NAME + " LIKE ? OR " + COLUMN_CUSTOMER_ADDRESS + " LIKE ?";

        String[] selectionArgs = new String[]{"%" + query + "%", "%" + query + "%"};

        Cursor cursor = db.rawQuery(sqlQuery, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_CUSTOMER_ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMER_NAME));
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMER_ADDRESS));
                @SuppressLint("Range") double electricUsage = cursor.getDouble(cursor.getColumnIndex(COLUMN_CUSTOMER_USED_NUM_ELECTRIC));
                @SuppressLint("Range") int yyyymm = cursor.getInt(cursor.getColumnIndex(COLUMN_CUSTOMER_YYYYMM));
                @SuppressLint("Range") int userTypeId = cursor.getInt(cursor.getColumnIndex(COLUMN_CUSTOMER_ELEC_USER_TYPE_ID));

                customers.add(new Customer(id, name, yyyymm, address, electricUsage, userTypeId));
            } while (cursor.moveToNext());
            cursor.close();
        }

        return customers;
    }

    public boolean updateCustomer(int customerId, String name, String address, double electricUsage, int yyyymm, int userTypeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CUSTOMER_NAME, name);
        values.put(COLUMN_CUSTOMER_ADDRESS, address);
        values.put(COLUMN_CUSTOMER_USED_NUM_ELECTRIC, electricUsage);
        values.put(COLUMN_CUSTOMER_YYYYMM, yyyymm);
        values.put(COLUMN_CUSTOMER_ELEC_USER_TYPE_ID, userTypeId);

        int rowsAffected = db.update(TABLE_CUSTOMER, values, COLUMN_CUSTOMER_ID + " = ?", new String[]{String.valueOf(customerId)});

        db.close();

        return rowsAffected > 0;
    }
}
