package com.example.electric.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
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
import com.example.electric.service.MusicService;

import java.util.Calendar;

public class CustomerEdit extends AppCompatActivity {

    private EditText etCustomerName, etCustomerAddress, etElectricUsage;
    private TextView tvBillingMonthYear;
    private Spinner spinnerUserType;
    private Button btnSaveCustomer, btnSelectMonthYear;
    private DatabaseManage dbHelper;
    private int customerId; // ID of the customer being edited

    private SharedPreferences sharedPreferences;
    private boolean isMusicPlaying = false;
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    private int selectedYear, selectedMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_customer);
        setupToolbar();
        // Setup toolbar and back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable back button
        }

        // Initialize views
        etCustomerName = findViewById(R.id.etCustomerName);
        etCustomerAddress = findViewById(R.id.etCustomerAddress);
        etElectricUsage = findViewById(R.id.etElectricUsage);
        tvBillingMonthYear = findViewById(R.id.tvBillingMonthYear);
        spinnerUserType = findViewById(R.id.spinnerUserType);
        btnSaveCustomer = findViewById(R.id.btnSaveCustomer);
        btnSelectMonthYear = findViewById(R.id.btnSelectMonthYear);

        dbHelper = new DatabaseManage(this);
        sharedPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE);

        // Music management
        setupMusicControl();

        // Get visibility preferences from SharedPreferences
        boolean showAddress = sharedPreferences.getBoolean("show_address", true);
        boolean showElectricUsage = sharedPreferences.getBoolean("show_electric_usage", true);
        boolean showUserType = sharedPreferences.getBoolean("show_user_type", true);

        if (!showAddress) {
            etCustomerAddress.setVisibility(View.GONE);
        }
        if (!showElectricUsage) {
            etElectricUsage.setVisibility(View.GONE);
        }
        if (!showUserType) {
            spinnerUserType.setVisibility(View.GONE);
        }

        // Get data from Intent
        if (getIntent() != null && getIntent().getExtras() != null) {
            customerId = getIntent().getExtras().getInt("customerId");
            String customerName = getIntent().getExtras().getString("customerName");
            String customerAddress = getIntent().getExtras().getString("customerAddress");
            double electricUsage = getIntent().getExtras().getDouble("electricUsage");
            int yyyymm = getIntent().getExtras().getInt("yyyymm");
            int userTypeId = getIntent().getExtras().getInt("userTypeId");

            // Set data to views
            etCustomerName.setText(customerName);
            etCustomerAddress.setText(customerAddress);
            etElectricUsage.setText(String.valueOf(electricUsage));

            // Show formatted date in "Month: Year" format
            int year = yyyymm / 100;
            int month = yyyymm % 100;
            String formattedDate = "Month: " + month + " Year: " + year;
            tvBillingMonthYear.setText(formattedDate);

            spinnerUserType.setSelection(userTypeId == 1 ? 0 : 1);
        }

        // Handle month/year picker button click
        btnSelectMonthYear.setOnClickListener(v -> showDatePickerDialog());

        // Handle save button click
        btnSaveCustomer.setOnClickListener(v -> {
            if (validateInput()) {
                saveCustomer();
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit Customer");
        }
    }
    private void setupMusicControl() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        AudioManager.OnAudioFocusChangeListener afChangeListener = focusChange -> {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // Stop music if we lose audio focus (another app playing sound)
                stopMusic();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume music when we regain audio focus
                if (isMusicPlaying) {
                    startMusic();
                }
            }
        };

        // Request audio focus for playback
        int result = audioManager.requestAudioFocus(afChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED && sharedPreferences.getBoolean("playMusic", false)) {
            startMusic();
        }
    }

    private void startMusic() {
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        isMusicPlaying = true;
    }

    private void stopMusic() {
        Intent intent = new Intent(this, MusicService.class);
        stopService(intent);
        isMusicPlaying = false;
    }

    // Show month/year picker dialog with NumberPicker
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

            // Display the selected month and year on the TextView
            tvBillingMonthYear.setText("Month: " + selectedMonth + " Year: " + selectedYear);
        });

        // When the user clicks "Cancel"
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // Save customer details after editing
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

            // Convert "Month: Year" format back to yyyymm integer
            String[] parts = billingMonthYearText.split(" ");
            int month = Integer.parseInt(parts[1].replace("Month:", "").trim());
            int year = Integer.parseInt(parts[3].replace("Year:", "").trim());
            yyyymm = year * 100 + month;

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid electric usage or billing month", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save customer to database
        dbHelper.updateCustomer(customerId, name, address, electricUsage, yyyymm, userTypeId);
        Toast.makeText(this, "Customer updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    // Validate input fields
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
