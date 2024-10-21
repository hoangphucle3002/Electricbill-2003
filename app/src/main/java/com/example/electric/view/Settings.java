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

    private Switch switchShowAddress, switchShowElectricUsage, switchShowUserType, switchShowPrice, switchMusic;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Initialize Toolbar and add back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        // Bind switches to layout
        switchShowAddress = findViewById(R.id.switchShowAddress);
        switchShowElectricUsage = findViewById(R.id.switchShowElectricUsage);
        switchShowUserType = findViewById(R.id.switchShowUserType);
        switchShowPrice = findViewById(R.id.switchShowPrice);
        switchMusic = findViewById(R.id.switchPlayMusic);

        // Get SharedPreferences to store switch states
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);

        // Restore switch states from SharedPreferences
        switchShowAddress.setChecked(sharedPreferences.getBoolean("showAddress", true));
        switchShowElectricUsage.setChecked(sharedPreferences.getBoolean("showElectricUsage", true));
        switchShowUserType.setChecked(sharedPreferences.getBoolean("showUserType", true));
        switchShowPrice.setChecked(sharedPreferences.getBoolean("showPrice", true));
        switchMusic.setChecked(sharedPreferences.getBoolean("playMusic", false));

        // Save switch state when user changes it
        switchShowAddress.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("showAddress", isChecked).apply();
        });

        switchShowElectricUsage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("showElectricUsage", isChecked).apply();
        });

        switchShowUserType.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("showUserType", isChecked).apply();
        });

        switchShowPrice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("showPrice", isChecked).apply();
        });

        // Handle music on/off switch
        switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("playMusic", isChecked);
            editor.apply();

            if (isChecked) {
                // Start playing music
                Intent intent = new Intent(Settings.this, Music.class);
                startService(intent);
            } else {
                // Stop playing music
                Intent intent = new Intent(Settings.this, Music.class);
                stopService(intent);
            }
        });
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
