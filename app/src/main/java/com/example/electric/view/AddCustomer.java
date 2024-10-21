package com.example.electric.view;

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

        // Thiết lập toolbar
        setupToolbar();

        // Khởi tạo các view
        initViews();

        // Thiết lập các listener cho các sự kiện
        setupListeners();

        // Lấy cấu hình từ SharedPreferences để ẩn/hiện các trường thông tin
        SharedPreferences preferences = getSharedPreferences("settings_prefs", MODE_PRIVATE);
        boolean showAddress = preferences.getBoolean("show_address", true);
        boolean showElectricUsage = preferences.getBoolean("show_electric_usage", true);
        boolean showUserType = preferences.getBoolean("show_user_type", true);

        // Ẩn/hiện các trường dữ liệu tùy thuộc vào cấu hình
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

    // Thiết lập Toolbar
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Customer");
        }
    }

    // Khởi tạo các view
    private void initViews() {
        etCustomerName = findViewById(R.id.etCustomerName);
        etCustomerAddress = findViewById(R.id.etCustomerAddress);
        etElectricUsage = findViewById(R.id.etElectricUsage);
        btnSaveCustomer = findViewById(R.id.btnSaveCustomer);
        btnPickDate = findViewById(R.id.btnPickDate);
        spinnerUserType = findViewById(R.id.spinnerUserType);
        customerController = new CustomerController(this);
    }

    // Thiết lập các sự kiện click
    private void setupListeners() {
        btnPickDate.setOnClickListener(v -> showDatePickerDialog());
        btnSaveCustomer.setOnClickListener(v -> validateAndSaveCustomer());
    }

    // Hiển thị hộp thoại chọn ngày/tháng
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
        yearPicker.setMinValue(2000);  // Năm tối thiểu
        yearPicker.setMaxValue(currentYear);  // Năm hiện tại
        yearPicker.setValue(currentYear);

        // Tạo layout để chứa NumberPicker
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER); // Căn giữa
        layout.setPadding(50, 0, 50, 0);
        layout.addView(monthPicker);

        // Thêm khoảng cách giữa các NumberPicker
        Space space = new Space(this);
        space.setMinimumWidth(20);
        layout.addView(space);

        layout.addView(yearPicker);

        // Tạo AlertDialog để hiển thị NumberPicker
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Month/Year");
        builder.setView(layout);

        // Xử lý sự kiện khi nhấn nút OK
        builder.setPositiveButton("OK", (dialog, which) -> {
            selectedMonth = monthPicker.getValue();
            selectedYear = yearPicker.getValue();

            // Hiển thị tháng và năm đã chọn trên nút
            btnPickDate.setText("Month: " + selectedMonth + " Year: " + selectedYear);
        });

        // Xử lý sự kiện khi nhấn nút Cancel
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // Kiểm tra và lưu khách hàng vào cơ sở dữ liệu
    private void validateAndSaveCustomer() {
        String name = etCustomerName.getText().toString().trim();
        String address = etCustomerAddress.getText().toString().trim();
        String usageText = etElectricUsage.getText().toString().trim();
        String selectedUserType = spinnerUserType.getSelectedItem().toString();

        // Nếu các trường không hợp lệ, dừng quá trình lưu
        if (!validateInputs(name, address, usageText, selectedUserType)) {
            return;
        }

        double usage = Double.parseDouble(usageText);
        int yyyymm = selectedYear * 100 + selectedMonth;
        int userTypeId = selectedUserType.equals("Private") ? 1 : 2;

        // Tạo đối tượng Customer và thêm vào cơ sở dữ liệu
        Customer customer = new Customer(0, name, yyyymm, address, usage, userTypeId);
        customerController.addCustomer(customer);

        // Thông báo thêm thành công và xóa nội dung các trường
        Toast.makeText(this, "Customer added successfully", Toast.LENGTH_SHORT).show();
        resetInputFields();
    }

    // Đặt lại các trường dữ liệu sau khi lưu
    private void resetInputFields() {
        etCustomerName.setText("");
        etCustomerAddress.setText("");
        etElectricUsage.setText("");
        btnPickDate.setText("Select Month/Year");
        spinnerUserType.setSelection(0);
    }

    // Kiểm tra tính hợp lệ của dữ liệu đầu vào
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

    // Hiển thị thông báo toast
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Xử lý sự kiện khi nhấn nút back trên toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
