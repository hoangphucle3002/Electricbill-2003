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
import com.example.electric.model.DatabaseManage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UpdatePrice extends AppCompatActivity {

    // Khai báo các thành phần giao diện
    private EditText etIncreaseAmount;
    private Button btnIncreasePrice, btnDecreasePrice;
    private Spinner spinnerUserType;
    private TextView tvCurrentPrice;
    private DatabaseManage dbHelper;
    private double currentPrice;  // Lưu giá điện hiện tại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_price);  // Ánh xạ layout cho màn hình cập nhật giá điện

        setupToolbar(); // Thiết lập Toolbar
        initViews();    // Khởi tạo các thành phần giao diện
        setupListeners();  // Thiết lập sự kiện cho các nút bấm và spinner

        dbHelper = new DatabaseManage(this);  // Khởi tạo đối tượng quản lý cơ sở dữ liệu
    }

    // Thiết lập Toolbar với nút quay lại
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Update Price");  // Đặt tiêu đề cho Toolbar
        }
    }

    // Khởi tạo các thành phần giao diện
    private void initViews() {
        etIncreaseAmount = findViewById(R.id.etIncreaseAmount);
        btnIncreasePrice = findViewById(R.id.btnIncreasePrice);
        btnDecreasePrice = findViewById(R.id.btnDecreasePrice);
        spinnerUserType = findViewById(R.id.spinnerUserType);
        tvCurrentPrice = findViewById(R.id.tvCurrentPrice);
    }

    // Thiết lập sự kiện cho Spinner và nút bấm
    private void setupListeners() {
        // Xử lý khi chọn loại người dùng từ Spinner
        spinnerUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCurrentPrice();  // Cập nhật giá điện hiện tại khi thay đổi loại người dùng
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không cần làm gì khi không có lựa chọn
            }
        });

        // Xử lý khi nhấn nút tăng giá điện
        btnIncreasePrice.setOnClickListener(v -> increaseElectricUnitPrice());

        // Xử lý khi nhấn nút giảm giá điện
        btnDecreasePrice.setOnClickListener(v -> decreaseElectricUnitPrice());
    }

    // Cập nhật giá điện hiện tại dựa trên loại người dùng
    private void updateCurrentPrice() {
        String selectedUserType = spinnerUserType.getSelectedItem().toString();
        int userTypeId = selectedUserType.equals("Private") ? 1 : 2;  // Xác định loại người dùng
        currentPrice = dbHelper.getUnitPrice(userTypeId);  // Lấy giá điện hiện tại từ cơ sở dữ liệu

        // Hiển thị giá hiện tại dưới dạng format
        tvCurrentPrice.setText("Current Price: " + String.format("%,.0f", currentPrice));
    }

    // Tăng giá điện theo số tiền nhập vào
    private void increaseElectricUnitPrice() {
        String amountText = etIncreaseAmount.getText().toString().trim();

        // Kiểm tra tính hợp lệ của dữ liệu người dùng nhập
        if (amountText.isEmpty()) {
            Toast.makeText(this, "Please enter an amount to increase", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double increaseAmount = Double.parseDouble(amountText);

            // Kiểm tra xem số tiền nhập vào có hợp lệ không
            if (increaseAmount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Xác định loại người dùng từ Spinner
            String selectedUserType = spinnerUserType.getSelectedItem().toString();
            int userTypeId = selectedUserType.equals("Private") ? 1 : 2;

            // Tăng giá điện cho loại người dùng được chọn
            dbHelper.increaseElectricPrice(userTypeId, increaseAmount);

            // Lấy thời gian hiện tại
            String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            // Hiển thị thông báo tăng giá thành công
            String message = "Increased price for " + selectedUserType + ": " + amountText + " VND at " + currentTime;
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            // Cập nhật lại giá điện hiện tại
            updateCurrentPrice();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
        }
    }

    // Giảm giá điện theo số tiền nhập vào
    private void decreaseElectricUnitPrice() {
        String amountText = etIncreaseAmount.getText().toString().trim();

        // Kiểm tra tính hợp lệ của dữ liệu người dùng nhập
        if (amountText.isEmpty()) {
            Toast.makeText(this, "Please enter an amount to decrease", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double decreaseAmount = Double.parseDouble(amountText);

            // Kiểm tra xem số tiền nhập vào có hợp lệ không
            if (decreaseAmount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Xác định loại người dùng từ Spinner
            String selectedUserType = spinnerUserType.getSelectedItem().toString();
            int userTypeId = selectedUserType.equals("Private") ? 1 : 2;

            // Tính giá mới sau khi giảm
            double newPrice = currentPrice - decreaseAmount;

            // Kiểm tra giá không được giảm xuống dưới 0
            if (newPrice < 0) {
                Toast.makeText(this, "Electric price cannot be lower than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // Giảm giá điện cho loại người dùng được chọn
            dbHelper.increaseElectricPrice(userTypeId, -decreaseAmount);

            // Lấy thời gian hiện tại
            String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            // Hiển thị thông báo giảm giá thành công
            String message = "Decreased price for " + selectedUserType + ": " + amountText + " VND at " + currentTime;
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            // Cập nhật lại giá điện hiện tại
            updateCurrentPrice();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
        }
    }

    // Xử lý khi nhấn nút quay lại
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();  // Quay lại màn hình trước đó
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
