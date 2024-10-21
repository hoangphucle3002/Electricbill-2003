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
import com.example.electric.service.MusicService;

public class MainActivity extends AppCompatActivity {

    private Button btnAddCustomer, btnViewCustomerList, btnUpdatePrice, btnSettings, btnIncreasePrice;
    private SharedPreferences sharedPreferences;
    private ViewPager2 viewPager2;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int currentPage = 0;
    private int[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupToolbar();
        super.onCreate(savedInstanceState);
         setContentView(R.layout.main);

        // Initialize ViewPager2
        viewPager2 = findViewById(R.id.viewPager2);

        if (viewPager2 == null) {
            throw new NullPointerException("ViewPager2 is not initialized. Check if you have the correct ID in the XML layout.");
        }

        // Set adapter for ViewPager2
        ViewPagerAdapter adapter = new ViewPagerAdapter(images);
        viewPager2.setAdapter(adapter);
        viewPager2.setPageTransformer(new DepthPageTransformer());

        // Start auto slide
        startAutoSlide();

        // Initialize buttons
        btnAddCustomer = findViewById(R.id.btnAddCustomer);
        btnViewCustomerList = findViewById(R.id.btnViewCustomerList);
        btnSettings = findViewById(R.id.btnSettings);
        btnIncreasePrice = findViewById(R.id.btnIncreasePrice);

        // Set button click listeners
        btnAddCustomer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddCustomer.class);
            startActivity(intent);
        });

        btnViewCustomerList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CustomerList.class);
            startActivity(intent);
        });

        btnIncreasePrice.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UpdatePrice.class);
            startActivity(intent);
        });

        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isMusicPlaying = sharedPreferences.getBoolean("isMusicPlaying", false);
        if (isMusicPlaying) {
            startService(new Intent(MainActivity.this, MusicService.class));
        }

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
        });
    }

    private void startAutoSlide() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (viewPager2.getAdapter() != null) {
                    if (currentPage == viewPager2.getAdapter().getItemCount()) {
                        currentPage = 0;
                    }
                    viewPager2.setCurrentItem(currentPage++, true);
                    handler.postDelayed(this, 2000); // Change image every 2 seconds
                }
            }
        };

        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Stop auto slide when activity is destroyed
    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Electric Management");
        }
    }
}
