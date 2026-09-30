package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText searchPantry;
    private TextView txtItemCount;
    private TextView btnTheme;

    private Button btnAll;
    private Button btnFresh;
    private Button btnDryGoods;
    private Button btnDairy;
    private Button btnRecipes;
    private Button btnAddIngredient;

    private LinearLayout ingredientContainer;
    private LinearLayout emptyState;

    private DatabaseHelper databaseHelper;

    private String currentCategory = "All";
    private String currentSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // ---------------------------------------------------------
        // CONNECT VIEWS
        // ---------------------------------------------------------

        searchPantry = findViewById(R.id.searchPantry);
        txtItemCount = findViewById(R.id.txtItemCount);
        btnTheme = findViewById(R.id.btnTheme);

        btnAll = findViewById(R.id.btnAll);
        btnFresh = findViewById(R.id.btnFresh);
        btnDryGoods = findViewById(R.id.btnDryGoods);
        btnDairy = findViewById(R.id.btnDairy);
        btnRecipes = findViewById(R.id.btnRecipes);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);

        ingredientContainer = findViewById(R.id.ingredientContainer);
        emptyState = findViewById(R.id.emptyState);

        // ---------------------------------------------------------
        // DATABASE
        // ---------------------------------------------------------

        databaseHelper = new DatabaseHelper(this);

        // ---------------------------------------------------------
        // LOAD INGREDIENTS
        // ---------------------------------------------------------

        loadIngredients();

        // ---------------------------------------------------------
        // SEARCH
        // ---------------------------------------------------------

        searchPantry.addTextChangedListener(new TextWatcher() {

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
                currentSearch = s.toString().trim();
                loadIngredients();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // ---------------------------------------------------------
        // ALL BUTTON
        // ---------------------------------------------------------

        btnAll.setOnClickListener(view -> {

            currentCategory = "All";

            updateCategoryButtons();

            loadIngredients();
        });

        // ---------------------------------------------------------
        // FRESH BUTTON
        // ---------------------------------------------------------

        btnFresh.setOnClickListener(view -> {

            currentCategory = "Fresh";

            updateCategoryButtons();

            loadIngredients();
        });

        // ---------------------------------------------------------
        // DRY GOODS BUTTON
        // ---------------------------------------------------------

        btnDryGoods.setOnClickListener(view -> {

            currentCategory = "Dry Goods";

            updateCategoryButtons();

            loadIngredients();
        });

        // ---------------------------------------------------------
        // DAIRY BUTTON
        // ---------------------------------------------------------

        btnDairy.setOnClickListener(view -> {

            currentCategory = "Dairy";

            updateCategoryButtons();

            loadIngredients();
        });

        // ---------------------------------------------------------
        // RECIPES BUTTON
        // ---------------------------------------------------------

        btnRecipes.setOnClickListener(view -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    RecipesActivity.class
            );

            startActivity(intent);
        });

        // ---------------------------------------------------------
        // ADD INGREDIENT BUTTON
        // ---------------------------------------------------------

        btnAddIngredient.setOnClickListener(view -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddIngredientActivity.class
            );

            startActivity(intent);
        });

        // ---------------------------------------------------------
        // THEME BUTTON
        // ---------------------------------------------------------

        btnTheme.setOnClickListener(view -> {

            if (btnTheme.getText().toString().equals("☾")) {

                btnTheme.setText("☀");

                getWindow().getDecorView().setBackgroundColor(
                        Color.rgb(30, 30, 30)
                );

            } else {

                btnTheme.setText("☾");

                getWindow().getDecorView().setBackgroundColor(
                        Color.rgb(248, 250, 250)
                );
            }
        });

        // ---------------------------------------------------------
        // INITIAL BUTTON STYLE
        // ---------------------------------------------------------

        updateCategoryButtons();
    }

    // =============================================================
    // RELOAD WHEN RETURNING FROM ADD INGREDIENT SCREEN
    // =============================================================

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadIngredients();
        }
    }

    // =============================================================
    // LOAD INGREDIENTS FROM DATABASE
    // =============================================================

    private void loadIngredients() {

        if (databaseHelper == null) {
            return;
        }

        ingredientContainer.removeAllViews();

        int numberOfItems = 0;

        Cursor cursor = null;

        try {

            cursor = databaseHelper.getAllIngredients();

            if (cursor != null) {

                while (cursor.moveToNext()) {

                    String name = "";
                    String quantity = "";
                    String unit = "";
                    String expiryDate = "";

                    // -------------------------------------------------
                    // READ DATABASE COLUMNS SAFELY
                    // -------------------------------------------------

                    int nameIndex = cursor.getColumnIndex("name");

                    if (nameIndex >= 0) {
                        name = cursor.getString(nameIndex);
                    }

                    int quantityIndex = cursor.getColumnIndex("quantity");

                    if (quantityIndex >= 0) {
                        quantity = cursor.getString(quantityIndex);
                    }

                    int unitIndex = cursor.getColumnIndex("unit");

                    if (unitIndex >= 0) {
                        unit = cursor.getString(unitIndex);
                    }

                    int expiryIndex = cursor.getColumnIndex("expiry_date");

                    if (expiryIndex >= 0) {
                        expiryDate = cursor.getString(expiryIndex);
                    }

                    // -------------------------------------------------
                    // SEARCH FILTER
                    // -------------------------------------------------

                    if (!currentSearch.isEmpty()) {

                        String searchableText =
                                name + " "
                                        + quantity + " "
                                        + unit + " "
                                        + expiryDate;

                        if (!searchableText
                                .toLowerCase()
                                .contains(currentSearch.toLowerCase())) {

                            continue;
                        }
                    }

                    // -------------------------------------------------
                    // CATEGORY FILTER
                    // -------------------------------------------------

                    if (!matchesCategory(name, unit)) {
                        continue;
                    }

                    // -------------------------------------------------
                    // DISPLAY INGREDIENT
                    // -------------------------------------------------

                    addIngredientToScreen(
                            name,
                            quantity,
                            unit,
                            expiryDate
                    );

                    numberOfItems++;
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        // ---------------------------------------------------------
        // UPDATE ITEM COUNT
        // ---------------------------------------------------------

        if (numberOfItems == 1) {

            txtItemCount.setText("1 item");

        } else {

            txtItemCount.setText(numberOfItems + " items");
        }

        // ---------------------------------------------------------
        // EMPTY STATE
        // ---------------------------------------------------------

        if (numberOfItems == 0) {

            emptyState.setVisibility(View.VISIBLE);

        } else {

            emptyState.setVisibility(View.GONE);
        }
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

        String ingredientName =
                name == null
                        ? ""
                        : name.toLowerCase();

        String ingredientUnit =
                unit == null
                        ? ""
                        : unit.toLowerCase();

        // ---------------------------------------------------------
        // FRESH
        // ---------------------------------------------------------

        if (currentCategory.equals("Fresh")) {

            return ingredientName.contains("fruit")
                    || ingredientName.contains("apple")
                    || ingredientName.contains("banana")
                    || ingredientName.contains("orange")
                    || ingredientName.contains("tomato")
                    || ingredientName.contains("lettuce")
                    || ingredientName.contains("spinach")
                    || ingredientName.contains("carrot")
                    || ingredientName.contains("potato")
                    || ingredientName.contains("vegetable")
                    || ingredientName.contains("chicken")
                    || ingredientName.contains("meat")
                    || ingredientName.contains("fish");
        }

        // ---------------------------------------------------------
        // DAIRY
        // ---------------------------------------------------------

        if (currentCategory.equals("Dairy")) {

            return ingredientName.contains("milk")
                    || ingredientName.contains("cheese")
                    || ingredientName.contains("yogurt")
                    || ingredientName.contains("yoghurt")
                    || ingredientName.contains("butter")
                    || ingredientName.contains("cream")
                    || ingredientName.contains("cheddar")
                    || ingredientName.contains("custard");
        }

        // ---------------------------------------------------------
        // DRY GOODS
        // ---------------------------------------------------------

        if (currentCategory.equals("Dry Goods")) {

            return ingredientName.contains("rice")
                    || ingredientName.contains("pasta")
                    || ingredientName.contains("flour")
                    || ingredientName.contains("sugar")
                    || ingredientName.contains("salt")
                    || ingredientName.contains("cereal")
                    || ingredientName.contains("beans")
                    || ingredientName.contains("lentils")
                    || ingredientName.contains("bread")
                    || ingredientName.contains("water")
                    || ingredientName.contains("coffee")
                    || ingredientName.contains("tea")
                    || ingredientName.contains("spice")
                    || ingredientName.contains("oil")
                    || ingredientName.contains("can")
                    || ingredientName.contains("tin")
                    || ingredientUnit.contains("kg")
                    || ingredientUnit.contains("g");
        }

        return true;
    }

    // =============================================================
    // ADD INGREDIENT TO SCREEN
    // =============================================================

    private void addIngredientToScreen(
            String name,
            String quantity,
            String unit,
            String expiryDate
    ) {

        LinearLayout itemLayout =
                new LinearLayout(this);

        itemLayout.setOrientation(
                LinearLayout.VERTICAL
        );

        itemLayout.setPadding(
                16,
                12,
                16,
                18
        );

        LinearLayout.LayoutParams itemParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        itemParams.setMargins(
                0,
                4,
                0,
                8
        );

        itemLayout.setLayoutParams(itemParams);

        // ---------------------------------------------------------
        // INGREDIENT NAME
        // ---------------------------------------------------------

        TextView nameText =
                new TextView(this);

        nameText.setText(name);

        nameText.setTextColor(
                Color.BLACK
        );

        nameText.setTextSize(22);

        nameText.setGravity(
                Gravity.START
        );

        nameText.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        itemLayout.addView(nameText);

        // ---------------------------------------------------------
        // QUANTITY
        // ---------------------------------------------------------

        TextView quantityText =
                new TextView(this);

        String quantityDisplay =
                "Quantity: "
                        + quantity
                        + (unit.isEmpty()
                        ? ""
                        : " " + unit);

        quantityText.setText(
                quantityDisplay
        );

        quantityText.setTextColor(
                Color.rgb(170, 170, 170)
        );

        quantityText.setTextSize(18);

        quantityText.setPadding(
                0,
                4,
                0,
                0
        );

        itemLayout.addView(
                quantityText
        );

        // ---------------------------------------------------------
        // EXPIRY DATE
        // ---------------------------------------------------------

        TextView expiryText =
                new TextView(this);

        expiryText.setText(
                "Expiry Date: "
                        + expiryDate
        );

        expiryText.setTextColor(
                Color.rgb(170, 170, 170)
        );

        expiryText.setTextSize(18);

        expiryText.setPadding(
                0,
                4,
                0,
                0
        );

        itemLayout.addView(
                expiryText
        );

        // ---------------------------------------------------------
        // ADD TO LIST
        // ---------------------------------------------------------

        ingredientContainer.addView(
                itemLayout
        );
    }

    // =============================================================
    // CATEGORY BUTTON APPEARANCE
    // =============================================================

    private void updateCategoryButtons() {

        int selectedColor =
                Color.rgb(199, 165, 240);

        int normalColor =
                Color.rgb(225, 210, 245);

        int textColor =
                Color.rgb(56, 33, 109);

        // ---------------------------------------------------------
        // ALL
        // ---------------------------------------------------------

        if (currentCategory.equals("All")) {

            btnAll.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            selectedColor
                    )
            );

        } else {

            btnAll.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            normalColor
                    )
            );
        }

        // ---------------------------------------------------------
        // FRESH
        // ---------------------------------------------------------

        if (currentCategory.equals("Fresh")) {

            btnFresh.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            selectedColor
                    )
            );

        } else {

            btnFresh.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            normalColor
                    )
            );
        }

        // ---------------------------------------------------------
        // DRY GOODS
        // ---------------------------------------------------------

        if (currentCategory.equals("Dry Goods")) {

            btnDryGoods.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            selectedColor
                    )
            );

        } else {

            btnDryGoods.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            normalColor
                    )
            );
        }

        // ---------------------------------------------------------
        // DAIRY
        // ---------------------------------------------------------

        if (currentCategory.equals("Dairy")) {

            btnDairy.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            selectedColor
                    )
            );

        } else {

            btnDairy.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            normalColor
                    )
            );
        }

        // ---------------------------------------------------------
        // TEXT COLOUR
        // ---------------------------------------------------------

        btnAll.setTextColor(textColor);
        btnFresh.setTextColor(textColor);
        btnDryGoods.setTextColor(textColor);
        btnDairy.setTextColor(textColor);
    }
}