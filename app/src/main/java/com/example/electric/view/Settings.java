package com.example.electric.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.electric.R;
import com.example.electric.service.Music;

public class Settings extends AppCompatActivity {

    // Khai báo các thành phần giao diện
    private Switch switchShowAddress, switchShowElectricUsage, switchShowUserType, switchShowPrice, switchMusic;
    private SharedPreferences sharedPreferences; // Để lưu trữ trạng thái của các switch

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings); // Ánh xạ giao diện "settings"

        setupToolbar(); // Thiết lập Toolbar
        bindViews(); // Ánh xạ các Switch từ giao diện
        setupSharedPreferences(); // Khởi tạo và lấy trạng thái từ SharedPreferences
        setupSwitchListeners(); // Thiết lập các sự kiện thay đổi cho Switch
    }

    // Phương thức thiết lập Toolbar với nút quay lại
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings"); // Đặt tiêu đề cho Toolbar
        }
    }

    // Ánh xạ các thành phần giao diện (Switch)
    private void bindViews() {
        switchShowAddress = findViewById(R.id.switchShowAddress);
        switchShowElectricUsage = findViewById(R.id.switchShowElectricUsage);
        switchShowUserType = findViewById(R.id.switchShowUserType);
        switchShowPrice = findViewById(R.id.switchShowPrice);
        switchMusic = findViewById(R.id.switchPlayMusic);
    }

    // Thiết lập SharedPreferences để lưu và khôi phục trạng thái của các switch
    private void setupSharedPreferences() {
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);

        // Khôi phục trạng thái của switch từ SharedPreferences
        switchShowAddress.setChecked(sharedPreferences.getBoolean("showAddress", true));
        switchShowElectricUsage.setChecked(sharedPreferences.getBoolean("showElectricUsage", true));
        switchShowUserType.setChecked(sharedPreferences.getBoolean("showUserType", true));
        switchShowPrice.setChecked(sharedPreferences.getBoolean("showPrice", true));
        switchMusic.setChecked(sharedPreferences.getBoolean("playMusic", false));
    }

    // Thiết lập các sự kiện khi người dùng thay đổi trạng thái của switch
    private void setupSwitchListeners() {
        // Lưu trạng thái của switch vào SharedPreferences khi thay đổi
        switchShowAddress.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferences.edit().putBoolean("showAddress", isChecked).apply());

        switchShowElectricUsage.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferences.edit().putBoolean("showElectricUsage", isChecked).apply());

        switchShowUserType.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferences.edit().putBoolean("showUserType", isChecked).apply());

        switchShowPrice.setOnCheckedChangeListener((buttonView, isChecked) ->
                sharedPreferences.edit().putBoolean("showPrice", isChecked).apply());

        // Bật/tắt nhạc khi switchMusic thay đổi
        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("playMusic", isChecked);
            editor.apply();

            // Nếu bật, bắt đầu phát nhạc
            if (isChecked) {
                startService(new Intent(Settings.this, Music.class));
            } else {
                // Nếu tắt, dừng nhạc
                stopService(new Intent(Settings.this, Music.class));
            }
        });
    }

    // Xử lý sự kiện khi người dùng nhấn vào nút "Back"
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Quay lại màn hình trước đó
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
