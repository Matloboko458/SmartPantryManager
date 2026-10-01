package com.example.smartpantrymanager;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class RecipesActivity extends AppCompatActivity {

    private LinearLayout recipeContainer;
    private DatabaseHelper databaseHelper;

    private final List<PantryItem> pantryItems =
            new ArrayList<>();

    private final List<Recipe> recipes =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipes);

        databaseHelper =
                new DatabaseHelper(this);

        recipeContainer =
                findViewById(R.id.recipeContainer);

        Button btnBack =
                findViewById(R.id.btnBack);

        btnBack.setOnClickListener(
                view -> finish()
        );

        createRecipes();

        loadRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null &&
                recipeContainer != null) {

            loadRecipes();
        }
    }

    private void createRecipes() {

        recipes.clear();

        recipes.add(new Recipe(
                "Egg & Cheese Toast",
                "10 minutes",
                "Easy",
                "A quick breakfast made with eggs, bread and cheese.",
                new RequiredIngredient[]{
                        new RequiredIngredient("egg", 2, "piece"),
                        new RequiredIngredient("bread", 2, "slice"),
                        new RequiredIngredient("cheese", 40, "g")
                },
                "1. Beat the eggs.\n" +
                        "2. Cook the eggs in a pan.\n" +
                        "3. Toast the bread.\n" +
                        "4. Add cheese and cooked eggs.\n" +
                        "5. Serve."
        ));

        recipes.add(new Recipe(
                "Creamy Pasta",
                "20 minutes",
                "Easy",
                "Creamy pasta using milk and cheese.",
                new RequiredIngredient[]{
                        new RequiredIngredient("pasta", 250, "g"),
                        new RequiredIngredient("milk", 200, "ml"),
                        new RequiredIngredient("cheese", 50, "g")
                },
                "1. Cook the pasta.\n" +
                        "2. Drain the pasta.\n" +
                        "3. Heat the milk.\n" +
                        "4. Add cheese.\n" +
                        "5. Add pasta and mix."
        ));

        recipes.add(new Recipe(
                "Chicken Rice Bowl",
                "30 minutes",
                "Medium",
                "A simple rice bowl with chicken and vegetables.",
                new RequiredIngredient[]{
                        new RequiredIngredient("chicken", 150, "g"),
                        new RequiredIngredient("rice", 100, "g"),
                        new RequiredIngredient("vegetables", 100, "g")
                },
                "1. Cook the rice.\n" +
                        "2. Cut and cook the chicken.\n" +
                        "3. Add vegetables.\n" +
                        "4. Season and serve with rice."
        ));

        recipes.add(new Recipe(
                "Pancakes",
                "15 minutes",
                "Easy",
                "Simple homemade pancakes.",
                new RequiredIngredient[]{
                        new RequiredIngredient("flour", 100, "g"),
                        new RequiredIngredient("egg", 1, "piece"),
                        new RequiredIngredient("milk", 120, "ml")
                },
                "1. Mix flour and egg.\n" +
                        "2. Add milk.\n" +
                        "3. Mix until smooth.\n" +
                        "4. Cook in a heated pan.\n" +
                        "5. Serve."
        ));

        recipes.add(new Recipe(
                "Vegetable Omelette",
                "15 minutes",
                "Easy",
                "An omelette made with eggs, vegetables and cheese.",
                new RequiredIngredient[]{
                        new RequiredIngredient("egg", 3, "piece"),
                        new RequiredIngredient("vegetables", 100, "g"),
                        new RequiredIngredient("cheese", 30, "g")
                },
                "1. Beat the eggs.\n" +
                        "2. Cook the vegetables.\n" +
                        "3. Add the eggs.\n" +
                        "4. Add cheese.\n" +
                        "5. Fold and serve."
        ));

        recipes.add(new Recipe(
                "Tomato Toast",
                "10 minutes",
                "Easy",
                "Simple toast with fresh tomato.",
                new RequiredIngredient[]{
                        new RequiredIngredient("bread", 2, "slice"),
                        new RequiredIngredient("tomato", 1, "piece"),
                        new RequiredIngredient("salt", 1, "pinch")
                },
                "1. Toast the bread.\n" +
                        "2. Slice the tomato.\n" +
                        "3. Place tomato on the toast.\n" +
                        "4. Add salt.\n" +
                        "5. Serve."
        ));

        recipes.add(new Recipe(
                "Egg Toast",
                "10 minutes",
                "Easy",
                "Toast topped with cooked eggs.",
                new RequiredIngredient[]{
                        new RequiredIngredient("bread", 2, "slice"),
                        new RequiredIngredient("egg", 2, "piece"),
                        new RequiredIngredient("salt", 1, "pinch")
                },
                "1. Toast the bread.\n" +
                        "2. Cook the eggs.\n" +
                        "3. Place the eggs on the toast.\n" +
                        "4. Add salt."
        ));

        recipes.add(new Recipe(
                "Cheese Omelette",
                "12 minutes",
                "Easy",
                "A simple cheese omelette.",
                new RequiredIngredient[]{
                        new RequiredIngredient("egg", 2, "piece"),
                        new RequiredIngredient("cheese", 50, "g"),
                        new RequiredIngredient("salt", 1, "pinch")
                },
                "1. Beat the eggs.\n" +
                        "2. Heat a pan.\n" +
                        "3. Add eggs.\n" +
                        "4. Add cheese.\n" +
                        "5. Fold and serve."
        ));

        recipes.add(new Recipe(
                "Cheesy Pasta",
                "20 minutes",
                "Easy",
                "Pasta with tomato sauce and cheese.",
                new RequiredIngredient[]{
                        new RequiredIngredient("pasta", 100, "g"),
                        new RequiredIngredient("tomato sauce", 100, "ml"),
                        new RequiredIngredient("cheese", 40, "g")
                },
                "1. Cook the pasta.\n" +
                        "2. Heat tomato sauce.\n" +
                        "3. Add the pasta.\n" +
                        "4. Add cheese.\n" +
                        "5. Serve."
        ));

        recipes.add(new Recipe(
                "Garlic Pasta",
                "20 minutes",
                "Easy",
                "Simple pasta with garlic and oil.",
                new RequiredIngredient[]{
                        new RequiredIngredient("pasta", 100, "g"),
                        new RequiredIngredient("garlic", 2, "clove"),
                        new RequiredIngredient("oil", 15, "ml"),
                        new RequiredIngredient("salt", 1, "pinch")
                },
                "1. Cook the pasta.\n" +
                        "2. Fry the garlic in oil.\n" +
                        "3. Add the pasta.\n" +
                        "4. Add salt.\n" +
                        "5. Mix and serve."
        ));

        recipes.add(new Recipe(
                "Chicken Sandwich",
                "15 minutes",
                "Easy",
                "Chicken sandwich with tomato and lettuce.",
                new RequiredIngredient[]{
                        new RequiredIngredient("chicken", 100, "g"),
                        new RequiredIngredient("bread", 2, "slice"),
                        new RequiredIngredient("tomato", 1, "piece"),
                        new RequiredIngredient("lettuce", 2, "leaf")
                },
                "1. Cook the chicken.\n" +
                        "2. Toast the bread.\n" +
                        "3. Add chicken, tomato and lettuce.\n" +
                        "4. Close the sandwich and serve."
        ));

        recipes.add(new Recipe(
                "Tuna Sandwich",
                "10 minutes",
                "Easy",
                "Quick tuna sandwich.",
                new RequiredIngredient[]{
                        new RequiredIngredient("tuna", 1, "can"),
                        new RequiredIngredient("bread", 2, "slice"),
                        new RequiredIngredient("mayonnaise", 20, "ml"),
                        new RequiredIngredient("lettuce", 2, "leaf")
                },
                "1. Drain the tuna.\n" +
                        "2. Mix tuna with mayonnaise.\n" +
                        "3. Add lettuce.\n" +
                        "4. Place between bread slices."
        ));

        recipes.add(new Recipe(
                "French Toast",
                "15 minutes",
                "Easy",
                "Golden French toast using bread, egg and milk.",
                new RequiredIngredient[]{
                        new RequiredIngredient("bread", 2, "slice"),
                        new RequiredIngredient("egg", 1, "piece"),
                        new RequiredIngredient("milk", 60, "ml"),
                        new RequiredIngredient("oil", 10, "ml")
                },
                "1. Beat egg and milk together.\n" +
                        "2. Dip bread into the mixture.\n" +
                        "3. Heat oil in a pan.\n" +
                        "4. Cook both sides until golden."
        ));

        recipes.add(new Recipe(
                "Vegetable Rice",
                "25 minutes",
                "Medium",
                "Rice mixed with vegetables.",
                new RequiredIngredient[]{
                        new RequiredIngredient("rice", 100, "g"),
                        new RequiredIngredient("vegetables", 100, "g"),
                        new RequiredIngredient("onion", 1, "piece"),
                        new RequiredIngredient("oil", 15, "ml")
                },
                "1. Cook the rice.\n" +
                        "2. Fry onion in oil.\n" +
                        "3. Add vegetables.\n" +
                        "4. Add rice.\n" +
                        "5. Mix and serve."
        ));

        recipes.add(new Recipe(
                "Tomato Pasta",
                "20 minutes",
                "Easy",
                "Pasta with tomato sauce, garlic and cheese.",
                new RequiredIngredient[]{
                        new RequiredIngredient("pasta", 100, "g"),
                        new RequiredIngredient("tomato sauce", 100, "ml"),
                        new RequiredIngredient("garlic", 1, "clove"),
                        new RequiredIngredient("cheese", 30, "g")
                },
                "1. Cook the pasta.\n" +
                        "2. Fry the garlic.\n" +
                        "3. Add tomato sauce.\n" +
                        "4. Add pasta.\n" +
                        "5. Add cheese and serve."
        ));

        recipes.add(new Recipe(
                "Chicken Pasta",
                "30 minutes",
                "Medium",
                "Creamy chicken pasta.",
                new RequiredIngredient[]{
                        new RequiredIngredient("pasta", 100, "g"),
                        new RequiredIngredient("chicken", 120, "g"),
                        new RequiredIngredient("cream", 80, "ml"),
                        new RequiredIngredient("cheese", 30, "g")
                },
                "1. Cook the pasta.\n" +
                        "2. Cook the chicken.\n" +
                        "3. Add cream.\n" +
                        "4. Add cheese.\n" +
                        "5. Mix with pasta."
        ));

        recipes.add(new Recipe(
                "Cheese Quesadilla",
                "15 minutes",
                "Easy",
                "Crispy tortilla filled with cheese and tomato.",
                new RequiredIngredient[]{
                        new RequiredIngredient("tortilla", 2, "piece"),
                        new RequiredIngredient("cheese", 60, "g"),
                        new RequiredIngredient("tomato", 1, "piece")
                },
                "1. Place cheese and tomato on tortilla.\n" +
                        "2. Fold the tortilla.\n" +
                        "3. Cook both sides until crispy.\n" +
                        "4. Cut and serve."
        ));

        recipes.add(new Recipe(
                "Peanut Butter Toast",
                "5 minutes",
                "Easy",
                "Quick toast with peanut butter.",
                new RequiredIngredient[]{
                        new RequiredIngredient("bread", 2, "slice"),
                        new RequiredIngredient("peanut butter", 30, "g")
                },
                "1. Toast the bread.\n" +
                        "2. Spread peanut butter.\n" +
                        "3. Serve."
        ));

        recipes.add(new Recipe(
                "Banana Toast",
                "5 minutes",
                "Easy",
                "Toast with peanut butter and banana.",
                new RequiredIngredient[]{
                        new RequiredIngredient("bread", 2, "slice"),
                        new RequiredIngredient("peanut butter", 20, "g"),
                        new RequiredIngredient("banana", 1, "piece")
                },
                "1. Toast the bread.\n" +
                        "2. Spread peanut butter.\n" +
                        "3. Slice the banana.\n" +
                        "4. Place banana on toast."
        ));

        recipes.add(new Recipe(
                "Egg Fried Rice",
                "25 minutes",
                "Medium",
                "Fried rice with egg and onion.",
                new RequiredIngredient[]{
                        new RequiredIngredient("rice", 150, "g"),
                        new RequiredIngredient("egg", 2, "piece"),
                        new RequiredIngredient("onion", 1, "piece"),
                        new RequiredIngredient("oil", 15, "ml")
                },
                "1. Cook the rice.\n" +
                        "2. Fry onion in oil.\n" +
                        "3. Add eggs.\n" +
                        "4. Add rice.\n" +
                        "5. Stir and serve."
        ));

        recipes.add(new Recipe(
                "Tomato Salad",
                "10 minutes",
                "Easy",
                "Fresh tomato and cucumber salad.",
                new RequiredIngredient[]{
                        new RequiredIngredient("tomato", 2, "piece"),
                        new RequiredIngredient("cucumber", 1, "piece"),
                        new RequiredIngredient("onion", 1, "piece"),
                        new RequiredIngredient("salt", 1, "pinch")
                },
                "1. Wash the vegetables.\n" +
                        "2. Chop tomato, cucumber and onion.\n" +
                        "3. Mix together.\n" +
                        "4. Add salt.\n" +
                        "5. Serve."
        ));
    }

    private void loadRecipes() {

        loadPantryIngredients();

        recipeContainer.removeAllViews();

        int matchingRecipeCount = 0;

        for (Recipe recipe : recipes) {

            if (canMakeRecipe(recipe)) {

                addRecipe(recipe);

                matchingRecipeCount++;
            }
        }

        if (matchingRecipeCount == 0) {

            TextView emptyText =
                    new TextView(this);

            emptyText.setText(
                    "No recipes match your pantry yet.\n\n" +
                            "Add more ingredients to unlock recipes."
            );

            emptyText.setTextSize(19);
            emptyText.setTextColor(
                    Color.rgb(100, 100, 100)
            );

            emptyText.setGravity(
                    android.view.Gravity.CENTER
            );

            emptyText.setPadding(
                    30,
                    80,
                    30,
                    80
            );

            recipeContainer.addView(
                    emptyText
            );
        }
    }

    private void loadPantryIngredients() {

        pantryItems.clear();

        Cursor cursor = null;

        try {

            cursor =
                    databaseHelper.getAllIngredients();

            if (cursor != null &&
                    cursor.moveToFirst()) {

                do {

                    int idIndex =
                            cursor.getColumnIndex(
                                    DatabaseHelper.COLUMN_ID
                            );

                    int nameIndex =
                            cursor.getColumnIndex(
                                    DatabaseHelper.COLUMN_NAME
                            );

                    int quantityIndex =
                            cursor.getColumnIndex(
                                    DatabaseHelper.COLUMN_QUANTITY
                            );

                    int unitIndex =
                            cursor.getColumnIndex(
                                    DatabaseHelper.COLUMN_UNIT
                            );

                    if (nameIndex >= 0 &&
                            quantityIndex >= 0 &&
                            unitIndex >= 0) {

                        long id =
                                idIndex >= 0
                                        ? cursor.getLong(idIndex)
                                        : 0;

                        String name =
                                cursor.getString(
                                        nameIndex
                                );

                        String quantity =
                                cursor.getString(
                                        quantityIndex
                                );

                        String unit =
                                cursor.getString(
                                        unitIndex
                                );

                        pantryItems.add(
                                new PantryItem(
                                        id,
                                        name,
                                        parseQuantity(quantity),
                                        unit
                                )
                        );
                    }

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private double parseQuantity(String value) {

        if (value == null) {
            return 0;
        }

        String cleaned =
                value.trim()
                        .replace(",", ".");

        try {

            return Double.parseDouble(
                    cleaned
            );

        } catch (Exception e) {

            return 0;
        }
    }

    private boolean canMakeRecipe(Recipe recipe) {

        for (RequiredIngredient required :
                recipe.requiredIngredients) {

            boolean ingredientFound = false;

            for (PantryItem pantry :
                    pantryItems) {

                if (ingredientMatches(
                        pantry.name,
                        required.name
                )) {

                    double pantryAmount =
                            convertToBaseUnit(
                                    pantry.quantity,
                                    pantry.unit,
                                    required.unit
                            );

                    double requiredAmount =
                            required.quantity;

                    if (pantryAmount >=
                            requiredAmount) {

                        ingredientFound = true;
                        break;
                    }
                }
            }

            if (!ingredientFound) {
                return false;
            }
        }

        return true;
    }

    private boolean ingredientMatches(
            String pantryName,
            String requiredName
    ) {

        if (pantryName == null ||
                requiredName == null) {

            return false;
        }

        String pantry =
                normaliseIngredient(
                        pantryName
                );

        String required =
                normaliseIngredient(
                        requiredName
                );

        if (pantry.equals(required)) {
            return true;
        }

        if (pantry.startsWith(required)) {
            return true;
        }

        if (required.startsWith(pantry)) {
            return true;
        }

        return false;
    }

    private String normaliseIngredient(
            String value
    ) {

        String result =
                value.toLowerCase(
                        Locale.US
                ).trim();

        result =
                result.replace(
                        "-", " "
                );

        result =
                result.replaceAll(
                        "\\s+",
                        " "
                );

        if (result.endsWith("ies")) {

            result =
                    result.substring(
                            0,
                            result.length() - 3
                    ) + "y";

        } else if (result.endsWith("oes")) {

            result =
                    result.substring(
                            0,
                            result.length() - 2
                    );

        } else if (result.endsWith("s") &&
                !result.endsWith("ss")) {

            result =
                    result.substring(
                            0,
                            result.length() - 1
                    );
        }

        return result;
    }

    private double convertToBaseUnit(
            double quantity,
            String pantryUnit,
            String requiredUnit
    ) {

        String pantry =
                normaliseUnit(pantryUnit);

        String required =
                normaliseUnit(requiredUnit);

        if (pantry.equals(required)) {
            return quantity;
        }

        if (required.equals("g")) {

            if (pantry.equals("kg")) {
                return quantity * 1000;
            }
        }

        if (required.equals("ml")) {

            if (pantry.equals("l")) {
                return quantity * 1000;
            }
        }

        if (required.equals("piece") ||
                required.equals("slice") ||
                required.equals("leaf") ||
                required.equals("clove") ||
                required.equals("can") ||
                required.equals("pinch")) {

            if (pantry.equals("piece") ||
                    pantry.equals("pieces") ||
                    pantry.equals("pcs") ||
                    pantry.equals("slice") ||
                    pantry.equals("slices") ||
                    pantry.equals("leaf") ||
                    pantry.equals("leaves") ||
                    pantry.equals("clove") ||
                    pantry.equals("cloves") ||
                    pantry.equals("can") ||
                    pantry.equals("cans") ||
                    pantry.equals("pinch")) {

                return quantity;
            }
        }

        return quantity;
    }

    private String normaliseUnit(
            String unit
    ) {

        if (unit == null) {
            return "";
        }

        String result =
                unit.toLowerCase(
                        Locale.US
                ).trim();

        if (result.equals("grams") ||
                result.equals("gram")) {
            return "g";
        }

        if (result.equals("kilograms") ||
                result.equals("kilogram") ||
                result.equals("kgs")) {
            return "kg";
        }

        if (result.equals("millilitres") ||
                result.equals("milliliters") ||
                result.equals("millilitre") ||
                result.equals("milliliter")) {
            return "ml";
        }

        if (result.equals("litres") ||
                result.equals("liters") ||
                result.equals("litre") ||
                result.equals("liter")) {
            return "l";
        }

        if (result.equals("pieces") ||
                result.equals("pcs") ||
                result.equals("pc")) {
            return "piece";
        }

        if (result.equals("slices")) {
            return "slice";
        }

        if (result.equals("leaves")) {
            return "leaf";
        }

        if (result.equals("cloves")) {
            return "clove";
        }

        if (result.equals("cans")) {
            return "can";
        }

        return result;
    }

    private void addRecipe(
            Recipe recipe
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                24,
                24,
                24,
                24
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.WHITE
        );

        background.setCornerRadius(
                24
        );

        background.setStroke(
                2,
                Color.rgb(
                        225,
                        225,
                        225
                )
        );

        card.setBackground(
                background
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

        TextView title =
                new TextView(this);

        title.setText(
                recipe.name
        );

        title.setTextColor(
                Color.rgb(
                        17,
                        17,
                        17
                )
        );

        title.setTextSize(
                23
        );

        title.setTypeface(
                null,
                Typeface.BOLD
        );

        card.addView(
                title
        );

        TextView description =
                new TextView(this);

        description.setText(
                recipe.description
        );

        description.setTextColor(
                Color.rgb(
                        90,
                        90,
                        90
                )
        );

        description.setTextSize(
                16
        );

        LinearLayout.LayoutParams descriptionParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        descriptionParams.setMargins(
                0,
                10,
                0,
                12
        );

        description.setLayoutParams(
                descriptionParams
        );

        card.addView(
                description
        );

        TextView ingredients =
                new TextView(this);

        ingredients.setText(
                "Required ingredients:\n"
                        + getIngredientList(
                        recipe
                )
        );

        ingredients.setTextColor(
                Color.rgb(
                        70,
                        70,
                        70
                )
        );

        ingredients.setTextSize(
                16
        );

        card.addView(
                ingredients
        );

        TextView time =
                new TextView(this);

        time.setText(
                "Cooking time: "
                        + recipe.cookingTime
                        + "     Difficulty: "
                        + recipe.difficulty
        );

        time.setTextColor(
                Color.rgb(
                        90,
                        90,
                        90
                )
        );

        time.setTextSize(
                15
        );

        LinearLayout.LayoutParams timeParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        timeParams.setMargins(
                0,
                12,
                0,
                12
        );

        time.setLayoutParams(
                timeParams
        );

        card.addView(
                time
        );

        Button viewRecipeButton =
                new Button(this);

        viewRecipeButton.setText(
                "View Recipe"
        );

        viewRecipeButton.setTextSize(
                16
        );

        viewRecipeButton.setAllCaps(
                false
        );

        viewRecipeButton.setTextColor(
                Color.WHITE
        );

        viewRecipeButton.setBackgroundTintList(
                ColorStateList.valueOf(
                        Color.rgb(
                                56,
                                142,
                                60
                        )
                )
        );

        viewRecipeButton.setOnClickListener(
                view ->
                        showRecipeDetails(
                                recipe
                        )
        );

        card.addView(
                viewRecipeButton
        );

        recipeContainer.addView(
                card
        );
    }

    private String getIngredientList(
            Recipe recipe
    ) {

        StringBuilder result =
                new StringBuilder();

        for (int i = 0;
             i < recipe.requiredIngredients.length;
             i++) {

            RequiredIngredient ingredient =
                    recipe.requiredIngredients[i];

            result.append(
                    "• "
                            + ingredient.quantity
                            + " "
                            + ingredient.unit
                            + " "
                            + ingredient.name
            );

            if (i <
                    recipe.requiredIngredients.length - 1) {

                result.append("\n");
            }
        }

        return result.toString();
    }

    private void showRecipeDetails(
            Recipe recipe
    ) {

        String message =
                recipe.description
                        + "\n\n"
                        + "Ingredients:\n"
                        + getIngredientList(
                        recipe
                )
                        + "\n\n"
                        + "Cooking Time: "
                        + recipe.cookingTime
                        + "\n"
                        + "Difficulty: "
                        + recipe.difficulty
                        + "\n\n"
                        + "Instructions:\n"
                        + recipe.instructions;

        new AlertDialog.Builder(this)
                .setTitle(
                        recipe.name
                )
                .setMessage(
                        message
                )
                .setPositiveButton(
                        "Close",
                        null
                )
                .show();
    }

    private static class PantryItem {

        long id;
        String name;
        double quantity;
        String unit;

        PantryItem(
                long id,
                String name,
                double quantity,
                String unit
        ) {

            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
    }

    private static class RequiredIngredient {

        String name;
        double quantity;
        String unit;

        RequiredIngredient(
                String name,
                double quantity,
                String unit
        ) {

            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
    }

    private static class Recipe {

        String name;
        String cookingTime;
        String difficulty;
        String description;
        RequiredIngredient[] requiredIngredients;
        String instructions;

        Recipe(
                String name,
                String cookingTime,
                String difficulty,
                String description,
                RequiredIngredient[] requiredIngredients,
                String instructions
        ) {

            this.name = name;
            this.cookingTime = cookingTime;
            this.difficulty = difficulty;
            this.description = description;
            this.requiredIngredients =
                    requiredIngredients;
            this.instructions = instructions;
        }
    }
}