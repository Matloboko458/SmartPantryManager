package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText editIngredientName;
    private EditText editQuantity;
    private EditText editUnit;
    private EditText editExpiryDate;

    private Button btnSaveIngredient;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_ingredient);

        editIngredientName =
                findViewById(R.id.editIngredientName);

        editQuantity =
                findViewById(R.id.editQuantity);

        editUnit =
                findViewById(R.id.editUnit);

        editExpiryDate =
                findViewById(R.id.editExpiryDate);

        btnSaveIngredient =
                findViewById(R.id.btnSaveIngredient);

        databaseHelper =
                new DatabaseHelper(this);

        editExpiryDate.addTextChangedListener(
                new TextWatcher() {

                    private boolean isFormatting = false;

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
                    public void afterTextChanged(
                            Editable editable
                    ) {

                        if (isFormatting) {
                            return;
                        }

                        isFormatting = true;

                        String digits =
                                editable.toString()
                                        .replace("/", "")
                                        .replace(" ", "");

                        if (digits.length() > 8) {
                            digits =
                                    digits.substring(0, 8);
                        }

                        StringBuilder formattedDate =
                                new StringBuilder();

                        for (int i = 0;
                             i < digits.length();
                             i++) {

                            if (i == 2 || i == 4) {
                                formattedDate.append("/");
                            }

                            formattedDate.append(
                                    digits.charAt(i)
                            );
                        }

                        editable.replace(
                                0,
                                editable.length(),
                                formattedDate.toString()
                        );

                        isFormatting = false;
                    }
                }
        );

        btnSaveIngredient.setOnClickListener(
                view -> saveIngredient()
        );
    }

    private void saveIngredient() {

        String ingredientName =
                editIngredientName
                        .getText()
                        .toString()
                        .trim();

        String quantity =
                editQuantity
                        .getText()
                        .toString()
                        .trim();

        String unit =
                editUnit
                        .getText()
                        .toString()
                        .trim();

        String expiryDate =
                editExpiryDate
                        .getText()
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

        String digitsOnly =
                expiryDate.replace("/", "");

        if (digitsOnly.length() != 8) {

            Toast.makeText(
                    AddIngredientActivity.this,
                    "Enter the expiry date as DDMMYYYY.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        long result =
                databaseHelper.addIngredient(
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