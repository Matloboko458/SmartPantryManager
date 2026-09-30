package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class RecipesActivity extends AppCompatActivity {

    private LinearLayout recipeContainer;
    private Button btnAddRecipe;
    private Button btnBack;

    private final ArrayList<Recipe> recipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        recipeContainer = findViewById(R.id.recipeContainer);
        btnAddRecipe = findViewById(R.id.btnAddRecipe);
        btnBack = findViewById(R.id.btnBack);

        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Android system back button
        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        finish();
                    }
                }
        );

        // Add Recipe button
        if (btnAddRecipe != null) {
            btnAddRecipe.setOnClickListener(v -> showAddRecipeDialog());
        }

        loadDefaultRecipes();
        displayRecipes();
    }

    private void loadDefaultRecipes() {

        recipes.clear();

        recipes.add(new Recipe(
                "Egg & Cheese Toast",
                "A quick breakfast made with eggs, toasted bread and cheese.",
                "Eggs, Bread, Cheese",
                "10 minutes",
                "Easy",
                "2 eggs\n" +
                        "2 slices of bread\n" +
                        "2 slices of cheese\n" +
                        "Salt and pepper",
                "1. Beat the eggs in a bowl.\n" +
                        "2. Cook the eggs in a pan.\n" +
                        "3. Toast the bread.\n" +
                        "4. Add the cheese and cooked eggs.\n" +
                        "5. Season with salt and pepper."
        ));

        recipes.add(new Recipe(
                "Creamy Pasta",
                "A simple creamy pasta recipe using basic pantry ingredients.",
                "Pasta, Milk, Cheese",
                "20 minutes",
                "Easy",
                "Pasta\n" +
                        "Milk\n" +
                        "Cheese\n" +
                        "Salt and pepper",
                "1. Cook the pasta.\n" +
                        "2. Heat the milk in a pan.\n" +
                        "3. Add the cheese.\n" +
                        "4. Add the cooked pasta.\n" +
                        "5. Season and serve."
        ));

        recipes.add(new Recipe(
                "Chicken Rice Bowl",
                "A filling rice bowl made with chicken and vegetables.",
                "Chicken, Rice, Vegetables",
                "30 minutes",
                "Medium",
                "Chicken\n" +
                        "Rice\n" +
                        "Mixed vegetables\n" +
                        "Salt and pepper",
                "1. Cook the rice.\n" +
                        "2. Cook the chicken thoroughly.\n" +
                        "3. Cook the vegetables.\n" +
                        "4. Combine the rice, chicken and vegetables.\n" +
                        "5. Season and serve."
        ));
    }

    private void displayRecipes() {

        if (recipeContainer == null) {
            return;
        }

        recipeContainer.removeAllViews();

        for (int i = 0; i < recipes.size(); i++) {

            final int position = i;
            Recipe recipe = recipes.get(i);

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(16, 16, 16, 16);

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            cardParams.setMargins(0, 0, 0, 20);
            card.setLayoutParams(cardParams);

            TextView title = new TextView(this);
            title.setText(recipe.name);
            title.setTextSize(28);
            title.setTextColor(0xFF111111);
            title.setTypeface(null, Typeface.BOLD);

            TextView description = new TextView(this);
            description.setText(recipe.description);
            description.setTextSize(18);
            description.setTextColor(0xFF666666);
            description.setPadding(0, 8, 0, 8);

            TextView ingredients = new TextView(this);
            ingredients.setText("Ingredients: " + recipe.ingredients);
            ingredients.setTextSize(17);
            ingredients.setTextColor(0xFF666666);

            TextView details = new TextView(this);
            details.setText(
                    "Cooking time: " + recipe.cookingTime +
                            "     Difficulty: " + recipe.difficulty
            );
            details.setTextSize(17);
            details.setTextColor(0xFF666666);
            details.setPadding(0, 8, 0, 12);

            // VIEW RECIPE - LIGHT GREEN
            Button viewButton = new Button(this);
            viewButton.setText("View Recipe");
            viewButton.setTextSize(18);
            viewButton.setTextColor(0xFF1B5E20);
            viewButton.setBackgroundTintList(
                    ColorStateList.valueOf(0xFF81C784)
            );

            // EDIT RECIPE - MEDIUM GREEN
            Button editButton = new Button(this);
            editButton.setText("Edit Recipe");
            editButton.setTextSize(17);
            editButton.setTextColor(0xFF1B5E20);
            editButton.setBackgroundTintList(
                    ColorStateList.valueOf(0xFF66BB6A)
            );

            // DELETE RECIPE - SOFT RED
            Button deleteButton = new Button(this);
            deleteButton.setText("Delete Recipe");
            deleteButton.setTextSize(17);
            deleteButton.setTextColor(0xFF8B0000);
            deleteButton.setBackgroundTintList(
                    ColorStateList.valueOf(0xFFEF9A9A)
            );

            viewButton.setOnClickListener(v ->
                    showRecipeDialog(recipe)
            );

            editButton.setOnClickListener(v ->
                    showEditRecipeDialog(position)
            );

            deleteButton.setOnClickListener(v ->
                    confirmDeleteRecipe(position)
            );

            card.addView(title);
            card.addView(description);
            card.addView(ingredients);
            card.addView(details);
            card.addView(viewButton);
            card.addView(editButton);
            card.addView(deleteButton);

            recipeContainer.addView(card);
        }
    }

    private void showRecipeDialog(Recipe recipe) {

        String message =
                recipe.description +
                        "\n\nIngredients:\n" +
                        recipe.ingredientsDetailed +
                        "\n\nCooking Time: " +
                        recipe.cookingTime +
                        "\nDifficulty: " +
                        recipe.difficulty +
                        "\n\nInstructions:\n" +
                        recipe.instructions;

        new AlertDialog.Builder(this)
                .setTitle(recipe.name)
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
    }

    private void showAddRecipeDialog() {

        LinearLayout layout = createRecipeInputLayout();

        EditText nameInput = (EditText) layout.getChildAt(0);
        EditText descriptionInput = (EditText) layout.getChildAt(1);
        EditText ingredientsInput = (EditText) layout.getChildAt(2);
        EditText timeInput = (EditText) layout.getChildAt(3);
        EditText difficultyInput = (EditText) layout.getChildAt(4);
        EditText detailedIngredientsInput =
                (EditText) layout.getChildAt(5);
        EditText instructionsInput =
                (EditText) layout.getChildAt(6);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Recipe")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add", null)
                .create();

        dialog.setOnShowListener(d -> {

            Button addButton =
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            addButton.setOnClickListener(v -> {

                String name =
                        nameInput.getText().toString().trim();

                String description =
                        descriptionInput.getText().toString().trim();

                String ingredients =
                        ingredientsInput.getText().toString().trim();

                String time =
                        timeInput.getText().toString().trim();

                String difficulty =
                        difficultyInput.getText().toString().trim();

                String detailedIngredients =
                        detailedIngredientsInput
                                .getText()
                                .toString()
                                .trim();

                String instructions =
                        instructionsInput
                                .getText()
                                .toString()
                                .trim();

                if (name.isEmpty()) {
                    nameInput.setError("Enter a recipe name");
                    return;
                }

                if (ingredients.isEmpty()) {
                    ingredientsInput.setError("Enter the ingredients");
                    return;
                }

                recipes.add(new Recipe(
                        name,
                        description,
                        ingredients,
                        time,
                        difficulty,
                        detailedIngredients,
                        instructions
                ));

                displayRecipes();

                Toast.makeText(
                        RecipesActivity.this,
                        "Recipe added",
                        Toast.LENGTH_SHORT
                ).show();

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void showEditRecipeDialog(int position) {

        Recipe recipe = recipes.get(position);

        LinearLayout layout = createRecipeInputLayout();

        EditText nameInput = (EditText) layout.getChildAt(0);
        EditText descriptionInput = (EditText) layout.getChildAt(1);
        EditText ingredientsInput = (EditText) layout.getChildAt(2);
        EditText timeInput = (EditText) layout.getChildAt(3);
        EditText difficultyInput = (EditText) layout.getChildAt(4);
        EditText detailedIngredientsInput =
                (EditText) layout.getChildAt(5);
        EditText instructionsInput =
                (EditText) layout.getChildAt(6);

        nameInput.setText(recipe.name);
        descriptionInput.setText(recipe.description);
        ingredientsInput.setText(recipe.ingredients);
        timeInput.setText(recipe.cookingTime);
        difficultyInput.setText(recipe.difficulty);
        detailedIngredientsInput.setText(
                recipe.ingredientsDetailed
        );
        instructionsInput.setText(recipe.instructions);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Recipe")
                .setView(layout)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null)
                .create();

        dialog.setOnShowListener(d -> {

            Button saveButton =
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            saveButton.setOnClickListener(v -> {

                String name =
                        nameInput.getText().toString().trim();

                String description =
                        descriptionInput.getText().toString().trim();

                String ingredients =
                        ingredientsInput.getText().toString().trim();

                String time =
                        timeInput.getText().toString().trim();

                String difficulty =
                        difficultyInput.getText().toString().trim();

                String detailedIngredients =
                        detailedIngredientsInput
                                .getText()
                                .toString()
                                .trim();

                String instructions =
                        instructionsInput
                                .getText()
                                .toString()
                                .trim();

                if (name.isEmpty()) {
                    nameInput.setError("Enter a recipe name");
                    return;
                }

                if (ingredients.isEmpty()) {
                    ingredientsInput.setError("Enter the ingredients");
                    return;
                }

                recipe.name = name;
                recipe.description = description;
                recipe.ingredients = ingredients;
                recipe.cookingTime = time;
                recipe.difficulty = difficulty;
                recipe.ingredientsDetailed = detailedIngredients;
                recipe.instructions = instructions;

                displayRecipes();

                Toast.makeText(
                        RecipesActivity.this,
                        "Recipe updated",
                        Toast.LENGTH_SHORT
                ).show();

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void confirmDeleteRecipe(int position) {

        Recipe recipe = recipes.get(position);

        new AlertDialog.Builder(this)
                .setTitle("Delete Recipe")
                .setMessage(
                        "Are you sure you want to delete \"" +
                                recipe.name +
                                "\"?"
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            recipes.remove(position);
                            displayRecipes();

                            Toast.makeText(
                                    RecipesActivity.this,
                                    "Recipe deleted",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .show();
    }

    private LinearLayout createRecipeInputLayout() {

        LinearLayout layout = new LinearLayout(this);

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 10, 40, 10);

        EditText name =
                createInput("Recipe name");

        EditText description =
                createInput("Description");

        EditText ingredients =
                createInput("Ingredients");

        EditText time =
                createInput("Cooking time");

        EditText difficulty =
                createInput("Difficulty");

        EditText detailedIngredients =
                createInput("Detailed ingredients");

        EditText instructions =
                createInput("Instructions");

        layout.addView(name);
        layout.addView(description);
        layout.addView(ingredients);
        layout.addView(time);
        layout.addView(difficulty);
        layout.addView(detailedIngredients);
        layout.addView(instructions);

        return layout;
    }

    private EditText createInput(String hint) {

        EditText editText = new EditText(this);

        editText.setHint(hint);
        editText.setTextSize(16);
        editText.setPadding(0, 12, 0, 12);

        editText.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        return editText;
    }

    public static class Recipe {

        String name;
        String description;
        String ingredients;
        String cookingTime;
        String difficulty;
        String ingredientsDetailed;
        String instructions;

        Recipe(
                String name,
                String description,
                String ingredients,
                String cookingTime,
                String difficulty,
                String ingredientsDetailed,
                String instructions
        ) {
            this.name = name;
            this.description = description;
            this.ingredients = ingredients;
            this.cookingTime = cookingTime;
            this.difficulty = difficulty;
            this.ingredientsDetailed = ingredientsDetailed;
            this.instructions = instructions;
        }
    }
}