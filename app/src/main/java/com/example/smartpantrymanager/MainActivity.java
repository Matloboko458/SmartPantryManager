package com.example.smartpantrymanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private EditText searchPantry;

    private TextView txtItemCount;
    private TextView txtEmptyTitle;
    private TextView txtEmptyMessage;

    private LinearLayout ingredientContainer;
    private LinearLayout emptyState;

    private Button btnAll;
    private Button btnFresh;
    private Button btnDryGoods;
    private Button btnDairy;
    private Button btnRecipes;
    private Button btnAddIngredient;

    private String currentCategory = "All";
    private String currentSearch = "";

    private final int GREEN_DARK = Color.rgb(56, 142, 60);
    private final int GREEN = Color.rgb(76, 175, 80);
    private final int GREEN_LIGHT = Color.rgb(129, 199, 132);
    private final int DARK_GREEN_TEXT = Color.rgb(27, 94, 32);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        searchPantry = findViewById(R.id.searchPantry);

        txtItemCount = findViewById(R.id.txtItemCount);

        txtEmptyTitle = findViewById(R.id.txtEmptyTitle);

        txtEmptyMessage = findViewById(R.id.txtEmptyMessage);

        ingredientContainer =
                findViewById(R.id.ingredientContainer);

        emptyState =
                findViewById(R.id.emptyState);

        btnAll =
                findViewById(R.id.btnAll);

        btnFresh =
                findViewById(R.id.btnFresh);

        btnDryGoods =
                findViewById(R.id.btnDryGoods);

        btnDairy =
                findViewById(R.id.btnDairy);

        btnRecipes =
                findViewById(R.id.btnRecipes);

        btnAddIngredient =
                findViewById(R.id.btnAddIngredient);

        btnAll.setOnClickListener(view -> {

            currentCategory = "All";

            updateCategoryButtons();

            loadIngredients();
        });

        btnFresh.setOnClickListener(view -> {

            currentCategory = "Fresh";

            updateCategoryButtons();

            loadIngredients();
        });

        btnDryGoods.setOnClickListener(view -> {

            currentCategory = "Dry Goods";

            updateCategoryButtons();

            loadIngredients();
        });

        btnDairy.setOnClickListener(view -> {

            currentCategory = "Dairy";

            updateCategoryButtons();

            loadIngredients();
        });

        btnRecipes.setOnClickListener(view -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    RecipesActivity.class
            );

            startActivity(intent);
        });

        btnAddIngredient.setOnClickListener(view -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddIngredientActivity.class
            );

            startActivity(intent);
        });

        searchPantry.addTextChangedListener(
                new TextWatcher() {

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

                        currentSearch =
                                s.toString().trim();

                        loadIngredients();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );

        updateCategoryButtons();

        loadIngredients();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null) {
            loadIngredients();
        }
    }

    private void loadIngredients() {

        if (ingredientContainer == null ||
                databaseHelper == null) {

            return;
        }

        ingredientContainer.removeAllViews();

        Cursor cursor = null;

        int visibleCount = 0;

        try {

            cursor =
                    databaseHelper.getAllIngredients();

            if (cursor != null &&
                    cursor.moveToFirst()) {

                do {

                    long id =
                            getColumnId(cursor);

                    String name =
                            getColumnValue(
                                    cursor,
                                    DatabaseHelper.COLUMN_NAME
                            );

                    String quantity =
                            getColumnValue(
                                    cursor,
                                    DatabaseHelper.COLUMN_QUANTITY
                            );

                    String unit =
                            getColumnValue(
                                    cursor,
                                    DatabaseHelper.COLUMN_UNIT
                            );

                    String expiryDate =
                            getColumnValue(
                                    cursor,
                                    DatabaseHelper.COLUMN_EXPIRY_DATE
                            );

                    if (!currentSearch.isEmpty()) {

                        String searchableText =
                                (
                                        name + " "
                                                + quantity + " "
                                                + unit + " "
                                                + expiryDate
                                ).toLowerCase();

                        if (!searchableText.contains(
                                currentSearch.toLowerCase()
                        )) {

                            continue;
                        }
                    }

                    if (!matchesCategory(
                            name,
                            unit
                    )) {

                        continue;
                    }

                    addIngredientToScreen(
                            id,
                            name,
                            quantity,
                            unit,
                            expiryDate
                    );

                    visibleCount++;

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Error loading ingredients.",
                    Toast.LENGTH_SHORT
            ).show();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        if (visibleCount == 1) {

            txtItemCount.setText(
                    "1 item"
            );

        } else {

            txtItemCount.setText(
                    visibleCount + " items"
            );
        }

        if (visibleCount == 0) {

            ingredientContainer.setVisibility(
                    View.GONE
            );

            emptyState.setVisibility(
                    View.VISIBLE
            );

            if (currentSearch.isEmpty()) {

                txtEmptyTitle.setText(
                        "Your pantry is empty"
                );

                txtEmptyMessage.setText(
                        "Add your first ingredient to start managing your pantry."
                );

            } else {

                txtEmptyTitle.setText(
                        "No ingredients found"
                );

                txtEmptyMessage.setText(
                        "Try another search."
                );
            }

        } else {

            ingredientContainer.setVisibility(
                    View.VISIBLE
            );

            emptyState.setVisibility(
                    View.GONE
            );
        }
    }

    private long getColumnId(Cursor cursor) {

        int columnIndex =
                cursor.getColumnIndex(
                        DatabaseHelper.COLUMN_ID
                );

        if (columnIndex == -1) {
            return -1;
        }

        return cursor.getLong(columnIndex);
    }

    private String getColumnValue(
            Cursor cursor,
            String columnName
    ) {

        int columnIndex =
                cursor.getColumnIndex(columnName);

        if (columnIndex == -1) {
            return "";
        }

        String value =
                cursor.getString(columnIndex);

        if (value == null) {
            return "";
        }

        return value;
    }

    private boolean matchesCategory(
            String name,
            String unit
    ) {

        if (currentCategory.equals("All")) {
            return true;
        }

        String ingredient =
                name.toLowerCase().trim();

        String ingredientUnit =
                unit.toLowerCase().trim();

        if (currentCategory.equals("Fresh")) {

            return ingredient.contains("apple")
                    || ingredient.contains("banana")
                    || ingredient.contains("orange")
                    || ingredient.contains("tomato")
                    || ingredient.contains("lettuce")
                    || ingredient.contains("spinach")
                    || ingredient.contains("carrot")
                    || ingredient.contains("potato")
                    || ingredient.contains("vegetable")
                    || ingredient.contains("chicken")
                    || ingredient.contains("beef")
                    || ingredient.contains("meat")
                    || ingredient.contains("fish")
                    || ingredient.contains("fruit");
        }

        if (currentCategory.equals("Dairy")) {

            return ingredient.contains("milk")
                    || ingredient.contains("cheese")
                    || ingredient.contains("yogurt")
                    || ingredient.contains("yoghurt")
                    || ingredient.contains("butter")
                    || ingredient.contains("cream")
                    || ingredient.contains("custard")
                    || ingredient.contains("cheddar");
        }

        if (currentCategory.equals("Dry Goods")) {

            return ingredient.contains("rice")
                    || ingredient.contains("pasta")
                    || ingredient.contains("flour")
                    || ingredient.contains("sugar")
                    || ingredient.contains("salt")
                    || ingredient.contains("cereal")
                    || ingredient.contains("beans")
                    || ingredient.contains("lentils")
                    || ingredient.contains("bread")
                    || ingredient.contains("coffee")
                    || ingredient.contains("tea")
                    || ingredient.contains("spice")
                    || ingredient.contains("oil")
                    || ingredient.contains("tuna")
                    || ingredient.contains("can")
                    || ingredient.contains("tin")
                    || ingredientUnit.contains("kg")
                    || ingredientUnit.contains("g");
        }

        return true;
    }

    private void addIngredientToScreen(
            long id,
            String name,
            String quantity,
            String unit,
            String expiryDate
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                24,
                20,
                24,
                20
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                20
        );

        card.setLayoutParams(cardParams);

        TextView nameText =
                new TextView(this);

        nameText.setText(name);

        nameText.setTextSize(22);

        nameText.setTextColor(
                Color.rgb(
                        17,
                        17,
                        17
                )
        );

        nameText.setTypeface(
                null,
                Typeface.BOLD
        );

        TextView quantityText =
                new TextView(this);

        quantityText.setText(
                "Quantity: "
                        + quantity
                        + " "
                        + unit
        );

        quantityText.setTextSize(18);

        quantityText.setTextColor(
                Color.rgb(
                        100,
                        100,
                        100
                )
        );

        quantityText.setPadding(
                0,
                6,
                0,
                0
        );

        TextView expiryText =
                new TextView(this);

        expiryText.setText(
                "Expiry Date: "
                        + expiryDate
        );

        expiryText.setTextSize(18);

        expiryText.setTextColor(
                Color.rgb(
                        100,
                        100,
                        100
                )
        );

        expiryText.setPadding(
                0,
                6,
                0,
                12
        );

        card.addView(nameText);

        card.addView(quantityText);

        card.addView(expiryText);

        LinearLayout buttonRow =
                new LinearLayout(this);

        buttonRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        buttonRow.setGravity(
                android.view.Gravity.CENTER_VERTICAL
        );

        Button editButton =
                new Button(this);

        editButton.setText("Edit");

        editButton.setTextColor(Color.WHITE);

        editButton.setTextSize(16);

        editButton.setBackgroundTintList(
                ColorStateList.valueOf(GREEN)
        );

        LinearLayout.LayoutParams editParams =
                new LinearLayout.LayoutParams(
                        0,
                        56,
                        1
                );

        editParams.setMargins(
                0,
                0,
                8,
                0
        );

        editButton.setLayoutParams(editParams);

        Button deleteButton =
                new Button(this);

        deleteButton.setText("Delete");

        deleteButton.setTextColor(Color.WHITE);

        deleteButton.setTextSize(16);

        deleteButton.setBackgroundTintList(
                ColorStateList.valueOf(
                        Color.rgb(198, 40, 40)
                )
        );

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(
                        0,
                        56,
                        1
                );

        deleteParams.setMargins(
                8,
                0,
                0,
                0
        );

        deleteButton.setLayoutParams(
                deleteParams
        );

        editButton.setOnClickListener(
                view -> showEditIngredientDialog(
                        id,
                        name,
                        quantity,
                        unit,
                        expiryDate
                )
        );

        deleteButton.setOnClickListener(
                view -> showDeleteConfirmation(
                        id,
                        name
                )
        );

        buttonRow.addView(editButton);

        buttonRow.addView(deleteButton);

        card.addView(buttonRow);

        ingredientContainer.addView(card);
    }

    private void showEditIngredientDialog(
            long id,
            String currentName,
            String currentQuantity,
            String currentUnit,
            String currentExpiryDate
    ) {

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                40,
                10,
                40,
                10
        );

        EditText nameInput =
                new EditText(this);

        nameInput.setHint("Ingredient Name");

        nameInput.setSingleLine(true);

        nameInput.setText(currentName);

        EditText quantityInput =
                new EditText(this);

        quantityInput.setHint("Quantity");

        quantityInput.setSingleLine(true);

        quantityInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
                        | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        quantityInput.setText(currentQuantity);

        EditText unitInput =
                new EditText(this);

        unitInput.setHint("Unit");

        unitInput.setSingleLine(true);

        unitInput.setText(currentUnit);

        EditText expiryInput =
                new EditText(this);

        expiryInput.setHint("DD/MM/YYYY");

        expiryInput.setSingleLine(true);

        expiryInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );

        expiryInput.setFilters(new android.text.InputFilter[]{
                new android.text.InputFilter.LengthFilter(10)
        });

        expiryInput.setText(currentExpiryDate);

        layout.addView(nameInput);

        layout.addView(quantityInput);

        layout.addView(unitInput);

        layout.addView(expiryInput);

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Edit Ingredient")
                        .setView(layout)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Save",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                dialogInterface -> {

                    Button saveButton =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );

                    saveButton.setTextColor(
                            GREEN_DARK
                    );

                    saveButton.setOnClickListener(
                            view -> {

                                String newName =
                                        nameInput.getText()
                                                .toString()
                                                .trim();

                                String newQuantity =
                                        quantityInput.getText()
                                                .toString()
                                                .trim();

                                String newUnit =
                                        unitInput.getText()
                                                .toString()
                                                .trim();

                                String newExpiry =
                                        expiryInput.getText()
                                                .toString()
                                                .trim();

                                if (newName.isEmpty()
                                        || newQuantity.isEmpty()
                                        || newUnit.isEmpty()
                                        || newExpiry.isEmpty()) {

                                    Toast.makeText(
                                            MainActivity.this,
                                            "Please complete all fields.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                if (!newExpiry.matches(
                                        "\\d{2}/\\d{2}/\\d{4}"
                                )) {

                                    Toast.makeText(
                                            MainActivity.this,
                                            "Enter the expiry date as DD/MM/YYYY.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                int result =
                                        databaseHelper.updateIngredient(
                                                id,
                                                newName,
                                                newQuantity,
                                                newUnit,
                                                newExpiry
                                        );

                                if (result > 0) {

                                    Toast.makeText(
                                            MainActivity.this,
                                            "Ingredient updated.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    dialog.dismiss();

                                    loadIngredients();

                                } else {

                                    Toast.makeText(
                                            MainActivity.this,
                                            "Failed to update ingredient.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                    );
                }
        );

        dialog.show();
    }

    private void showDeleteConfirmation(
            long id,
            String name
    ) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Are you sure you want to delete "
                                + name
                                + "?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            int result =
                                    databaseHelper.deleteIngredient(
                                            id
                                    );

                            if (result > 0) {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Ingredient deleted.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadIngredients();

                            } else {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Failed to delete ingredient.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .show();
    }

    private void updateCategoryButtons() {

        if (btnAll == null ||
                btnFresh == null ||
                btnDryGoods == null ||
                btnDairy == null) {

            return;
        }

        if (currentCategory.equals("All")) {

            btnAll.setBackgroundTintList(
                    ColorStateList.valueOf(
                            GREEN_DARK
                    )
            );

            btnAll.setTextColor(
                    Color.WHITE
            );

        } else {

            btnAll.setBackgroundTintList(
                    ColorStateList.valueOf(
                            GREEN_LIGHT
                    )
            );

            btnAll.setTextColor(
                    DARK_GREEN_TEXT
            );
        }

        if (currentCategory.equals("Fresh")) {

            btnFresh.setBackgroundTintList(
                    ColorStateList.valueOf(
                            GREEN_DARK
                    )
            );

            btnFresh.setTextColor(
                    Color.WHITE
            );

        } else {

            btnFresh.setBackgroundTintList(
                    ColorStateList.valueOf(
                            GREEN_LIGHT
                    )
            );

            btnFresh.setTextColor(
                    DARK_GREEN_TEXT
            );
        }

        if (currentCategory.equals("Dry Goods")) {

            btnDryGoods.setBackgroundTintList(
                    ColorStateList.valueOf(
                            GREEN_DARK
                    )
            );

            btnDryGoods.setTextColor(
                    Color.WHITE
            );

        } else {

            btnDryGoods.setBackgroundTintList(
                    ColorStateList.valueOf(
                            GREEN_LIGHT
                    )
            );

            btnDryGoods.setTextColor(
                    DARK_GREEN_TEXT
            );
        }

        if (currentCategory.equals("Dairy")) {

            btnDairy.setBackgroundTintList(
                    ColorStateList.valueOf(
                            GREEN_DARK
                    )
            );

            btnDairy.setTextColor(
                    Color.WHITE
            );

        } else {

            btnDairy.setBackgroundTintList(
                    ColorStateList.valueOf(
                            GREEN_LIGHT
                    )
            );

            btnDairy.setTextColor(
                    DARK_GREEN_TEXT
            );
        }

        if (btnRecipes != null) {

            btnRecipes.setBackgroundTintList(
                    ColorStateList.valueOf(
                            GREEN_DARK
                    )
            );

            btnRecipes.setTextColor(
                    Color.WHITE
            );
        }

        if (btnAddIngredient != null) {

            btnAddIngredient.setBackgroundTintList(
                    ColorStateList.valueOf(
                            GREEN
                    )
            );

            btnAddIngredient.setTextColor(
                    Color.WHITE
            );
        }
    }
}