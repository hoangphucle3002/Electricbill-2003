package com.example.electric.view;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import com.example.electric.R;
import com.example.electric.service.Music;

public class MainActivity extends AppCompatActivity {

    // Khai báo các thành phần giao diện và biến cần thiết
    private Button btnAddCustomer, btnViewCustomerList, btnSettings, btnIncreasePrice;
    private SharedPreferences sharedPreferences;
    private ViewPager2 viewPager2;
    private Handler handler = new Handler(Looper.getMainLooper()); // Xử lý luồng cho auto slide
    private int currentPage = 0; // Trang hiện tại của ViewPager
    private final int[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5}; // Danh sách ảnh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); // Ánh xạ giao diện chính

        setupToolbar(); // Thiết lập thanh toolbar
        initViewPager(); // Khởi tạo ViewPager2
        initButtons(); // Khởi tạo các nút bấm

        // Kiểm tra trạng thái nhạc và khởi động nhạc nếu cần
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isMusicPlaying = sharedPreferences.getBoolean("isMusicPlaying", false);
        if (isMusicPlaying) {
            startService(new Intent(MainActivity.this, Music.class));
        }
    }

    // Khởi tạo ViewPager và tự động slide
    private void initViewPager() {
        viewPager2 = findViewById(R.id.viewPager2); // Ánh xạ ViewPager2 từ layout

        // Kiểm tra nếu ViewPager2 không được ánh xạ đúng
        if (viewPager2 == null) {
            throw new NullPointerException("ViewPager2 không được khởi tạo đúng cách. Kiểm tra ID trong layout XML.");
        }

        // Thiết lập adapter cho ViewPager2
        ViewPagerAdapter adapter = new ViewPagerAdapter(images);
        viewPager2.setAdapter(adapter);
        viewPager2.setPageTransformer(new DepthPageTransformer()); // Thiết lập hiệu ứng chuyển trang

        startAutoSlide(); // Bắt đầu tự động slide
    }

    // Khởi tạo các nút và thiết lập sự kiện click
    private void initButtons() {
        btnAddCustomer = findViewById(R.id.btnAddCustomer); // Ánh xạ nút thêm khách hàng
        btnViewCustomerList = findViewById(R.id.btnViewCustomerList); // Ánh xạ nút xem danh sách khách hàng
        btnSettings = findViewById(R.id.btnSettings); // Ánh xạ nút cài đặt
        btnIncreasePrice = findViewById(R.id.btnIncreasePrice); // Ánh xạ nút tăng giá

        // Sự kiện khi nhấn vào nút "Add Customer"
        btnAddCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddCustomer.class);
            startActivity(intent); // Chuyển sang màn hình thêm khách hàng
        });

        // Sự kiện khi nhấn vào nút "View Customer List"
        btnViewCustomerList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CustomerList.class);
            startActivity(intent); // Chuyển sang màn hình danh sách khách hàng
        });

        // Sự kiện khi nhấn vào nút "Increase Price"
        btnIncreasePrice.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UpdatePrice.class);
            startActivity(intent); // Chuyển sang màn hình cập nhật giá điện
        });

        // Sự kiện khi nhấn vào nút "Settings"
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent); // Chuyển sang màn hình cài đặt
        });
    }

    // Bắt đầu tự động slide hình ảnh trong ViewPager2
    private void startAutoSlide() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (viewPager2.getAdapter() != null) {
                    if (currentPage == viewPager2.getAdapter().getItemCount()) {
                        currentPage = 0; // Nếu hết trang, quay lại trang đầu tiên
                    }
                    viewPager2.setCurrentItem(currentPage++, true); // Chuyển sang trang tiếp theo
                    handler.postDelayed(this, 2000); // Đổi ảnh sau mỗi 2 giây
                }
            }
        };
        handler.post(runnable); // Bắt đầu tự động slide
    }

    // Hủy bỏ slide khi Activity bị hủy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Ngừng auto slide khi Activity bị hủy
    }

  
}
