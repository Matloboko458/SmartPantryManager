package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class RecipesActivity extends AppCompatActivity {

    private LinearLayout recipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        recipeContainer = findViewById(R.id.recipeContainer);

        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        addRecipe(
                "Egg & Cheese Toast",
                "Eggs, Bread, Cheese",
                "10 minutes",
                "Easy",
                "A quick breakfast made with eggs, toasted bread and cheese.",
                "Ingredients:\n" +
                        "• 2 eggs\n" +
                        "• 2 slices of bread\n" +
                        "• 2 slices of cheese\n" +
                        "• Salt and pepper\n\n" +
                        "Instructions:\n" +
                        "1. Beat the eggs in a bowl.\n" +
                        "2. Cook the eggs in a pan.\n" +
                        "3. Toast the bread.\n" +
                        "4. Add the cheese and cooked eggs.\n" +
                        "5. Season with salt and pepper."
        );

        addRecipe(
                "Creamy Pasta",
                "Pasta, Milk, Cheese",
                "20 minutes",
                "Easy",
                "A simple creamy pasta recipe using basic pantry ingredients.",
                "Ingredients:\n" +
                        "• 250 g pasta\n" +
                        "• 1 cup milk\n" +
                        "• 1 cup grated cheese\n" +
                        "• Salt and pepper\n\n" +
                        "Instructions:\n" +
                        "1. Cook the pasta according to the package instructions.\n" +
                        "2. Drain the pasta.\n" +
                        "3. Heat the milk in a pan.\n" +
                        "4. Add the cheese and stir until melted.\n" +
                        "5. Add the pasta and mix well.\n" +
                        "6. Season and serve."
        );

        addRecipe(
                "Chicken Rice Bowl",
                "Chicken, Rice, Vegetables",
                "30 minutes",
                "Medium",
                "A filling rice bowl made with chicken and vegetables.",
                "Ingredients:\n" +
                        "• 1 cup rice\n" +
                        "• 250 g chicken\n" +
                        "• Mixed vegetables\n" +
                        "• Cooking oil\n" +
                        "• Salt and pepper\n\n" +
                        "Instructions:\n" +
                        "1. Cook the rice.\n" +
                        "2. Cut the chicken into small pieces.\n" +
                        "3. Cook the chicken in a pan.\n" +
                        "4. Add the vegetables.\n" +
                        "5. Season to taste.\n" +
                        "6. Serve the chicken and vegetables over rice."
        );

        addRecipe(
                "Pancakes",
                "Flour, Eggs, Milk",
                "15 minutes",
                "Easy",
                "Simple homemade pancakes suitable for breakfast.",
                "Ingredients:\n" +
                        "• 1 cup flour\n" +
                        "• 1 egg\n" +
                        "• 1 cup milk\n" +
                        "• 1 tablespoon sugar\n" +
                        "• 1 teaspoon baking powder\n\n" +
                        "Instructions:\n" +
                        "1. Mix the flour, sugar and baking powder.\n" +
                        "2. Add the egg and milk.\n" +
                        "3. Mix until smooth.\n" +
                        "4. Heat a lightly oiled pan.\n" +
                        "5. Pour in some batter.\n" +
                        "6. Cook both sides until golden."
        );

        addRecipe(
                "Vegetable Omelette",
                "Eggs, Vegetables, Cheese",
                "15 minutes",
                "Easy",
                "A healthy omelette using eggs and vegetables.",
                "Ingredients:\n" +
                        "• 3 eggs\n" +
                        "• Mixed vegetables\n" +
                        "• Cheese\n" +
                        "• Salt and pepper\n\n" +
                        "Instructions:\n" +
                        "1. Beat the eggs.\n" +
                        "2. Chop the vegetables.\n" +
                        "3. Cook the vegetables briefly.\n" +
                        "4. Add the eggs.\n" +
                        "5. Add cheese.\n" +
                        "6. Fold the omelette and serve."
        );
    }

    private void addRecipe(
            String recipeName,
            String ingredients,
            String cookingTime,
            String difficulty,
            String description,
            String instructions
    ) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 24, 24, 24);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(24);
        background.setStroke(2, Color.rgb(225, 225, 225));

        card.setBackground(background);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, 20);

        card.setLayoutParams(cardParams);

        TextView title = new TextView(this);

        title.setText(recipeName);
        title.setTextColor(Color.rgb(17, 17, 17));
        title.setTextSize(23);
        title.setTypeface(null, Typeface.BOLD);

        card.addView(title);

        TextView descriptionText = new TextView(this);

        descriptionText.setText(description);
        descriptionText.setTextColor(Color.rgb(100, 100, 100));
        descriptionText.setTextSize(16);

        LinearLayout.LayoutParams descriptionParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        descriptionParams.setMargins(0, 10, 0, 12);

        descriptionText.setLayoutParams(descriptionParams);

        card.addView(descriptionText);

        TextView ingredientText = new TextView(this);

        ingredientText.setText("Ingredients: " + ingredients);
        ingredientText.setTextColor(Color.rgb(70, 70, 70));
        ingredientText.setTextSize(16);

        card.addView(ingredientText);

        TextView timeText = new TextView(this);

        timeText.setText(
                "Cooking time: " + cookingTime +
                        "     Difficulty: " + difficulty
        );

        timeText.setTextColor(Color.rgb(90, 90, 90));
        timeText.setTextSize(15);

        LinearLayout.LayoutParams timeParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        timeParams.setMargins(0, 12, 0, 12);

        timeText.setLayoutParams(timeParams);

        card.addView(timeText);

        Button viewRecipeButton = new Button(this);

        viewRecipeButton.setText("View Recipe");
        viewRecipeButton.setTextColor(Color.rgb(56, 33, 109));
        viewRecipeButton.setTextSize(16);
        viewRecipeButton.setAllCaps(false);

        viewRecipeButton.setBackgroundColor(
                Color.rgb(199, 165, 240)
        );

        viewRecipeButton.setOnClickListener(
                v -> showRecipeDetails(
                        recipeName,
                        ingredients,
                        cookingTime,
                        difficulty,
                        description,
                        instructions
                )
        );

        card.addView(viewRecipeButton);

        recipeContainer.addView(card);
    }

    private void showRecipeDetails(
            String recipeName,
            String ingredients,
            String cookingTime,
            String difficulty,
            String description,
            String instructions
    ) {

        String message =
                description +
                        "\n\n" +
                        "Ingredients:\n" +
                        ingredients +
                        "\n\n" +
                        "Cooking Time: " +
                        cookingTime +
                        "\n" +
                        "Difficulty: " +
                        difficulty +
                        "\n\n" +
                        instructions;

        new AlertDialog.Builder(this)
                .setTitle(recipeName)
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
    }
}