package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREF_NAME = "SmartPantryPrefs";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_PROFILE_IMAGE = "profile_image_uri";

    private TextView txtProfileName;
    private TextView txtProfileEmail;
    private TextView txtPantryInfoCount;
    private ImageView imgProfile;
    private DatabaseHelper databaseHelper;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

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
        imgProfile = findViewById(R.id.imgProfile);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            try {
                                getContentResolver().takePersistableUriPermission(
                                        imageUri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                            prefs.edit().putString(KEY_PROFILE_IMAGE, imageUri.toString()).apply();

                            imgProfile.setImageURI(imageUri);
                            Toast.makeText(this, "Profile picture updated.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        imgProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

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
        String imageUriStr = prefs.getString(KEY_PROFILE_IMAGE, null);

        txtProfileName.setText(name);
        txtProfileEmail.setText(email);

        if (imageUriStr != null && !imageUriStr.isEmpty()) {
            try {
                imgProfile.setImageURI(Uri.parse(imageUriStr));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
