package com.example.electric.view;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.electric.R;
import com.example.electric.model.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class IncreasePriceActivity extends AppCompatActivity {

    private EditText etIncreaseAmount;
    private Button btnIncreasePrice, btnDecreasePrice;
    private Spinner spinnerUserType;
    private TextView tvCurrentPrice;
    private DatabaseHelper dbHelper;
    private double currentPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_increase_price);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable back button
        }

        // Initialize views
        etIncreaseAmount = findViewById(R.id.etIncreaseAmount);
        btnIncreasePrice = findViewById(R.id.btnIncreasePrice);
        btnDecreasePrice = findViewById(R.id.btnDecreasePrice);
        spinnerUserType = findViewById(R.id.spinnerUserType);
        tvCurrentPrice = findViewById(R.id.tvCurrentPrice);

        dbHelper = new DatabaseHelper(this);

        // Handle Spinner item selection to display current electric price
        spinnerUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCurrentPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Handle increase price button click
        btnIncreasePrice.setOnClickListener(v -> increaseElectricUnitPrice());

        // Handle decrease price button click
        btnDecreasePrice.setOnClickListener(v -> decreaseElectricUnitPrice());
    }

    private void updateCurrentPrice() {
        String selectedUserType = spinnerUserType.getSelectedItem().toString();
        int userTypeId = selectedUserType.equals("Private") ? 1 : 2;
        currentPrice = dbHelper.getUnitPrice(userTypeId);  // Lấy giá hiện tại từ DatabaseHelper

        // Cập nhật TextView để hiển thị giá điện hiện tại
        tvCurrentPrice.setText("Current Price: " + String.format("%,.0f", currentPrice) );
    }

    private void increaseElectricUnitPrice() {
        String amountText = etIncreaseAmount.getText().toString().trim();

        // Validate user input
        if (amountText.isEmpty()) {
            Toast.makeText(this, "Please enter an amount to increase", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double increaseAmount = Double.parseDouble(amountText);

            // Check for positive value
            if (increaseAmount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get user type from Spinner (Private or Business)
            String selectedUserType = spinnerUserType.getSelectedItem().toString();
            int userTypeId = selectedUserType.equals("Private") ? 1 : 2;

            // Increase electric price for the selected user type
            dbHelper.increaseElectricPrice(userTypeId, increaseAmount);

            // Get current time
            String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            // Display confirmation message
            String message = "Increased price for " + selectedUserType + ": " + amountText + " VND at " + currentTime;
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            // Update current price
            updateCurrentPrice();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
        }
    }

    private void decreaseElectricUnitPrice() {
        String amountText = etIncreaseAmount.getText().toString().trim();

        // Validate user input
        if (amountText.isEmpty()) {
            Toast.makeText(this, "Please enter an amount to decrease", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double decreaseAmount = Double.parseDouble(amountText);

            // Check for positive value
            if (decreaseAmount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get user type from Spinner (Private or Business)
            String selectedUserType = spinnerUserType.getSelectedItem().toString();
            int userTypeId = selectedUserType.equals("Private") ? 1 : 2;

            // Calculate new price
            double newPrice = currentPrice - decreaseAmount;

            // Ensure price doesn't drop below 0
            if (newPrice < 0) {
                Toast.makeText(this, "Electric price cannot be lower than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Decrease electric price for the selected user type
            dbHelper.increaseElectricPrice(userTypeId, -decreaseAmount);

            // Get current time
            String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            // Display confirmation message
            String message = "Decreased price for " + selectedUserType + ": " + amountText + " VND at " + currentTime;
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            // Update current price
            updateCurrentPrice();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Handle back button press
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
