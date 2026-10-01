package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREF_NAME = "SmartPantryPrefs";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_EMAIL = "user_email";

    private TextView txtProfileName;
    private TextView txtProfileEmail;
    private TextView txtPantryInfoCount;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        databaseHelper = new DatabaseHelper(this);

        Button btnBack = findViewById(R.id.btnBack);
        Button btnEditProfile = findViewById(R.id.btnEditProfile);

        txtProfileName = findViewById(R.id.txtProfileName);
        txtProfileEmail = findViewById(R.id.txtProfileEmail);
        txtPantryInfoCount = findViewById(R.id.txtPantryInfoCount);

        btnBack.setOnClickListener(v -> finish());
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        loadProfileData();
        loadPantrySummary();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantrySummary();
    }

    private void loadProfileData() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String name = prefs.getString(KEY_NAME, "John Doe");
        String email = prefs.getString(KEY_EMAIL, "john.doe@example.com");

        txtProfileName.setText(name);
        txtProfileEmail.setText(email);
    }

    private void loadPantrySummary() {
        int count = databaseHelper.getIngredientCount();
        txtPantryInfoCount.setText("Total Items in Pantry: " + count);
    }

    private void showEditProfileDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        final EditText nameInput = new EditText(this);
        nameInput.setHint("Name");
        nameInput.setSingleLine(true);
        nameInput.setText(txtProfileName.getText().toString());
        layout.addView(nameInput);

        final EditText emailInput = new EditText(this);
        emailInput.setHint("Email");
        emailInput.setSingleLine(true);
        emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setText(txtProfileEmail.getText().toString());
        layout.addView(emailInput);

        new AlertDialog.Builder(this)
                .setTitle("Edit Profile")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = nameInput.getText().toString().trim();
                    String newEmail = emailInput.getText().toString().trim();

                    if (newName.isEmpty() || newEmail.isEmpty()) {
                        Toast.makeText(this, "Fields cannot be empty.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                    prefs.edit()
                            .putString(KEY_NAME, newName)
                            .putString(KEY_EMAIL, newEmail)
                            .apply();

                    loadProfileData();
                    Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
