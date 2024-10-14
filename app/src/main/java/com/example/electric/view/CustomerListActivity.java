package com.example.electric.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.electric.R;
import com.example.electric.model.Customer;
import com.example.electric.model.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class CustomerListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerAdapter customerAdapter;
    private List<Customer> customerList = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private EditText etSearch;  // Search bar
    private TextView tvNoResults; // TextView for "No results found"
    private ActivityResultLauncher<Intent> editCustomerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        dbHelper = new DatabaseHelper(this);

        setupToolbar();
        setupRecyclerView();
        setupSearch();  // Setup search functionality
        setupActivityResultLauncher();

        loadCustomerData();  // Load initial data
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        customerAdapter = new CustomerAdapter(customerList, this, dbHelper);
        recyclerView.setAdapter(customerAdapter);
        tvNoResults = findViewById(R.id.tvNoResults); // Ensure correct ID mapping
    }

    // Setup search functionality
    private void setupSearch() {
        etSearch = findViewById(R.id.etSearch); // Ensure R.id.etSearch is an EditText
        Button searchButton = findViewById(R.id.btnSearch); // Ensure R.id.btnSearch is a Button
        Button resetButton = findViewById(R.id.btnReset); // Reset button

        // Handle Search button click
        searchButton.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                searchCustomers(query);
            } else {
                loadCustomerData(); // Load all customers if search query is empty
            }
        });

        // Handle Reset button click
        resetButton.setOnClickListener(v -> {
            etSearch.setText(""); // Clear search text
            loadCustomerData();  // Reload the full customer list
        });
    }

    @SuppressLint("Range")
    private void loadCustomerData() {
        customerList.clear();
        try (Cursor cursor = dbHelper.getAllCustomers()) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("ID"));
                    String name = cursor.getString(cursor.getColumnIndex("NAME"));
                    String address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
                    double electricUsage = cursor.getDouble(cursor.getColumnIndex("USED_NUM_ELECTRIC"));
                    int yyyymm = cursor.getInt(cursor.getColumnIndex("YYYYMM"));
                    int userTypeId = cursor.getInt(cursor.getColumnIndex("ELEC_USER_TYPE_ID"));

                    customerList.add(new Customer(id, name, yyyymm, address, electricUsage, userTypeId));
                } while (cursor.moveToNext());
            }
        }

        if (customerList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvNoResults.setVisibility(View.VISIBLE); // Show "No results found" if no data
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoResults.setVisibility(View.GONE); // Hide "No results found" if data exists
        }

        customerAdapter.notifyDataSetChanged();
    }

    // Search for customers
    private void searchCustomers(String query) {
        customerList.clear();
        List<Customer> searchResults = dbHelper.searchCustomers(query);
        customerList.addAll(searchResults); // Get search results from DB

        if (customerList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvNoResults.setVisibility(View.VISIBLE); // Show "No results found" if no results
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoResults.setVisibility(View.GONE); // Hide "No results found" if results exist
        }

        customerAdapter.notifyDataSetChanged(); // Update RecyclerView
    }

    private void setupActivityResultLauncher() {
        editCustomerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadCustomerData();
                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private double calculateFinalPrice(Customer customer) {
        double unitPrice = customer.getUserTypeId() == 1 ? 1000 : 2000;  // Example: price for userType 1 is 1000, and for userType 2 is 2000
        return customer.getElectricUsage() * unitPrice;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCustomerData();
    }
}
