package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipesActivity extends AppCompatActivity {

    private LinearLayout recipeContainer;
    private DatabaseHelper databaseHelper;
    private Spinner recipeFilterSpinner;
    private TextView txtSectionTitle;

    private String currentRecipeFilter = "Suggested Recipes";

    private final List<PantryItem> pantryItems =
            new ArrayList<>();

    private static final List<Recipe> recipes =
            new ArrayList<>();

    private static final String PREF_NAME =
            "SmartPantryPrefs";

    private static final String KEY_ALL_RECIPES_JSON =
            "all_recipes_json";

    private static final String KEY_DATABASE_MIGRATED =
            "recipes_database_migrated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipes);

        databaseHelper =
                new DatabaseHelper(this);

        recipeContainer =
                findViewById(R.id.recipeContainer);

        txtSectionTitle =
                findViewById(R.id.txtSectionTitle);

        Button btnBack =
                findViewById(R.id.btnBack);

        btnBack.setOnClickListener(
                view -> finish()
        );

        Button btnAddRecipe =
                findViewById(R.id.btnAddRecipe);

        btnAddRecipe.setOnClickListener(
                view -> showAddRecipeDialog()
        );

        recipeFilterSpinner =
                findViewById(R.id.recipeFilterSpinner);

        int textPrimary =
                ContextCompat.getColor(
                        this,
                        R.color.text_primary
                );

        int cardBg =
                ContextCompat.getColor(
                        this,
                        R.color.card_bg
                );

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        new String[]{
                                "Suggested Recipes",
                                "All Recipes"
                        }
                ) {

                    @Override
                    public View getView(
                            int position,
                            View convertView,
                            ViewGroup parent
                    ) {

                        View view =
                                super.getView(
                                        position,
                                        convertView,
                                        parent
                                );

                        TextView tv =
                                (TextView) view;

                        tv.setTextColor(
                                textPrimary
                        );

                        return view;
                    }

                    @Override
                    public View getDropDownView(
                            int position,
                            View convertView,
                            ViewGroup parent
                    ) {

                        View view =
                                super.getDropDownView(
                                        position,
                                        convertView,
                                        parent
                                );

                        TextView tv =
                                (TextView) view;

                        tv.setTextColor(
                                textPrimary
                        );

                        tv.setBackgroundColor(
                                cardBg
                        );

                        return view;
                    }
                };

        adapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item
        );

        recipeFilterSpinner.setAdapter(
                adapter
        );

        recipeFilterSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {

                        currentRecipeFilter =
                                parent
                                        .getItemAtPosition(position)
                                        .toString();

                        loadRecipes();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent
                    ) {
                    }
                }
        );

        initRecipes();

        loadRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null
                && recipeContainer != null) {

            loadRecipes();
        }
    }

    // ---------------------------------------------------------
    // INITIALISE RECIPE DATABASE
    // ---------------------------------------------------------

    private void initRecipes() {

        SharedPreferences prefs =
                getSharedPreferences(
                        PREF_NAME,
                        MODE_PRIVATE
                );

        boolean migrated =
                prefs.getBoolean(
                        KEY_DATABASE_MIGRATED,
                        false
                );

        if (!migrated) {

            String oldJson =
                    prefs.getString(
                            KEY_ALL_RECIPES_JSON,
                            null
                    );

            if (oldJson != null
                    && !oldJson.trim().isEmpty()) {

                migrateOldRecipesToDatabase(
                        oldJson
                );

            } else {

                databaseHelper
                        .seedRecipesIfEmpty();
            }

            prefs.edit()
                    .putBoolean(
                            KEY_DATABASE_MIGRATED,
                            true
                    )
                    .apply();

        } else {

            databaseHelper
                    .seedRecipesIfEmpty();
        }
    }

    // ---------------------------------------------------------
    // MIGRATE OLD SHARED PREFERENCES RECIPES
    // ---------------------------------------------------------

    private void migrateOldRecipesToDatabase(
            String jsonStr
    ) {

        ArrayList<Recipe> oldRecipes =
                new ArrayList<>();

        try {

            JSONArray arr =
                    new JSONArray(jsonStr);

            for (int i = 0;
                 i < arr.length();
                 i++) {

                JSONObject obj =
                        arr.getJSONObject(i);

                String name =
                        obj.optString(
                                "name",
                                ""
                        );

                String cookingTime =
                        obj.optString(
                                "cookingTime",
                                ""
                        );

                String difficulty =
                        obj.optString(
                                "difficulty",
                                ""
                        );

                String description =
                        obj.optString(
                                "description",
                                ""
                        );

                String instructions =
                        obj.optString(
                                "instructions",
                                ""
                        );

                JSONArray reqArr =
                        obj.optJSONArray(
                                "requiredIngredients"
                        );

                ArrayList<RequiredIngredient>
                        requiredIngredients =
                        new ArrayList<>();

                if (reqArr != null) {

                    for (int j = 0;
                         j < reqArr.length();
                         j++) {

                        JSONObject reqObj =
                                reqArr.getJSONObject(j);

                        String reqName =
                                reqObj.optString(
                                        "name",
                                        ""
                                );

                        double quantity =
                                reqObj.optDouble(
                                        "quantity",
                                        1
                                );

                        String unit =
                                reqObj.optString(
                                        "unit",
                                        "piece"
                                );

                        if (!reqName.isEmpty()) {

                            requiredIngredients.add(
                                    new RequiredIngredient(
                                            reqName,
                                            quantity,
                                            unit
                                    )
                            );
                        }
                    }
                }

                if (!name.isEmpty()) {

                    oldRecipes.add(
                            new Recipe(
                                    -1,
                                    name,
                                    cookingTime,
                                    difficulty,
                                    description,
                                    requiredIngredients,
                                    instructions
                            )
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();

            return;
        }

        if (oldRecipes.isEmpty()) {

            databaseHelper
                    .seedRecipesIfEmpty();

            return;
        }

        SQLiteDatabase db =
                databaseHelper
                        .getWritableDatabase();

        db.beginTransaction();

        try {

            db.delete(
                    DatabaseHelper.TABLE_RECIPE_INGREDIENTS,
                    null,
                    null
            );

            db.delete(
                    DatabaseHelper.TABLE_RECIPES,
                    null,
                    null
            );

            for (Recipe recipe :
                    oldRecipes) {

                ContentValues recipeValues =
                        new ContentValues();

                recipeValues.put(
                        DatabaseHelper.COLUMN_RECIPE_NAME,
                        recipe.name
                );

                recipeValues.put(
                        DatabaseHelper
                                .COLUMN_RECIPE_DESCRIPTION,
                        recipe.description
                );

                recipeValues.put(
                        DatabaseHelper.COLUMN_COOKING_TIME,
                        recipe.cookingTime
                );

                recipeValues.put(
                        DatabaseHelper.COLUMN_DIFFICULTY,
                        recipe.difficulty
                );

                recipeValues.put(
                        DatabaseHelper.COLUMN_INSTRUCTIONS,
                        recipe.instructions
                );

                long recipeId =
                        db.insert(
                                DatabaseHelper.TABLE_RECIPES,
                                null,
                                recipeValues
                        );

                if (recipeId == -1) {
                    continue;
                }

                for (RequiredIngredient required :
                        recipe.requiredIngredients) {

                    ContentValues ingredientValues =
                            new ContentValues();

                    ingredientValues.put(
                            DatabaseHelper.COLUMN_RECIPE_ID_FK,
                            recipeId
                    );

                    ingredientValues.put(
                            DatabaseHelper.COLUMN_INGREDIENT_NAME,
                            required.name
                    );

                    ingredientValues.put(
                            DatabaseHelper.COLUMN_REQUIRED_QUANTITY,
                            required.quantity
                    );

                    ingredientValues.put(
                            DatabaseHelper.COLUMN_REQUIRED_UNIT,
                            required.unit
                    );

                    db.insert(
                            DatabaseHelper
                                    .TABLE_RECIPE_INGREDIENTS,
                            null,
                            ingredientValues
                    );
                }
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            db.endTransaction();
        }
    }

    // ---------------------------------------------------------
    // LOAD RECIPES FROM SQLITE
    // ---------------------------------------------------------

    private void loadRecipes() {

        if (txtSectionTitle != null) {

            if (currentRecipeFilter != null
                    && currentRecipeFilter.contains(
                    "All"
            )) {

                txtSectionTitle.setText(
                        "Recipes"
                );

            } else {

                txtSectionTitle.setText(
                        "Suggested Recipes"
                );
            }
        }

        loadPantryIngredients();

        recipes.clear();

        Cursor cursor = null;

        try {

            cursor =
                    databaseHelper.getAllRecipes();

            while (cursor.moveToNext()) {

                long recipeId =
                        cursor.getLong(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_RECIPE_ID
                                )
                        );

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_RECIPE_NAME
                                )
                        );

                String cookingTime =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_COOKING_TIME
                                )
                        );

                String difficulty =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_DIFFICULTY
                                )
                        );

                String description =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_RECIPE_DESCRIPTION
                                )
                        );

                String instructions =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COLUMN_INSTRUCTIONS
                                )
                        );

                ArrayList<RequiredIngredient>
                        requiredIngredients =
                        loadRequiredIngredients(
                                recipeId
                        );

                recipes.add(
                        new Recipe(
                                recipeId,
                                name,
                                cookingTime,
                                difficulty,
                                description,
                                requiredIngredients,
                                instructions
                        )
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(
                    this,
                    "Unable to load recipes.",
                    Toast.LENGTH_SHORT
            ).show();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        if (recipeContainer == null) {
            return;
        }

        recipeContainer.removeAllViews();

        int displayedRecipeCount = 0;

        for (Recipe recipe : recipes) {

            boolean shouldShow;

            if (currentRecipeFilter != null
                    && currentRecipeFilter.equals(
                    "All Recipes"
            )) {

                shouldShow = true;

            } else {

                shouldShow =
                        canMakeRecipe(recipe);
            }

            if (shouldShow) {

                addRecipe(recipe);

                displayedRecipeCount++;
            }
        }

        if (displayedRecipeCount == 0) {

            TextView emptyText =
                    new TextView(this);

            if (currentRecipeFilter != null
                    && currentRecipeFilter.equals(
                    "All Recipes"
            )) {

                emptyText.setText(
                        "No recipes available."
                );

            } else {

                emptyText.setText(
                        "No recipes match your pantry yet.\n\n" +
                                "Add more ingredients to unlock recipes."
                );
            }

            emptyText.setTextSize(19);

            emptyText.setTextColor(
                    ContextCompat.getColor(
                            this,
                            R.color.text_secondary
                    )
            );

            emptyText.setGravity(
                    Gravity.CENTER
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

    // ---------------------------------------------------------
    // LOAD REQUIRED INGREDIENTS
    // ---------------------------------------------------------

    private ArrayList<RequiredIngredient>
    loadRequiredIngredients(
            long recipeId
    ) {

        ArrayList<RequiredIngredient> list =
                new ArrayList<>();

        Cursor cursor = null;

        try {

            cursor =
                    databaseHelper
                            .getRecipeIngredients(
                                    recipeId
                            );

            while (cursor.moveToNext()) {

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper
                                                .COLUMN_INGREDIENT_NAME
                                )
                        );

                double quantity =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper
                                                .COLUMN_REQUIRED_QUANTITY
                                )
                        );

                String unit =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper
                                                .COLUMN_REQUIRED_UNIT
                                )
                        );

                list.add(
                        new RequiredIngredient(
                                name,
                                quantity,
                                unit
                        )
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        return list;
    }

    // ---------------------------------------------------------
    // LOAD PANTRY INGREDIENTS
    // ---------------------------------------------------------

    private void loadPantryIngredients() {

        pantryItems.clear();

        Cursor cursor = null;

        try {

            cursor =
                    databaseHelper
                            .getAllIngredients();

            if (cursor != null
                    && cursor.moveToFirst()) {

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

                    if (nameIndex >= 0
                            && quantityIndex >= 0
                            && unitIndex >= 0) {

                        long id =
                                idIndex >= 0
                                        ? cursor.getLong(
                                        idIndex
                                )
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
                                        parseQuantity(
                                                quantity
                                        ),
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

    // ---------------------------------------------------------
    // STRICT RECIPE MATCHING
    // ---------------------------------------------------------

    private boolean canMakeRecipe(
            Recipe recipe
    ) {

        for (RequiredIngredient required :
                recipe.requiredIngredients) {

            boolean ingredientFound =
                    false;

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

                        ingredientFound =
                                true;

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

    // ---------------------------------------------------------
    // INGREDIENT NAME MATCHING
    // ---------------------------------------------------------

    private boolean ingredientMatches(
            String pantryName,
            String requiredName
    ) {

        if (pantryName == null
                || requiredName == null) {

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

        return pantry.startsWith(required)
                || required.startsWith(pantry);
    }

    // ---------------------------------------------------------
    // NORMALISE INGREDIENT
    // ---------------------------------------------------------

    private String normaliseIngredient(
            String value
    ) {

        String result =
                value.toLowerCase(
                        Locale.US
                ).trim();

        result =
                result.replace(
                        "-",
                        " "
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

        } else if (
                result.endsWith("s")
                        && !result.endsWith("ss")
        ) {

            result =
                    result.substring(
                            0,
                            result.length() - 1
                    );
        }

        return result;
    }

    // ---------------------------------------------------------
    // UNIT CONVERSION
    // ---------------------------------------------------------

    private double convertToBaseUnit(
            double quantity,
            String pantryUnit,
            String requiredUnit
    ) {

        String pantry =
                normaliseUnit(
                        pantryUnit
                );

        String required =
                normaliseUnit(
                        requiredUnit
                );

        if (pantry.equals(required)) {

            return quantity;
        }

        if (required.equals("g")) {

            if (pantry.equals("kg")) {

                return quantity * 1000;
            }
        }

        if (required.equals("kg")) {

            if (pantry.equals("g")) {

                return quantity / 1000;
            }
        }

        if (required.equals("ml")) {

            if (pantry.equals("l")) {

                return quantity * 1000;
            }
        }

        if (required.equals("l")) {

            if (pantry.equals("ml")) {

                return quantity / 1000;
            }
        }

        if (required.equals("piece")
                || required.equals("slice")
                || required.equals("leaf")
                || required.equals("clove")
                || required.equals("can")
                || required.equals("pinch")
                || required.equals("cup")
                || required.equals("scoop")
                || required.equals("tbsp")
                || required.equals("tsp")) {

            if (pantry.equals("piece")
                    || pantry.equals("slice")
                    || pantry.equals("leaf")
                    || pantry.equals("clove")
                    || pantry.equals("can")
                    || pantry.equals("pinch")
                    || pantry.equals("cup")
                    || pantry.equals("scoop")
                    || pantry.equals("tbsp")
                    || pantry.equals("tsp")) {

                return quantity;
            }
        }

        return quantity;
    }

    // ---------------------------------------------------------
    // NORMALISE UNIT
    // ---------------------------------------------------------

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

        if (result.equals("grams")
                || result.equals("gram")) {

            return "g";
        }

        if (result.equals("kilograms")
                || result.equals("kilogram")
                || result.equals("kgs")) {

            return "kg";
        }

        if (
                result.equals("millilitres")
                        || result.equals("milliliters")
                        || result.equals("millilitre")
                        || result.equals("milliliter")
        ) {

            return "ml";
        }

        if (
                result.equals("litres")
                        || result.equals("liters")
                        || result.equals("litre")
                        || result.equals("liter")
        ) {

            return "l";
        }

        if (
                result.equals("pieces")
                        || result.equals("pcs")
                        || result.equals("pc")
        ) {

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

        if (result.equals("cups")) {
            return "cup";
        }

        if (result.equals("scoops")) {
            return "scoop";
        }

        if (
                result.equals("pinches")
        ) {

            return "pinch";
        }

        if (
                result.equals("tablespoon")
                        || result.equals("tablespoons")
                        || result.equals("tbsp")
        ) {

            return "tbsp";
        }

        if (
                result.equals("teaspoon")
                        || result.equals("teaspoons")
                        || result.equals("tsp")
        ) {

            return "tsp";
        }

        return result;
    }

    // ---------------------------------------------------------
    // PARSE QUANTITY
    // ---------------------------------------------------------

    private double parseQuantity(
            String value
    ) {

        if (value == null) {
            return 0;
        }

        String cleaned =
                value.trim()
                        .replace(
                                ",",
                                "."
                        );

        try {

            return Double.parseDouble(
                    cleaned
            );

        } catch (Exception e) {

            return 0;
        }
    }

    // ---------------------------------------------------------
    // ADD RECIPE
    // ---------------------------------------------------------

    private void showAddRecipeDialog() {

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                40,
                20,
                40,
                10
        );

        int textPrimary =
                ContextCompat.getColor(
                        this,
                        R.color.text_primary
                );

        int textHint =
                ContextCompat.getColor(
                        this,
                        R.color.text_hint
                );

        final EditText nameInput =
                new EditText(this);

        nameInput.setHint(
                "Recipe Name"
        );

        nameInput.setHintTextColor(
                textHint
        );

        nameInput.setTextColor(
                textPrimary
        );

        nameInput.setSingleLine(
                true
        );

        layout.addView(
                nameInput
        );

        final EditText descInput =
                new EditText(this);

        descInput.setHint(
                "Description"
        );

        descInput.setHintTextColor(
                textHint
        );

        descInput.setTextColor(
                textPrimary
        );

        descInput.setSingleLine(
                true
        );

        layout.addView(
                descInput
        );

        final EditText timeInput =
                new EditText(this);

        timeInput.setHint(
                "Cooking Time (e.g., 15 minutes)"
        );

        timeInput.setHintTextColor(
                textHint
        );

        timeInput.setTextColor(
                textPrimary
        );

        timeInput.setSingleLine(
                true
        );

        layout.addView(
                timeInput
        );

        final EditText diffInput =
                new EditText(this);

        diffInput.setHint(
                "Difficulty (Easy / Medium / Hard)"
        );

        diffInput.setHintTextColor(
                textHint
        );

        diffInput.setTextColor(
                textPrimary
        );

        diffInput.setSingleLine(
                true
        );

        layout.addView(
                diffInput
        );

        final EditText ingNameInput =
                new EditText(this);

        ingNameInput.setHint(
                "Main Required Ingredient (e.g., egg)"
        );

        ingNameInput.setHintTextColor(
                textHint
        );

        ingNameInput.setTextColor(
                textPrimary
        );

        ingNameInput.setSingleLine(
                true
        );

        layout.addView(
                ingNameInput
        );

        final EditText ingQtyInput =
                new EditText(this);

        ingQtyInput.setHint(
                "Ingredient Quantity (e.g., 2)"
        );

        ingQtyInput.setHintTextColor(
                textHint
        );

        ingQtyInput.setTextColor(
                textPrimary
        );

        ingQtyInput.setSingleLine(
                true
        );

        ingQtyInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        layout.addView(
                ingQtyInput
        );

        final EditText ingUnitInput =
                new EditText(this);

        ingUnitInput.setHint(
                "Ingredient Unit (e.g., piece, g, ml)"
        );

        ingUnitInput.setHintTextColor(
                textHint
        );

        ingUnitInput.setTextColor(
                textPrimary
        );

        ingUnitInput.setSingleLine(
                true
        );

        layout.addView(
                ingUnitInput
        );

        final EditText instInput =
                new EditText(this);

        instInput.setHint(
                "Instructions (e.g., 1. Cook... 2. Serve)"
        );

        instInput.setHintTextColor(
                textHint
        );

        instInput.setTextColor(
                textPrimary
        );

        instInput.setTextSize(
                16
        );

        instInput.setGravity(
                Gravity.TOP
        );

        instInput.setMinLines(
                4
        );

        layout.addView(
                instInput
        );

        ScrollView scrollView =
                new ScrollView(this);

        scrollView.addView(
                layout
        );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle(
                                "Add Recipe"
                        )
                        .setView(
                                scrollView
                        )
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
                d -> {

                    Button saveButton =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );

                    saveButton.setOnClickListener(
                            v -> {

                                String name =
                                        nameInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String desc =
                                        descInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String time =
                                        timeInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String diff =
                                        diffInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String ingName =
                                        ingNameInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String ingQtyStr =
                                        ingQtyInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String ingUnit =
                                        ingUnitInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String inst =
                                        instInput
                                                .getText()
                                                .toString()
                                                .trim();

                                if (
                                        name.isEmpty()
                                                || desc.isEmpty()
                                                || time.isEmpty()
                                                || diff.isEmpty()
                                                || ingName.isEmpty()
                                                || ingQtyStr.isEmpty()
                                                || ingUnit.isEmpty()
                                                || inst.isEmpty()
                                ) {

                                    Toast.makeText(
                                            this,
                                            "Please fill in all fields.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                double ingQty =
                                        parseQuantity(
                                                ingQtyStr
                                        );

                                long recipeId =
                                        databaseHelper.addRecipe(
                                                name,
                                                desc,
                                                time,
                                                diff,
                                                inst
                                        );

                                if (recipeId == -1) {

                                    Toast.makeText(
                                            this,
                                            "Recipe could not be added.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                databaseHelper.addRecipeIngredient(
                                        recipeId,
                                        ingName,
                                        ingQty,
                                        ingUnit
                                );

                                Toast.makeText(
                                        this,
                                        "Recipe added.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                dialog.dismiss();

                                loadRecipes();

                                saveRecipesToPrefs();
                            }
                    );
                }
        );

        dialog.show();
    }

    // ---------------------------------------------------------
    // EDIT RECIPE
    // ---------------------------------------------------------

    private void showEditRecipeDialog(
            Recipe recipe
    ) {

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                40,
                20,
                40,
                10
        );

        int textPrimary =
                ContextCompat.getColor(
                        this,
                        R.color.text_primary
                );

        int textHint =
                ContextCompat.getColor(
                        this,
                        R.color.text_hint
                );

        final EditText nameInput =
                new EditText(this);

        nameInput.setHint(
                "Recipe Name"
        );

        nameInput.setHintTextColor(
                textHint
        );

        nameInput.setTextColor(
                textPrimary
        );

        nameInput.setSingleLine(
                true
        );

        nameInput.setText(
                recipe.name
        );

        layout.addView(
                nameInput
        );

        final EditText descInput =
                new EditText(this);

        descInput.setHint(
                "Description"
        );

        descInput.setHintTextColor(
                textHint
        );

        descInput.setTextColor(
                textPrimary
        );

        descInput.setSingleLine(
                true
        );

        descInput.setText(
                recipe.description
        );

        layout.addView(
                descInput
        );

        final EditText timeInput =
                new EditText(this);

        timeInput.setHint(
                "Cooking Time (e.g., 15 minutes)"
        );

        timeInput.setHintTextColor(
                textHint
        );

        timeInput.setTextColor(
                textPrimary
        );

        timeInput.setSingleLine(
                true
        );

        timeInput.setText(
                recipe.cookingTime
        );

        layout.addView(
                timeInput
        );

        final EditText diffInput =
                new EditText(this);

        diffInput.setHint(
                "Difficulty (Easy / Medium / Hard)"
        );

        diffInput.setHintTextColor(
                textHint
        );

        diffInput.setTextColor(
                textPrimary
        );

        diffInput.setSingleLine(
                true
        );

        diffInput.setText(
                recipe.difficulty
        );

        layout.addView(
                diffInput
        );

        String mainIngName =
                recipe.requiredIngredients.size() > 0
                        ? recipe.requiredIngredients
                        .get(0)
                          .name
                        : "";

        String mainIngQty =
                recipe.requiredIngredients.size() > 0
                        ? String.valueOf(
                        recipe.requiredIngredients
                                .get(0)
                        .quantity
                )
                        : "";

        String mainIngUnit =
                recipe.requiredIngredients.size() > 0
                        ? recipe.requiredIngredients
                        .get(0)
                          .unit
                        : "";

        final EditText ingNameInput =
                new EditText(this);

        ingNameInput.setHint(
                "Main Required Ingredient (e.g., egg)"
        );

        ingNameInput.setHintTextColor(
                textHint
        );

        ingNameInput.setTextColor(
                textPrimary
        );

        ingNameInput.setSingleLine(
                true
        );

        ingNameInput.setText(
                mainIngName
        );

        layout.addView(
                ingNameInput
        );

        final EditText ingQtyInput =
                new EditText(this);

        ingQtyInput.setHint(
                "Ingredient Quantity (e.g., 2)"
        );

        ingQtyInput.setHintTextColor(
                textHint
        );

        ingQtyInput.setTextColor(
                textPrimary
        );

        ingQtyInput.setSingleLine(
                true
        );

        ingQtyInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        ingQtyInput.setText(
                mainIngQty
        );

        layout.addView(
                ingQtyInput
        );

        final EditText ingUnitInput =
                new EditText(this);

        ingUnitInput.setHint(
                "Ingredient Unit (e.g., piece, g, ml)"
        );

        ingUnitInput.setHintTextColor(
                textHint
        );

        ingUnitInput.setTextColor(
                textPrimary
        );

        ingUnitInput.setSingleLine(
                true
        );

        ingUnitInput.setText(
                mainIngUnit
        );

        layout.addView(
                ingUnitInput
        );

        final EditText instInput =
                new EditText(this);

        instInput.setHint(
                "Instructions (e.g., 1. Cook... 2. Serve)"
        );

        instInput.setHintTextColor(
                textHint
        );

        instInput.setTextColor(
                textPrimary
        );

        instInput.setText(
                recipe.instructions
        );

        instInput.setGravity(
                Gravity.TOP
        );

        instInput.setMinLines(
                4
        );

        layout.addView(
                instInput
        );

        ScrollView scrollView =
                new ScrollView(this);

        scrollView.addView(
                layout
        );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle(
                                "Edit Recipe"
                        )
                        .setView(
                                scrollView
                        )
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
                d -> {

                    Button saveButton =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );

                    saveButton.setOnClickListener(
                            v -> {

                                String name =
                                        nameInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String desc =
                                        descInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String time =
                                        timeInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String diff =
                                        diffInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String ingName =
                                        ingNameInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String ingQtyStr =
                                        ingQtyInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String ingUnit =
                                        ingUnitInput
                                                .getText()
                                                .toString()
                                                .trim();

                                String inst =
                                        instInput
                                                .getText()
                                                .toString()
                                                .trim();

                                if (
                                        name.isEmpty()
                                                || desc.isEmpty()
                                                || time.isEmpty()
                                                || diff.isEmpty()
                                                || ingName.isEmpty()
                                                || ingQtyStr.isEmpty()
                                                || ingUnit.isEmpty()
                                                || inst.isEmpty()
                                ) {

                                    Toast.makeText(
                                            this,
                                            "Please fill in all fields.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }

                                double ingQty =
                                        parseQuantity(
                                                ingQtyStr
                                        );

                                SQLiteDatabase db =
                                        databaseHelper
                                                .getWritableDatabase();

                                ContentValues values =
                                        new ContentValues();

                                values.put(
                                        DatabaseHelper
                                                .COLUMN_RECIPE_NAME,
                                        name
                                );

                                values.put(
                                        DatabaseHelper
                                                .COLUMN_RECIPE_DESCRIPTION,
                                        desc
                                );

                                values.put(
                                        DatabaseHelper
                                                .COLUMN_COOKING_TIME,
                                        time
                                );

                                values.put(
                                        DatabaseHelper
                                                .COLUMN_DIFFICULTY,
                                        diff
                                );

                                values.put(
                                        DatabaseHelper
                                                .COLUMN_INSTRUCTIONS,
                                        inst
                                );

                                int rowsUpdated =
                                        db.update(
                                                DatabaseHelper
                                                        .TABLE_RECIPES,
                                                values,
                                                DatabaseHelper
                                                        .COLUMN_RECIPE_ID
                                                        + " = ?",
                                                new String[]{
                                                        String.valueOf(
                                                                recipe.id
                                                        )
                                                }
                                        );

                                if (rowsUpdated > 0) {

                                    db.delete(
                                            DatabaseHelper
                                                    .TABLE_RECIPE_INGREDIENTS,
                                            DatabaseHelper
                                                    .COLUMN_RECIPE_ID_FK
                                                    + " = ?",
                                            new String[]{
                                                    String.valueOf(
                                                            recipe.id
                                                    )
                                            }
                                    );

                                    databaseHelper
                                            .addRecipeIngredient(
                                                    recipe.id,
                                                    ingName,
                                                    ingQty,
                                                    ingUnit
                                            );

                                    Toast.makeText(
                                            this,
                                            "Recipe updated.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    dialog.dismiss();

                                    loadRecipes();

                                    saveRecipesToPrefs();

                                } else {

                                    Toast.makeText(
                                            this,
                                            "Recipe could not be updated.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                    );
                }
        );

        dialog.show();
    }

    // ---------------------------------------------------------
    // DELETE RECIPE
    // ---------------------------------------------------------

    private void confirmDeleteRecipe(
            Recipe recipe
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Delete Recipe"
                )
                .setMessage(
                        "Are you sure you want to delete '" +
                                recipe.name +
                                "'?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            SQLiteDatabase db =
                                    databaseHelper
                                            .getWritableDatabase();

                            int deleted =
                                    db.delete(
                                            DatabaseHelper
                                                    .TABLE_RECIPES,
                                            DatabaseHelper
                                                    .COLUMN_RECIPE_ID
                                                    + " = ?",
                                            new String[]{
                                                    String.valueOf(
                                                            recipe.id
                                                    )
                                            }
                                    );

                            if (deleted > 0) {

                                Toast.makeText(
                                        this,
                                        "Recipe deleted.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                loadRecipes();

                                saveRecipesToPrefs();

                            } else {

                                Toast.makeText(
                                        this,
                                        "Recipe could not be deleted.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                )
                .show();
    }

    // ---------------------------------------------------------
    // RECIPE CARD
    // ---------------------------------------------------------

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

        int cardBg =
                ContextCompat.getColor(
                        this,
                        R.color.card_bg
                );

        int cardStroke =
                ContextCompat.getColor(
                        this,
                        R.color.card_stroke
                );

        int textPrimary =
                ContextCompat.getColor(
                        this,
                        R.color.text_primary
                );

        int textSecondary =
                ContextCompat.getColor(
                        this,
                        R.color.text_secondary
                );

        int primaryGreenDark =
                ContextCompat.getColor(
                        this,
                        R.color.primary_green_dark
                );

        int primaryGreen =
                ContextCompat.getColor(
                        this,
                        R.color.primary_green
                );

        int dangerRed =
                ContextCompat.getColor(
                        this,
                        R.color.danger_red
                );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                cardBg
        );

        background.setCornerRadius(
                24
        );

        background.setStroke(
                2,
                cardStroke
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
                textPrimary
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
                textSecondary
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
                "Required ingredients:\n" +
                        getIngredientList(recipe)
        );

        ingredients.setTextColor(
                textSecondary
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
                "Cooking time: " +
                        recipe.cookingTime +
                        "     Difficulty: " +
                        recipe.difficulty
        );

        time.setTextColor(
                textSecondary
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

        LinearLayout buttonRow =
                new LinearLayout(this);

        buttonRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        rowParams.setMargins(
                0,
                12,
                0,
                0
        );

        buttonRow.setLayoutParams(
                rowParams
        );

        Button viewBtn =
                new Button(this);

        viewBtn.setText(
                "View"
        );

        viewBtn.setTextSize(
                15
        );

        viewBtn.setAllCaps(
                false
        );

        viewBtn.setTextColor(
                Color.WHITE
        );

        viewBtn.setBackgroundTintList(
                ColorStateList.valueOf(
                        primaryGreenDark
                )
        );

        LinearLayout.LayoutParams viewParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        viewParams.setMarginEnd(
                4
        );

        viewBtn.setLayoutParams(
                viewParams
        );

        viewBtn.setOnClickListener(
                view ->
                        showRecipeDetails(
                                recipe
                        )
        );

        Button editBtn =
                new Button(this);

        editBtn.setText(
                "Edit"
        );

        editBtn.setTextSize(
                15
        );

        editBtn.setAllCaps(
                false
        );

        editBtn.setTextColor(
                Color.WHITE
        );

        editBtn.setBackgroundTintList(
                ColorStateList.valueOf(
                        primaryGreen
                )
        );

        LinearLayout.LayoutParams editParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        editParams.setMarginStart(
                4
        );

        editParams.setMarginEnd(
                4
        );

        editBtn.setLayoutParams(
                editParams
        );

        editBtn.setOnClickListener(
                view ->
                        showEditRecipeDialog(
                                recipe
                        )
        );

        Button deleteBtn =
                new Button(this);

        deleteBtn.setText(
                "Delete"
        );

        deleteBtn.setTextSize(
                15
        );

        deleteBtn.setAllCaps(
                false
        );

        deleteBtn.setTextColor(
                Color.WHITE
        );

        deleteBtn.setBackgroundTintList(
                ColorStateList.valueOf(
                        dangerRed
                )
        );

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        deleteParams.setMarginStart(
                4
        );

        deleteBtn.setLayoutParams(
                deleteParams
        );

        deleteBtn.setOnClickListener(
                view ->
                        confirmDeleteRecipe(
                                recipe
                        )
        );

        buttonRow.addView(
                viewBtn
        );

        buttonRow.addView(
                editBtn
        );

        buttonRow.addView(
                deleteBtn
        );

        card.addView(
                buttonRow
        );

        recipeContainer.addView(
                card
        );
    }

    // ---------------------------------------------------------
    // INGREDIENT LIST
    // ---------------------------------------------------------

    private String getIngredientList(
            Recipe recipe
    ) {

        StringBuilder result =
                new StringBuilder();

        for (int i = 0;
             i < recipe.requiredIngredients.size();
             i++) {

            RequiredIngredient ingredient =
                    recipe.requiredIngredients
                            .get(i);

            result.append(
                            "• "
                    )
                    .append(
                            ingredient.quantity
                    )
                    .append(
                            " "
                    )
                    .append(
                            ingredient.unit
                    )
                    .append(
                            " "
                    )
                    .append(
                            ingredient.name
                    );

            if (
                    i <
                            recipe.requiredIngredients.size()
                                    - 1
            ) {

                result.append(
                        "\n"
                );
            }
        }

        return result.toString();
    }

    // ---------------------------------------------------------
    // VIEW RECIPE DETAILS
    // ---------------------------------------------------------

    private void showRecipeDetails(
            Recipe recipe
    ) {

        String message =
                recipe.description
                        + "\n\nIngredients:\n"
                        + getIngredientList(
                        recipe
                )
                        + "\n\nCooking Time: "
                        + recipe.cookingTime
                        + "\nDifficulty: "
                        + recipe.difficulty
                        + "\n\nInstructions:\n"
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

    // ---------------------------------------------------------
    // SAVE COMPATIBILITY COPY
    // ---------------------------------------------------------

    private void saveRecipesToPrefs() {

        try {

            SharedPreferences prefs =
                    getSharedPreferences(
                            PREF_NAME,
                            MODE_PRIVATE
                    );

            JSONArray arr =
                    new JSONArray();

            for (Recipe recipe :
                    recipes) {

                JSONObject obj =
                        new JSONObject();

                obj.put(
                        "name",
                        recipe.name
                );

                obj.put(
                        "cookingTime",
                        recipe.cookingTime
                );

                obj.put(
                        "difficulty",
                        recipe.difficulty
                );

                obj.put(
                        "description",
                        recipe.description
                );

                obj.put(
                        "instructions",
                        recipe.instructions
                );

                JSONArray reqArr =
                        new JSONArray();

                for (RequiredIngredient req :
                        recipe.requiredIngredients) {

                    JSONObject reqObj =
                            new JSONObject();

                    reqObj.put(
                            "name",
                            req.name
                    );

                    reqObj.put(
                            "quantity",
                            req.quantity
                    );

                    reqObj.put(
                            "unit",
                            req.unit
                    );

                    reqArr.put(
                            reqObj
                    );
                }

                obj.put(
                        "requiredIngredients",
                        reqArr
                );

                arr.put(
                        obj
                );
            }

            prefs.edit()
                    .putString(
                            KEY_ALL_RECIPES_JSON,
                            arr.toString()
                    )
                    .apply();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------
    // DATA CLASSES
    // ---------------------------------------------------------

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

        long id;

        String name;

        String cookingTime;

        String difficulty;

        String description;

        List<RequiredIngredient>
                requiredIngredients;

        String instructions;

        Recipe(
                long id,
                String name,
                String cookingTime,
                String difficulty,
                String description,
                List<RequiredIngredient>
                        requiredIngredients,
                String instructions
        ) {

            this.id = id;
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