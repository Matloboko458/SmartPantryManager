package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "SmartPantryPrefs";
    private static final String KEY_DARK_MODE = "dark_mode";

    private DatabaseHelper databaseHelper;

    private RecyclerView recyclerViewIngredients;
    private IngredientAdapter ingredientAdapter;
    private final List<IngredientModel> ingredientList = new ArrayList<>();

    private LinearLayout emptyState;
    private TextView txtItemCount;
    private EditText searchPantry;

    private Button btnAll;
    private Button btnFresh;
    private Button btnDryGoods;
    private Button btnDairy;
    private Button btnRecipes;
    private Button btnAddIngredient;
    private Button btnThemeMain;

    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        recyclerViewIngredients = findViewById(R.id.recyclerViewIngredients);
        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(this));

        ingredientAdapter = new IngredientAdapter(this, ingredientList, new IngredientAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(IngredientModel ingredient) {
                showEditIngredientDialog(
                        ingredient.getId(),
                        ingredient.getName(),
                        ingredient.getQuantity(),
                        ingredient.getUnit(),
                        ingredient.getExpiryDate()
                );
            }

            @Override
            public void onDeleteClick(IngredientModel ingredient) {
                confirmDeleteIngredient(
                        ingredient.getId(),
                        ingredient.getName()
                );
            }
        });
        recyclerViewIngredients.setAdapter(ingredientAdapter);

        emptyState = findViewById(R.id.emptyState);
        txtItemCount = findViewById(R.id.txtItemCount);
        searchPantry = findViewById(R.id.searchPantry);

        btnAll = findViewById(R.id.btnAll);
        btnFresh = findViewById(R.id.btnFresh);
        btnDryGoods = findViewById(R.id.btnDryGoods);
        btnDairy = findViewById(R.id.btnDairy);
        btnRecipes = findViewById(R.id.btnRecipes);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);

        Button btnProfileMain = findViewById(R.id.btnProfileMain);
        Button btnSettingsMain = findViewById(R.id.btnSettingsMain);
        btnThemeMain = findViewById(R.id.btnThemeMain);

        updateThemeButtonText(isDarkMode);

        btnProfileMain.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        btnSettingsMain.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });

        btnThemeMain.setOnClickListener(view -> {
            boolean currentMode = prefs.getBoolean(KEY_DARK_MODE, false);
            boolean newMode = !currentMode;
            prefs.edit().putBoolean(KEY_DARK_MODE, newMode).apply();
            if (newMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        btnAll.setOnClickListener(view -> {
            currentFilter = "All";
            updateCategoryButtons();
            loadIngredients();
        });

        btnFresh.setOnClickListener(view -> {
            currentFilter = "Fresh";
            updateCategoryButtons();
            loadIngredients();
        });

        btnDryGoods.setOnClickListener(view -> {
            currentFilter = "Dry Goods";
            updateCategoryButtons();
            loadIngredients();
        });

        btnDairy.setOnClickListener(view -> {
            currentFilter = "Dairy";
            updateCategoryButtons();
            loadIngredients();
        });

        btnAddIngredient.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddIngredientActivity.class);
            startActivity(intent);
        });

        btnRecipes.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RecipesActivity.class);
            startActivity(intent);
        });

        searchPantry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadIngredients();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        updateCategoryButtons();
        loadIngredients();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        updateThemeButtonText(isDarkMode);

        loadIngredients();
    }

    private void updateThemeButtonText(boolean isDarkMode) {
        if (btnThemeMain != null) {
            if (isDarkMode) {
                btnThemeMain.setText("☀️ Light");
            } else {
                btnThemeMain.setText("🌙 Dark");
            }
        }
    }

    private void updateCategoryButtons() {
        int selectedColor = ContextCompat.getColor(this, R.color.primary_green);
        int normalColor = ContextCompat.getColor(this, R.color.accent_green_light);

        btnAll.setBackgroundTintList(
                ColorStateList.valueOf(currentFilter.equals("All") ? selectedColor : normalColor)
        );

        btnFresh.setBackgroundTintList(
                ColorStateList.valueOf(currentFilter.equals("Fresh") ? selectedColor : normalColor)
        );

        btnDryGoods.setBackgroundTintList(
                ColorStateList.valueOf(currentFilter.equals("Dry Goods") ? selectedColor : normalColor)
        );

        btnDairy.setBackgroundTintList(
                ColorStateList.valueOf(currentFilter.equals("Dairy") ? selectedColor : normalColor)
        );

        int textNormal = ContextCompat.getColor(this, R.color.text_green_dark);

        btnAll.setTextColor(currentFilter.equals("All") ? Color.WHITE : textNormal);
        btnFresh.setTextColor(currentFilter.equals("Fresh") ? Color.WHITE : textNormal);
        btnDryGoods.setTextColor(currentFilter.equals("Dry Goods") ? Color.WHITE : textNormal);
        btnDairy.setTextColor(currentFilter.equals("Dairy") ? Color.WHITE : textNormal);
    }

    private void loadIngredients() {
        ingredientList.clear();

        Cursor cursor = databaseHelper.getAllIngredients();

        int totalItems = cursor.getCount();

        txtItemCount.setText(totalItems + (totalItems == 1 ? " item" : " items"));

        String searchText = searchPantry.getText().toString().trim().toLowerCase();

        int displayedItems = 0;

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String quantity = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QUANTITY));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UNIT));
                String expiryDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPIRY_DATE));

                if (!searchText.isEmpty() && !name.toLowerCase().contains(searchText)) {
                    continue;
                }

                if (!currentFilter.equals("All")) {
                    String lowerName = name.toLowerCase();
                    boolean matches = false;

                    if (currentFilter.equals("Fresh")) {
                        matches = lowerName.contains("fruit")
                                || lowerName.contains("apple")
                                || lowerName.contains("banana")
                                || lowerName.contains("orange")
                                || lowerName.contains("vegetable")
                                || lowerName.contains("tomato")
                                || lowerName.contains("lettuce")
                                || lowerName.contains("carrot");
                    } else if (currentFilter.equals("Dry Goods")) {
                        matches = lowerName.contains("rice")
                                || lowerName.contains("pasta")
                                || lowerName.contains("beans")
                                || lowerName.contains("sugar")
                                || lowerName.contains("flour")
                                || lowerName.contains("cereal");
                    } else if (currentFilter.equals("Dairy")) {
                        matches = lowerName.contains("milk")
                                || lowerName.contains("cheese")
                                || lowerName.contains("yogurt")
                                || lowerName.contains("cream")
                                || lowerName.contains("butter");
                    }

                    if (!matches) {
                        continue;
                    }
                }

                displayedItems++;

                ingredientList.add(new IngredientModel(id, name, quantity, unit, expiryDate));

            } while (cursor.moveToNext());
        }

        cursor.close();

        ingredientAdapter.notifyDataSetChanged();

        if (displayedItems == 0) {
            emptyState.setVisibility(View.VISIBLE);
        } else {
            emptyState.setVisibility(View.GONE);
        }
    }

    private void showEditIngredientDialog(
            int id,
            String currentName,
            String currentQuantity,
            String currentUnit,
            String currentExpiryDate
    ) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        int textPrimary = ContextCompat.getColor(this, R.color.text_primary);
        int textHint = ContextCompat.getColor(this, R.color.text_hint);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Ingredient name");
        nameInput.setHintTextColor(textHint);
        nameInput.setTextColor(textPrimary);
        nameInput.setSingleLine(true);
        nameInput.setText(currentName);
        layout.addView(nameInput);

        EditText quantityInput = new EditText(this);
        quantityInput.setHint("Quantity");
        quantityInput.setHintTextColor(textHint);
        quantityInput.setTextColor(textPrimary);
        quantityInput.setSingleLine(true);
        quantityInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        quantityInput.setText(currentQuantity);
        layout.addView(quantityInput);

        EditText unitInput = new EditText(this);
        unitInput.setHint("Unit");
        unitInput.setHintTextColor(textHint);
        unitInput.setTextColor(textPrimary);
        unitInput.setSingleLine(true);
        unitInput.setText(currentUnit);
        layout.addView(unitInput);

        EditText expiryInput = new EditText(this);
        expiryInput.setHint("DD/MM/YYYY");
        expiryInput.setHintTextColor(textHint);
        expiryInput.setTextColor(textPrimary);
        expiryInput.setSingleLine(true);
        expiryInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        expiryInput.setText(currentExpiryDate);
        layout.addView(expiryInput);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Ingredient")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveButton.setOnClickListener(view -> {
                String name = nameInput.getText().toString().trim();
                String quantity = quantityInput.getText().toString().trim();
                String unit = unitInput.getText().toString().trim();
                String expiry = expiryInput.getText().toString().trim();

                if (name.isEmpty() || quantity.isEmpty() || unit.isEmpty() || expiry.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseHelper db = new DatabaseHelper(MainActivity.this);
                SQLiteDatabase database = db.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_NAME, name);
                values.put(DatabaseHelper.COLUMN_QUANTITY, quantity);
                values.put(DatabaseHelper.COLUMN_UNIT, unit);
                values.put(DatabaseHelper.COLUMN_EXPIRY_DATE, expiry);

                database.update(
                        DatabaseHelper.TABLE_INGREDIENTS,
                        values,
                        DatabaseHelper.COLUMN_ID + "=?",
                        new String[]{String.valueOf(id)}
                );

                database.close();
                dialog.dismiss();

                Toast.makeText(MainActivity.this, "Ingredient updated.", Toast.LENGTH_SHORT).show();
                loadIngredients();
            });
        });

        dialog.show();
    }

    private void confirmDeleteIngredient(int id, String name) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage("Delete " + name + " from your pantry?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseHelper.deleteIngredient(id);
                    Toast.makeText(MainActivity.this, "Ingredient deleted.", Toast.LENGTH_SHORT).show();
                    loadIngredients();
                })
                .show();
    }
}
