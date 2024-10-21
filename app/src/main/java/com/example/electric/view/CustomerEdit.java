package com.example.electric.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.Space;
import android.view.Gravity;

import com.example.electric.R;
import com.example.electric.model.DatabaseManage;

import java.util.Calendar;

public class CustomerEdit extends AppCompatActivity {

    private EditText etCustomerName, etCustomerAddress, etElectricUsage;
    private TextView tvBillingMonthYear;
    private Spinner spinnerUserType;
    private Button btnSaveCustomer, btnSelectMonthYear;
    private DatabaseManage dbHelper;
    private int customerId; // ID của khách hàng đang chỉnh sửa

    private SharedPreferences sharedPreferences;
    private int selectedYear, selectedMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_customer);
        setupToolbar(); // Thiết lập thanh công cụ (toolbar)

        // Khởi tạo các view
        etCustomerName = findViewById(R.id.etCustomerName);
        etCustomerAddress = findViewById(R.id.etCustomerAddress);
        etElectricUsage = findViewById(R.id.etElectricUsage);
        tvBillingMonthYear = findViewById(R.id.tvBillingMonthYear);
        spinnerUserType = findViewById(R.id.spinnerUserType);
        btnSaveCustomer = findViewById(R.id.btnSaveCustomer);
        btnSelectMonthYear = findViewById(R.id.btnSelectMonthYear);

        dbHelper = new DatabaseManage(this);
        sharedPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE);

        // Lấy các tùy chọn hiển thị từ SharedPreferences
        boolean showAddress = sharedPreferences.getBoolean("show_address", true);
        boolean showElectricUsage = sharedPreferences.getBoolean("show_electric_usage", true);
        boolean showUserType = sharedPreferences.getBoolean("show_user_type", true);

        // Hiển thị hoặc ẩn các view dựa trên cấu hình
        etCustomerAddress.setVisibility(showAddress ? View.VISIBLE : View.GONE);
        etElectricUsage.setVisibility(showElectricUsage ? View.VISIBLE : View.GONE);
        spinnerUserType.setVisibility(showUserType ? View.VISIBLE : View.GONE);

        // Lấy dữ liệu từ Intent và hiển thị lên các view
        if (getIntent() != null && getIntent().getExtras() != null) {
            customerId = getIntent().getExtras().getInt("customerId");
            String customerName = getIntent().getExtras().getString("customerName");
            String customerAddress = getIntent().getExtras().getString("customerAddress");
            double electricUsage = getIntent().getExtras().getDouble("electricUsage");
            int yyyymm = getIntent().getExtras().getInt("yyyymm");
            int userTypeId = getIntent().getExtras().getInt("userTypeId");

            // Hiển thị dữ liệu lên các view
            etCustomerName.setText(customerName);
            etCustomerAddress.setText(customerAddress);
            etElectricUsage.setText(String.valueOf(electricUsage));

            // Hiển thị ngày tháng năm theo định dạng "Month: Year"
            int year = yyyymm / 100;
            int month = yyyymm % 100;
            String formattedDate = "Month: " + month + " Year: " + year;
            tvBillingMonthYear.setText(formattedDate);

            spinnerUserType.setSelection(userTypeId == 1 ? 0 : 1);
        }

        // Xử lý sự kiện khi người dùng chọn tháng/năm
        btnSelectMonthYear.setOnClickListener(v -> showDatePickerDialog());

        // Xử lý sự kiện khi người dùng nhấn nút lưu khách hàng
        btnSaveCustomer.setOnClickListener(v -> {
            if (validateInput()) {
                saveCustomer(); // Lưu khách hàng sau khi chỉnh sửa
            }
        });
    }

    // Thiết lập toolbar
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Customer");
        }
    }

    // Hiển thị hộp thoại chọn tháng/năm với NumberPicker
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        // Tạo NumberPicker cho tháng
        final NumberPicker monthPicker = new NumberPicker(this);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(currentMonth);

        // Tạo NumberPicker cho năm
        final NumberPicker yearPicker = new NumberPicker(this);
        yearPicker.setMinValue(2000);
        yearPicker.setMaxValue(currentYear);
        yearPicker.setValue(currentYear);

        // Tạo layout để chứa NumberPickers và căn giữa
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(50, 0, 50, 0);
        layout.addView(monthPicker);

        // Thêm khoảng cách giữa 2 NumberPickers
        Space space = new Space(this);
        space.setMinimumWidth(20);
        layout.addView(space);

        layout.addView(yearPicker);

        // Hiển thị AlertDialog chứa NumberPickers
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Month/Year");
        builder.setView(layout);
        builder.setPositiveButton("OK", (dialog, which) -> {
            selectedMonth = monthPicker.getValue();
            selectedYear = yearPicker.getValue();
            tvBillingMonthYear.setText("Month: " + selectedMonth + " Year: " + selectedYear);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // Lưu thông tin khách hàng sau khi chỉnh sửa
    private void saveCustomer() {
        String name = etCustomerName.getText().toString().trim();
        String address = etCustomerAddress.getText().toString().trim();
        String usageText = etElectricUsage.getText().toString().trim();
        String billingMonthYearText = tvBillingMonthYear.getText().toString().trim();
        int userTypeId = spinnerUserType.getSelectedItem().toString().equals("Private") ? 1 : 2;

        double electricUsage;
        int yyyymm;
        try {
            electricUsage = Double.parseDouble(usageText);
            String[] parts = billingMonthYearText.split(" ");
            int month = Integer.parseInt(parts[1].replace("Month:", "").trim());
            int year = Integer.parseInt(parts[3].replace("Year:", "").trim());
            yyyymm = year * 100 + month;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid electric usage or billing month", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.updateCustomer(customerId, name, address, electricUsage, yyyymm, userTypeId);
        Toast.makeText(this, "Customer updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    // Xác thực các trường nhập liệu
    private boolean validateInput() {
        String name = etCustomerName.getText().toString().trim();
        String address = etCustomerAddress.getText().toString().trim();
        String usageText = etElectricUsage.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter customer name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (name.matches(".*\\d.*")) {
            Toast.makeText(this, "Customer name cannot contain numbers", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (address.isEmpty()) {
            Toast.makeText(this, "Please enter customer address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (usageText.isEmpty()) {
            Toast.makeText(this, "Please enter electric usage", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double usage = Double.parseDouble(usageText);
            if (usage < 0) {
                Toast.makeText(this, "Electric usage must be greater than 0", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid electric usage", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Xử lý sự kiện nhấn nút back trên toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
