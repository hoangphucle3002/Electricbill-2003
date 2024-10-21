package com.example.electric.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.electric.R;
import com.example.electric.model.Customer;
import com.example.electric.model.DatabaseManage;

import java.util.ArrayList;
import java.util.List;

public class CustomerList extends AppCompatActivity {

    // Khai báo các thành phần giao diện và biến cần thiết
    private RecyclerView recyclerView; // Danh sách khách hàng
    private CustomerAdapter customerAdapter; // Adapter để quản lý hiển thị danh sách
    private List<Customer> customerList = new ArrayList<>(); // Danh sách khách hàng
    private DatabaseManage dbHelper; // Đối tượng quản lý cơ sở dữ liệu
    private EditText etSearch;  // Ô tìm kiếm
    private TextView tvNoResults; // TextView hiển thị khi không tìm thấy kết quả
    private ActivityResultLauncher<Intent> editCustomerLauncher; // Quản lý kết quả từ việc chỉnh sửa khách hàng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_list);

        // Khởi tạo cơ sở dữ liệu
        dbHelper = new DatabaseManage(this);

        // Cấu hình giao diện và các chức năng
        setupToolbar();  // Thiết lập toolbar
        setupRecyclerView(); // Thiết lập RecyclerView
        setupSearch();  // Thiết lập chức năng tìm kiếm
        setupActivityResultLauncher(); // Thiết lập ActivityResultLauncher cho chỉnh sửa khách hàng

        // Tải dữ liệu khách hàng ban đầu
        loadCustomerData();
    }

    // Thiết lập thanh toolbar
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Nút quay lại
            getSupportActionBar().setTitle("Customer Lists"); // Tiêu đề
        }
    }

    // Thiết lập RecyclerView để hiển thị danh sách khách hàng
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView); // Ánh xạ RecyclerView từ giao diện
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Sử dụng LinearLayoutManager cho danh sách
        customerAdapter = new CustomerAdapter(customerList, this, dbHelper); // Khởi tạo adapter cho danh sách
        recyclerView.setAdapter(customerAdapter); // Gắn adapter vào RecyclerView
        tvNoResults = findViewById(R.id.tvNoResults); // TextView hiển thị khi không có kết quả
    }

    // Thiết lập chức năng tìm kiếm khách hàng
    private void setupSearch() {
        etSearch = findViewById(R.id.etSearch); // Ánh xạ EditText của thanh tìm kiếm
        Button searchButton = findViewById(R.id.btnSearch); // Nút tìm kiếm
        Button resetButton = findViewById(R.id.btnReset); // Nút đặt lại tìm kiếm

        // Xử lý khi nhấn nút tìm kiếm
        searchButton.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                searchCustomers(query); // Tìm kiếm khách hàng với từ khóa
            } else {
                loadCustomerData(); // Nếu từ khóa rỗng, tải lại toàn bộ khách hàng
            }
        });

        // Xử lý khi nhấn nút đặt lại
        resetButton.setOnClickListener(v -> {
            etSearch.setText(""); // Xóa nội dung tìm kiếm
            loadCustomerData(); // Tải lại toàn bộ danh sách khách hàng
        });
    }

    // Tải dữ liệu khách hàng từ cơ sở dữ liệu và hiển thị
    @SuppressLint("Range")
    private void loadCustomerData() {
        customerList.clear(); // Xóa danh sách hiện tại
        try (Cursor cursor = dbHelper.getAllCustomers()) { // Lấy tất cả khách hàng từ cơ sở dữ liệu
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Lấy dữ liệu từ cursor và tạo đối tượng Customer
                    int id = cursor.getInt(cursor.getColumnIndex("ID"));
                    String name = cursor.getString(cursor.getColumnIndex("NAME"));
                    String address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
                    double electricUsage = cursor.getDouble(cursor.getColumnIndex("USED_NUM_ELECTRIC"));
                    int yyyymm = cursor.getInt(cursor.getColumnIndex("YYYYMM"));
                    int userTypeId = cursor.getInt(cursor.getColumnIndex("ELEC_USER_TYPE_ID"));

                    // Thêm đối tượng Customer vào danh sách
                    customerList.add(new Customer(id, name, yyyymm, address, electricUsage, userTypeId));
                } while (cursor.moveToNext());
            }
        }

        // Cập nhật giao diện dựa trên dữ liệu khách hàng
        if (customerList.isEmpty()) {
            recyclerView.setVisibility(View.GONE); // Ẩn danh sách nếu không có dữ liệu
            tvNoResults.setVisibility(View.VISIBLE); // Hiển thị thông báo "Không có kết quả"
        } else {
            recyclerView.setVisibility(View.VISIBLE); // Hiển thị danh sách nếu có dữ liệu
            tvNoResults.setVisibility(View.GONE); // Ẩn thông báo "Không có kết quả"
        }

        customerAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu cho adapter
    }

    // Tìm kiếm khách hàng theo từ khóa
    private void searchCustomers(String query) {
        customerList.clear(); // Xóa danh sách hiện tại
        List<Customer> searchResults = dbHelper.searchCustomers(query); // Tìm kiếm khách hàng từ cơ sở dữ liệu
        customerList.addAll(searchResults); // Thêm kết quả tìm kiếm vào danh sách

        // Cập nhật giao diện dựa trên kết quả tìm kiếm
        if (customerList.isEmpty()) {
            recyclerView.setVisibility(View.GONE); // Ẩn danh sách nếu không có kết quả
            tvNoResults.setVisibility(View.VISIBLE); // Hiển thị thông báo "Không có kết quả"
        } else {
            recyclerView.setVisibility(View.VISIBLE); // Hiển thị danh sách nếu có kết quả
            tvNoResults.setVisibility(View.GONE); // Ẩn thông báo "Không có kết quả"
        }

        customerAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu cho adapter
    }

    // Thiết lập ActivityResultLauncher để xử lý kết quả từ CustomerEdit
    private void setupActivityResultLauncher() {
        editCustomerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadCustomerData(); // Nếu khách hàng được chỉnh sửa, tải lại dữ liệu
                    }
                }
        );
    }

    // Xử lý sự kiện khi nhấn nút quay lại trên toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Quay lại màn hình trước đó
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Tải lại dữ liệu khi Activity quay trở lại trạng thái hoạt động
    @Override
    protected void onResume() {
        super.onResume();
        loadCustomerData(); // Tải lại dữ liệu để đảm bảo danh sách luôn cập nhật
    }
}
