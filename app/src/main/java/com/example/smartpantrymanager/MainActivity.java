package com.example.smartpantrymanager;

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

    // Green theme colours
    private final int GREEN_DARK = Color.rgb(56, 142, 60);
    private final int GREEN = Color.rgb(76, 175, 80);
    private final int GREEN_LIGHT = Color.rgb(129, 199, 132);
    private final int DARK_GREEN_TEXT = Color.rgb(27, 94, 32);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // =========================================================
        // DATABASE
        // =========================================================

        databaseHelper = new DatabaseHelper(this);

        // =========================================================
        // CONNECT XML VIEWS
        // =========================================================

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

        // =========================================================
        // CATEGORY BUTTONS
        // =========================================================

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

        // =========================================================
        // RECIPES BUTTON
        // =========================================================

        btnRecipes.setOnClickListener(view -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    RecipesActivity.class
            );

            startActivity(intent);
        });

        // =========================================================
        // ADD INGREDIENT BUTTON
        // =========================================================

        btnAddIngredient.setOnClickListener(view -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddIngredientActivity.class
            );

            startActivity(intent);
        });

        // =========================================================
        // SEARCH
        // =========================================================

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

        // =========================================================
        // INITIAL BUTTON STYLE
        // =========================================================

        updateCategoryButtons();

        // =========================================================
        // LOAD PANTRY
        // =========================================================

        loadIngredients();
    }

    // =============================================================
    // RELOAD WHEN RETURNING TO MAIN SCREEN
    // =============================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (databaseHelper != null) {

            loadIngredients();
        }
    }

    // =============================================================
    // LOAD INGREDIENTS
    // =============================================================

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

                    // Search filter
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

                    // Category filter
                    if (!matchesCategory(
                            name,
                            unit
                    )) {

                        continue;
                    }

                    addIngredientToScreen(
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

        } finally {

            if (cursor != null) {

                cursor.close();
            }
        }

        // =========================================================
        // UPDATE ITEM COUNT
        // =========================================================

        if (visibleCount == 1) {

            txtItemCount.setText(
                    "1 item"
            );

        } else {

            txtItemCount.setText(
                    visibleCount + " items"
            );
        }

        // =========================================================
        // EMPTY STATE
        // =========================================================

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

    // =============================================================
    // GET DATABASE COLUMN SAFELY
    // =============================================================

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

    // =============================================================
    // CATEGORY FILTER
    // =============================================================

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

        // =========================================================
        // FRESH
        // =========================================================

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

        // =========================================================
        // DAIRY
        // =========================================================

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

        // =========================================================
        // DRY GOODS
        // =========================================================

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

    // =============================================================
    // DISPLAY INGREDIENT
    // =============================================================

    private void addIngredientToScreen(
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

        card.setLayoutParams(
                cardParams
        );

        // =========================================================
        // INGREDIENT NAME
        // =========================================================

        TextView nameText =
                new TextView(this);

        nameText.setText(
                name
        );

        nameText.setTextSize(
                22
        );

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

        // =========================================================
        // QUANTITY
        // =========================================================

        TextView quantityText =
                new TextView(this);

        quantityText.setText(
                "Quantity: "
                        + quantity
                        + " "
                        + unit
        );

        quantityText.setTextSize(
                18
        );

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

        // =========================================================
        // EXPIRY DATE
        // =========================================================

        TextView expiryText =
                new TextView(this);

        expiryText.setText(
                "Expiry Date: "
                        + expiryDate
        );

        expiryText.setTextSize(
                18
        );

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
                0
        );

        // =========================================================
        // ADD TO CARD
        // =========================================================

        card.addView(
                nameText
        );

        card.addView(
                quantityText
        );

        card.addView(
                expiryText
        );

        ingredientContainer.addView(
                card
        );
    }

    // =============================================================
    // CATEGORY BUTTON COLOURS
    // =============================================================

    private void updateCategoryButtons() {

        if (btnAll == null ||
                btnFresh == null ||
                btnDryGoods == null ||
                btnDairy == null) {

            return;
        }

        // ALL

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

        // FRESH

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

        // DRY GOODS

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

        // DAIRY

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

        // RECIPES

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

        // ADD INGREDIENT

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