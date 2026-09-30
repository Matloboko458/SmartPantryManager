package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText edtIngredientName;
    private EditText edtQuantity;
    private EditText edtUnit;
    private EditText edtExpiryDate;

    private Button btnSaveIngredient;

    private DatabaseHelper databaseHelper;

    private boolean isFormattingDate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_ingredient);

        edtIngredientName = findViewById(R.id.edtIngredientName);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtUnit = findViewById(R.id.edtUnit);
        edtExpiryDate = findViewById(R.id.edtExpiryDate);
        btnSaveIngredient = findViewById(R.id.btnSaveIngredient);

        databaseHelper = new DatabaseHelper(this);

        edtExpiryDate.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (isFormattingDate) {
                    return;
                }

                isFormattingDate = true;

                String digits = s.toString()
                        .replace("/", "")
                        .replaceAll("[^0-9]", "");

                if (digits.length() > 8) {
                    digits = digits.substring(0, 8);
                }

                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digits.length(); i++) {

                    if (i == 2 || i == 4) {
                        formatted.append("/");
                    }

                    formatted.append(digits.charAt(i));
                }

                edtExpiryDate.setText(formatted.toString());

                edtExpiryDate.setSelection(
                        edtExpiryDate.length()
                );

                isFormattingDate = false;
            }
        });

        btnSaveIngredient.setOnClickListener(
                view -> saveIngredient()
        );
    }

    private void saveIngredient() {

        String ingredientName =
                edtIngredientName.getText()
                        .toString()
                        .trim();

        String quantity =
                edtQuantity.getText()
                        .toString()
                        .trim();

        String unit =
                edtUnit.getText()
                        .toString()
                        .trim();

        String expiryDate =
                edtExpiryDate.getText()
                        .toString()
                        .trim();

        if (ingredientName.isEmpty()
                || quantity.isEmpty()
                || unit.isEmpty()
                || expiryDate.isEmpty()) {

            Toast.makeText(
                    AddIngredientActivity.this,
                    "Please complete all fields.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (!expiryDate.matches("\\d{2}/\\d{2}/\\d{4}")) {

            Toast.makeText(
                    AddIngredientActivity.this,
                    "Enter the expiry date as DD/MM/YYYY.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String[] dateParts = expiryDate.split("/");

        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int year = Integer.parseInt(dateParts[2]);

        if (day < 1 || day > 31
                || month < 1 || month > 12
                || year < 1) {

            Toast.makeText(
                    AddIngredientActivity.this,
                    "Please enter a valid expiry date.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        long result = databaseHelper.addIngredient(
                ingredientName,
                quantity,
                unit,
                expiryDate
        );

        if (result != -1) {

            Toast.makeText(
                    AddIngredientActivity.this,
                    "Ingredient added successfully.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    AddIngredientActivity.this,
                    "Failed to add ingredient.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}