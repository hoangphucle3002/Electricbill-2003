package com.example.electric.view;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.electric.R;
import com.example.electric.controller.CustomerController;
import com.example.electric.model.Customer;

import java.util.Calendar;

public class AddCustomer extends AppCompatActivity {

    private EditText etCustomerName, etCustomerAddress, etElectricUsage;
    private Button btnSaveCustomer, btnPickDate;
    private CustomerController customerController;
    private Spinner spinnerUserType;
    private int selectedYear, selectedMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_customer);

        setupToolbar();
        initViews();
        setupListeners();

        SharedPreferences preferences = getSharedPreferences("settings_prefs", MODE_PRIVATE);
        boolean showAddress = preferences.getBoolean("show_address", true);
        boolean showElectricUsage = preferences.getBoolean("show_electric_usage", true);
        boolean showUserType = preferences.getBoolean("show_user_type", true);

        if (!showAddress) {
            etCustomerAddress.setVisibility(View.GONE);
        }
        if (!showElectricUsage) {
            etElectricUsage.setVisibility(View.GONE);
        }
        if (!showUserType) {
            spinnerUserType.setVisibility(View.GONE);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Customer");
        }
    }

    private void initViews() {
        etCustomerName = findViewById(R.id.etCustomerName);
        etCustomerAddress = findViewById(R.id.etCustomerAddress);
        etElectricUsage = findViewById(R.id.etElectricUsage);
        btnSaveCustomer = findViewById(R.id.btnSaveCustomer);
        btnPickDate = findViewById(R.id.btnPickDate);
        spinnerUserType = findViewById(R.id.spinnerUserType);
        customerController = new CustomerController(this);
    }

    private void setupListeners() {
        btnPickDate.setOnClickListener(v -> showDatePickerDialog());
        btnSaveCustomer.setOnClickListener(v -> validateAndSaveCustomer());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        // Create NumberPicker for month selection
        final NumberPicker monthPicker = new NumberPicker(this);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(currentMonth);

        // Create NumberPicker for year selection
        final NumberPicker yearPicker = new NumberPicker(this);
        yearPicker.setMinValue(2000);  // Minimum year
        yearPicker.setMaxValue(currentYear);  // Maximum year (current year)
        yearPicker.setValue(currentYear);

        // Create a LinearLayout to hold the two NumberPickers and center them
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER); // Center the two NumberPickers
        layout.setPadding(50, 0, 50, 0); // Add padding for balance
        layout.addView(monthPicker);

        // Add spacing between the two NumberPickers
        Space space = new Space(this);
        space.setMinimumWidth(20); // Distance between monthPicker and yearPicker
        layout.addView(space);

        layout.addView(yearPicker);

        // Create an AlertDialog to display the pickers
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Month/Year");
        builder.setView(layout);

        // When the user clicks "OK"
        builder.setPositiveButton("OK", (dialog, which) -> {
            selectedMonth = monthPicker.getValue();
            selectedYear = yearPicker.getValue();

            // Display the selected month and year on the button
            btnPickDate.setText("Month: " + selectedMonth + " Year: " + selectedYear);
        });

        // When the user clicks "Cancel"
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void validateAndSaveCustomer() {
        String name = etCustomerName.getText().toString().trim();
        String address = etCustomerAddress.getText().toString().trim();
        String usageText = etElectricUsage.getText().toString().trim();
        String selectedUserType = spinnerUserType.getSelectedItem().toString();

        if (!validateInputs(name, address, usageText, selectedUserType)) {
            return;
        }

        double usage = Double.parseDouble(usageText);
        int yyyymm = selectedYear * 100 + selectedMonth;
        int userTypeId = selectedUserType.equals("Private") ? 1 : 2;

        Customer customer = new Customer(0, name, yyyymm, address, usage, userTypeId);
        customerController.addCustomer(customer);

        // Thông báo thêm thành công và xóa nội dung các trường để tiếp tục thêm
        Toast.makeText(this, "Customer added successfully", Toast.LENGTH_SHORT).show();

        // Reset các trường sau khi thêm
        resetInputFields();
    }

    private void resetInputFields() {
        etCustomerName.setText("");
        etCustomerAddress.setText("");
        etElectricUsage.setText("");
        btnPickDate.setText("Select Month/Year");
        spinnerUserType.setSelection(0); // Đặt lại spinner về loại "Private"
    }

    private boolean validateInputs(String name, String address, String usageText, String selectedUserType) {
        if (selectedYear == 0 || selectedMonth == 0) {
            showToast("Please select month and year!");
            return false;
        }

        if (!name.matches("[a-zA-Z\\s]+")) {
            showToast("Customer name can only contain letters and spaces!");
            return false;
        }

        if (!address.matches("[a-zA-Z0-9\\s]+")) {
            showToast("Address can only contain letters, numbers, and spaces!");
            return false;
        }

        if (selectedUserType.equals("Type")) {
            showToast("Please select a customer type!");
            return false;
        }

        if (!usageText.matches("[0-9]+(\\.[0-9]+)?")) {
            showToast("Electric usage can only contain numbers!");
            return false;
        }

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
