package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREF_NAME = "SmartPantryPrefs";
    private static final String KEY_NOTIFICATIONS = "notifications_enabled";
    private static final String KEY_EXPIRY_REMINDERS = "expiry_reminders_enabled";

    private SwitchCompat switchNotifications;
    private SwitchCompat switchExpiryReminders;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);

        Button btnBack = findViewById(R.id.btnBack);
        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnAbout = findViewById(R.id.btnAbout);
        Button btnResetPantry = findViewById(R.id.btnResetPantry);

        switchNotifications = findViewById(R.id.switchNotifications);
        switchExpiryReminders = findViewById(R.id.switchExpiryReminders);

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        switchNotifications.setChecked(prefs.getBoolean(KEY_NOTIFICATIONS, true));
        switchExpiryReminders.setChecked(prefs.getBoolean(KEY_EXPIRY_REMINDERS, true));

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_NOTIFICATIONS, isChecked).apply();
            Toast.makeText(this, "Notifications " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
        });

        switchExpiryReminders.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_EXPIRY_REMINDERS, isChecked).apply();
            Toast.makeText(this, "Expiry Reminders " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> finish());

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnAbout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("About Smart Pantry")
                    .setMessage("Smart Pantry Manager v1.0\n\nKeep your food organised, reduce waste, and discover delicious recipes you can make with what you have in your pantry.")
                    .setPositiveButton("Close", null)
                    .show();
        });

        btnResetPantry.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Reset Pantry Data")
                    .setMessage("Are you sure you want to delete all items in your pantry? This action cannot be undone.")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Reset", (dialog, which) -> {
                        SQLiteDatabase db = databaseHelper.getWritableDatabase();
                        db.delete(DatabaseHelper.TABLE_INGREDIENTS, null, null);
                        db.close();
                        Toast.makeText(this, "Pantry data reset successfully.", Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });
    }
}
