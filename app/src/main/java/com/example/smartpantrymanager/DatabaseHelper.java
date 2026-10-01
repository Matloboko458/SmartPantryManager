package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 2;

    // ---------------------------------------------------------
    // INGREDIENTS TABLE
    // ---------------------------------------------------------

    public static final String TABLE_INGREDIENTS = "ingredients";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_EXPIRY_DATE = "expiry_date";

    // ---------------------------------------------------------
    // RECIPES TABLE
    // ---------------------------------------------------------

    public static final String TABLE_RECIPES = "recipes";

    public static final String COLUMN_RECIPE_ID = "id";
    public static final String COLUMN_RECIPE_NAME = "name";
    public static final String COLUMN_RECIPE_DESCRIPTION = "description";
    public static final String COLUMN_COOKING_TIME = "cooking_time";
    public static final String COLUMN_DIFFICULTY = "difficulty";
    public static final String COLUMN_INSTRUCTIONS = "instructions";

    // ---------------------------------------------------------
    // RECIPE INGREDIENTS TABLE
    // ---------------------------------------------------------

    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";

    public static final String COLUMN_RECIPE_INGREDIENT_ID = "id";
    public static final String COLUMN_RECIPE_ID_FK = "recipe_id";
    public static final String COLUMN_INGREDIENT_NAME =
            "ingredient_name";
    public static final String COLUMN_REQUIRED_QUANTITY =
            "required_quantity";
    public static final String COLUMN_REQUIRED_UNIT =
            "required_unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ---------------------------------------------------------
    // FOREIGN KEY SETTINGS
    // ---------------------------------------------------------

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // ---------------------------------------------------------
    // DATABASE CREATION
    // ---------------------------------------------------------

    @Override
    public void onCreate(SQLiteDatabase db) {

        createIngredientsTable(db);

        createRecipesTable(db);

        createRecipeIngredientsTable(db);

        seedInitialIngredients(db);

        seedRecipes(db);
    }

    // ---------------------------------------------------------
    // INGREDIENTS TABLE CREATION
    // ---------------------------------------------------------

    private void createIngredientsTable(SQLiteDatabase db) {

        String createTable =
                "CREATE TABLE " + TABLE_INGREDIENTS + " (" +
                        COLUMN_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COLUMN_NAME +
                        " TEXT NOT NULL, " +

                        COLUMN_QUANTITY +
                        " TEXT NOT NULL, " +

                        COLUMN_UNIT +
                        " TEXT NOT NULL, " +

                        COLUMN_EXPIRY_DATE +
                        " TEXT NOT NULL" +

                        ")";

        db.execSQL(createTable);
    }

    // ---------------------------------------------------------
    // RECIPES TABLE CREATION
    // ---------------------------------------------------------

    private void createRecipesTable(SQLiteDatabase db) {

        String createTable =
                "CREATE TABLE " + TABLE_RECIPES + " (" +

                        COLUMN_RECIPE_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COLUMN_RECIPE_NAME +
                        " TEXT NOT NULL, " +

                        COLUMN_RECIPE_DESCRIPTION +
                        " TEXT NOT NULL, " +

                        COLUMN_COOKING_TIME +
                        " TEXT NOT NULL, " +

                        COLUMN_DIFFICULTY +
                        " TEXT NOT NULL, " +

                        COLUMN_INSTRUCTIONS +
                        " TEXT NOT NULL" +

                        ")";

        db.execSQL(createTable);
    }

    // ---------------------------------------------------------
    // RECIPE INGREDIENTS TABLE CREATION
    // ---------------------------------------------------------

    private void createRecipeIngredientsTable(SQLiteDatabase db) {

        String createTable =
                "CREATE TABLE " +
                        TABLE_RECIPE_INGREDIENTS + " (" +

                        COLUMN_RECIPE_INGREDIENT_ID +
                        " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        COLUMN_RECIPE_ID_FK +
                        " INTEGER NOT NULL, " +

                        COLUMN_INGREDIENT_NAME +
                        " TEXT NOT NULL, " +

                        COLUMN_REQUIRED_QUANTITY +
                        " REAL NOT NULL, " +

                        COLUMN_REQUIRED_UNIT +
                        " TEXT NOT NULL, " +

                        "FOREIGN KEY (" +
                        COLUMN_RECIPE_ID_FK +
                        ") REFERENCES " +
                        TABLE_RECIPES +
                        "(" +
                        COLUMN_RECIPE_ID +
                        ") ON DELETE CASCADE" +

                        ")";

        db.execSQL(createTable);
    }

    // ---------------------------------------------------------
    // DATABASE UPGRADE
    // ---------------------------------------------------------

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {

        if (oldVersion < 2) {

            createRecipesTable(db);

            createRecipeIngredientsTable(db);

            seedRecipes(db);
        }
    }

    // =========================================================
    // INGREDIENT METHODS
    // =========================================================

    // ---------------------------------------------------------
    // INITIAL INGREDIENTS
    // ---------------------------------------------------------

    public void seedInitialIngredientsIfEmpty() {

        if (getIngredientCount() == 0) {

            SQLiteDatabase db = getWritableDatabase();

            seedInitialIngredients(db);
        }
    }

    private void seedInitialIngredients(SQLiteDatabase db) {

        ContentValues v1 = new ContentValues();

        v1.put(COLUMN_NAME, "Eggs");
        v1.put(COLUMN_QUANTITY, "6");
        v1.put(COLUMN_UNIT, "piece");
        v1.put(COLUMN_EXPIRY_DATE, "25/12/2026");

        db.insert(
                TABLE_INGREDIENTS,
                null,
                v1
        );

        ContentValues v2 = new ContentValues();

        v2.put(COLUMN_NAME, "Bread");
        v2.put(COLUMN_QUANTITY, "8");
        v2.put(COLUMN_UNIT, "slice");
        v2.put(COLUMN_EXPIRY_DATE, "20/12/2026");

        db.insert(
                TABLE_INGREDIENTS,
                null,
                v2
        );

        ContentValues v3 = new ContentValues();

        v3.put(COLUMN_NAME, "Cheese");
        v3.put(COLUMN_QUANTITY, "200");
        v3.put(COLUMN_UNIT, "g");
        v3.put(COLUMN_EXPIRY_DATE, "30/12/2026");

        db.insert(
                TABLE_INGREDIENTS,
                null,
                v3
        );

        ContentValues v4 = new ContentValues();

        v4.put(COLUMN_NAME, "Milk");
        v4.put(COLUMN_QUANTITY, "1000");
        v4.put(COLUMN_UNIT, "ml");
        v4.put(COLUMN_EXPIRY_DATE, "18/12/2026");

        db.insert(
                TABLE_INGREDIENTS,
                null,
                v4
        );

        ContentValues v5 = new ContentValues();

        v5.put(COLUMN_NAME, "Oil");
        v5.put(COLUMN_QUANTITY, "250");
        v5.put(COLUMN_UNIT, "ml");
        v5.put(COLUMN_EXPIRY_DATE, "15/01/2027");

        db.insert(
                TABLE_INGREDIENTS,
                null,
                v5
        );

        ContentValues v6 = new ContentValues();

        v6.put(COLUMN_NAME, "Salt");
        v6.put(COLUMN_QUANTITY, "100");
        v6.put(COLUMN_UNIT, "pinch");
        v6.put(COLUMN_EXPIRY_DATE, "01/01/2028");

        db.insert(
                TABLE_INGREDIENTS,
                null,
                v6
        );
    }

    // ---------------------------------------------------------
    // ADD INGREDIENT
    // ---------------------------------------------------------

    public long addIngredient(
            String name,
            String quantity,
            String unit,
            String expiryDate
    ) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(
                COLUMN_NAME,
                name
        );

        values.put(
                COLUMN_QUANTITY,
                quantity
        );

        values.put(
                COLUMN_UNIT,
                unit
        );

        values.put(
                COLUMN_EXPIRY_DATE,
                expiryDate
        );

        return db.insert(
                TABLE_INGREDIENTS,
                null,
                values
        );
    }

    // ---------------------------------------------------------
    // GET ALL INGREDIENTS
    // ---------------------------------------------------------

    public Cursor getAllIngredients() {

        SQLiteDatabase db =
                getReadableDatabase();

        return db.query(
                TABLE_INGREDIENTS,
                null,
                null,
                null,
                null,
                null,
                COLUMN_ID + " DESC"
        );
    }

    // ---------------------------------------------------------
    // INGREDIENT COUNT
    // ---------------------------------------------------------

    public int getIngredientCount() {

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT COUNT(*) FROM " +
                                TABLE_INGREDIENTS,
                        null
                );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    // ---------------------------------------------------------
    // UPDATE INGREDIENT
    // ---------------------------------------------------------

    public int updateIngredient(
            long id,
            String name,
            String quantity,
            String unit,
            String expiryDate
    ) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                COLUMN_NAME,
                name
        );

        values.put(
                COLUMN_QUANTITY,
                quantity
        );

        values.put(
                COLUMN_UNIT,
                unit
        );

        values.put(
                COLUMN_EXPIRY_DATE,
                expiryDate
        );

        return db.update(
                TABLE_INGREDIENTS,
                values,
                COLUMN_ID + " = ?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }

    // ---------------------------------------------------------
    // DELETE INGREDIENT
    // ---------------------------------------------------------

    public int deleteIngredient(long id) {

        SQLiteDatabase db =
                getWritableDatabase();

        return db.delete(
                TABLE_INGREDIENTS,
                COLUMN_ID + " = ?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }

    // =========================================================
    // RECIPE METHODS
    // =========================================================

    // ---------------------------------------------------------
    // RECIPE COUNT
    // ---------------------------------------------------------

    public int getRecipeCount() {

        SQLiteDatabase db =
                getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT COUNT(*) FROM " +
                                TABLE_RECIPES,
                        null
                );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    // ---------------------------------------------------------
    // SEED RECIPES IF EMPTY
    // ---------------------------------------------------------

    public void seedRecipesIfEmpty() {

        if (getRecipeCount() == 0) {

            SQLiteDatabase db =
                    getWritableDatabase();

            seedRecipes(db);
        }
    }

    // ---------------------------------------------------------
    // ADD RECIPE
    // ---------------------------------------------------------

    public long addRecipe(
            String name,
            String description,
            String cookingTime,
            String difficulty,
            String instructions
    ) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                COLUMN_RECIPE_NAME,
                name
        );

        values.put(
                COLUMN_RECIPE_DESCRIPTION,
                description
        );

        values.put(
                COLUMN_COOKING_TIME,
                cookingTime
        );

        values.put(
                COLUMN_DIFFICULTY,
                difficulty
        );

        values.put(
                COLUMN_INSTRUCTIONS,
                instructions
        );

        return db.insert(
                TABLE_RECIPES,
                null,
                values
        );
    }

    // ---------------------------------------------------------
    // ADD RECIPE INGREDIENT
    // ---------------------------------------------------------

    public long addRecipeIngredient(
            long recipeId,
            String ingredientName,
            double requiredQuantity,
            String requiredUnit
    ) {

        SQLiteDatabase db =
                getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                COLUMN_RECIPE_ID_FK,
                recipeId
        );

        values.put(
                COLUMN_INGREDIENT_NAME,
                ingredientName
        );

        values.put(
                COLUMN_REQUIRED_QUANTITY,
                requiredQuantity
        );

        values.put(
                COLUMN_REQUIRED_UNIT,
                requiredUnit
        );

        return db.insert(
                TABLE_RECIPE_INGREDIENTS,
                null,
                values
        );
    }

    // ---------------------------------------------------------
    // GET ALL RECIPES
    // ---------------------------------------------------------

    public Cursor getAllRecipes() {

        SQLiteDatabase db =
                getReadableDatabase();

        return db.query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                COLUMN_RECIPE_ID + " ASC"
        );
    }

    // ---------------------------------------------------------
    // GET SINGLE RECIPE
    // ---------------------------------------------------------

    public Cursor getRecipeById(long recipeId) {

        SQLiteDatabase db =
                getReadableDatabase();

        return db.query(
                TABLE_RECIPES,
                null,
                COLUMN_RECIPE_ID + " = ?",
                new String[]{
                        String.valueOf(recipeId)
                },
                null,
                null,
                null
        );
    }

    // ---------------------------------------------------------
    // GET RECIPE INGREDIENTS
    // ---------------------------------------------------------

    public Cursor getRecipeIngredients(long recipeId) {

        SQLiteDatabase db =
                getReadableDatabase();

        return db.query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                COLUMN_RECIPE_ID_FK + " = ?",
                new String[]{
                        String.valueOf(recipeId)
                },
                null,
                null,
                COLUMN_RECIPE_INGREDIENT_ID + " ASC"
        );
    }

    // =========================================================
    // SEED 20 RECIPES
    // =========================================================

    private void seedRecipes(SQLiteDatabase db) {

        addRecipeToDatabase(
                db,
                "Egg & Cheese Toast",
                "A quick breakfast made with eggs, toasted bread and cheese.",
                "10 minutes",
                "Easy",
                "1. Beat the eggs in a bowl.\n" +
                        "2. Cook the eggs in a pan.\n" +
                        "3. Toast the bread.\n" +
                        "4. Add the cheese and cooked eggs.\n" +
                        "5. Season with salt and pepper.",
                new String[]{"Eggs", "Bread", "Cheese"},
                new double[]{2, 2, 50},
                new String[]{"piece", "slice", "g"}
        );

        addRecipeToDatabase(
                db,
                "Creamy Pasta",
                "A simple creamy pasta recipe using milk and cheese.",
                "20 minutes",
                "Easy",
                "1. Cook the pasta.\n" +
                        "2. Drain the pasta.\n" +
                        "3. Heat the milk.\n" +
                        "4. Add the cheese and stir.\n" +
                        "5. Add the pasta and mix well.",
                new String[]{"Pasta", "Milk", "Cheese"},
                new double[]{200, 250, 50},
                new String[]{"g", "ml", "g"}
        );

        addRecipeToDatabase(
                db,
                "Cheese Omelette",
                "An omelette made with eggs, cheese and salt.",
                "10 minutes",
                "Easy",
                "1. Beat the eggs.\n" +
                        "2. Heat a pan.\n" +
                        "3. Cook the eggs.\n" +
                        "4. Add the cheese.\n" +
                        "5. Fold and serve.",
                new String[]{"Eggs", "Cheese", "Salt"},
                new double[]{2, 50, 1},
                new String[]{"piece", "g", "pinch"}
        );

        addRecipeToDatabase(
                db,
                "French Toast",
                "French toast made with bread, eggs and milk.",
                "15 minutes",
                "Easy",
                "1. Beat the eggs and milk together.\n" +
                        "2. Dip the bread into the mixture.\n" +
                        "3. Heat a pan.\n" +
                        "4. Cook both sides until golden.\n" +
                        "5. Serve.",
                new String[]{"Bread", "Eggs", "Milk", "Sugar"},
                new double[]{2, 2, 100, 1},
                new String[]{"slice", "piece", "ml", "tbsp"}
        );

        addRecipeToDatabase(
                db,
                "Grilled Cheese Sandwich",
                "Toasted bread filled with melted cheese.",
                "10 minutes",
                "Easy",
                "1. Butter the bread.\n" +
                        "2. Add the cheese.\n" +
                        "3. Heat a pan.\n" +
                        "4. Toast both sides.\n" +
                        "5. Serve when the cheese melts.",
                new String[]{"Bread", "Cheese", "Butter"},
                new double[]{2, 50, 10},
                new String[]{"slice", "g", "g"}
        );

        addRecipeToDatabase(
                db,
                "Peanut Butter Sandwich",
                "A simple sandwich made with bread and peanut butter.",
                "5 minutes",
                "Easy",
                "1. Place two slices of bread on a plate.\n" +
                        "2. Spread peanut butter over the bread.\n" +
                        "3. Join the slices.\n" +
                        "4. Serve.",
                new String[]{"Bread", "Peanut Butter"},
                new double[]{2, 30},
                new String[]{"slice", "g"}
        );

        addRecipeToDatabase(
                db,
                "Banana Smoothie",
                "A simple smoothie made with banana and milk.",
                "5 minutes",
                "Easy",
                "1. Peel the banana.\n" +
                        "2. Add banana and milk to a blender.\n" +
                        "3. Add sugar.\n" +
                        "4. Blend until smooth.\n" +
                        "5. Serve.",
                new String[]{"Banana", "Milk", "Sugar"},
                new double[]{1, 250, 1},
                new String[]{"piece", "ml", "tbsp"}
        );

        addRecipeToDatabase(
                db,
                "Cereal with Milk",
                "A quick breakfast using cereal and milk.",
                "5 minutes",
                "Easy",
                "1. Pour cereal into a bowl.\n" +
                        "2. Add milk.\n" +
                        "3. Serve immediately.",
                new String[]{"Cereal", "Milk"},
                new double[]{1, 250},
                new String[]{"cup", "ml"}
        );

        addRecipeToDatabase(
                db,
                "Cheese Toast",
                "Toasted bread with cheese and butter.",
                "10 minutes",
                "Easy",
                "1. Butter the bread.\n" +
                        "2. Add cheese.\n" +
                        "3. Toast until the cheese melts.\n" +
                        "4. Serve.",
                new String[]{"Bread", "Cheese", "Butter"},
                new double[]{2, 50, 10},
                new String[]{"slice", "g", "g"}
        );

        addRecipeToDatabase(
                db,
                "Egg Sandwich",
                "A simple sandwich made with eggs and mayonnaise.",
                "10 minutes",
                "Easy",
                "1. Cook the eggs.\n" +
                        "2. Toast the bread.\n" +
                        "3. Add mayonnaise.\n" +
                        "4. Add the eggs.\n" +
                        "5. Serve.",
                new String[]{"Bread", "Eggs", "Mayonnaise"},
                new double[]{2, 2, 15},
                new String[]{"slice", "piece", "g"}
        );

        addRecipeToDatabase(
                db,
                "Pancakes",
                "Simple pancakes made using flour, milk and eggs.",
                "20 minutes",
                "Medium",
                "1. Mix flour, milk, eggs and sugar.\n" +
                        "2. Heat a pan.\n" +
                        "3. Pour the batter into the pan.\n" +
                        "4. Cook both sides.\n" +
                        "5. Serve.",
                new String[]{"Flour", "Milk", "Eggs", "Sugar"},
                new double[]{100, 200, 2, 1},
                new String[]{"g", "ml", "piece", "tbsp"}
        );

        addRecipeToDatabase(
                db,
                "Pasta with Tomato Sauce",
                "Pasta served with tomato, onion and seasoning.",
                "25 minutes",
                "Medium",
                "1. Cook the pasta.\n" +
                        "2. Chop the tomatoes and onion.\n" +
                        "3. Cook the vegetables.\n" +
                        "4. Add seasoning.\n" +
                        "5. Mix with the pasta.",
                new String[]{"Pasta", "Tomato", "Onion", "Salt"},
                new double[]{200, 2, 1, 1},
                new String[]{"g", "piece", "piece", "pinch"}
        );

        addRecipeToDatabase(
                db,
                "Tomato Sandwich",
                "A fresh sandwich made with bread, tomato and cheese.",
                "5 minutes",
                "Easy",
                "1. Slice the tomato.\n" +
                        "2. Place tomato on the bread.\n" +
                        "3. Add cheese.\n" +
                        "4. Close the sandwich.\n" +
                        "5. Serve.",
                new String[]{"Bread", "Tomato", "Cheese"},
                new double[]{2, 1, 30},
                new String[]{"slice", "piece", "g"}
        );

        addRecipeToDatabase(
                db,
                "Mashed Potatoes",
                "Mashed potatoes prepared with milk, butter and salt.",
                "25 minutes",
                "Medium",
                "1. Boil the potatoes.\n" +
                        "2. Drain the potatoes.\n" +
                        "3. Mash the potatoes.\n" +
                        "4. Add milk and butter.\n" +
                        "5. Season with salt.",
                new String[]{"Potatoes", "Milk", "Butter", "Salt"},
                new double[]{3, 100, 20, 1},
                new String[]{"piece", "ml", "g", "pinch"}
        );

        addRecipeToDatabase(
                db,
                "Potato Omelette",
                "An omelette prepared with potatoes, eggs and onion.",
                "20 minutes",
                "Medium",
                "1. Cook the potatoes.\n" +
                        "2. Beat the eggs.\n" +
                        "3. Add onion.\n" +
                        "4. Combine the ingredients.\n" +
                        "5. Cook until set.",
                new String[]{"Potatoes", "Eggs", "Onion", "Salt"},
                new double[]{2, 2, 1, 1},
                new String[]{"piece", "piece", "piece", "pinch"}
        );

        addRecipeToDatabase(
                db,
                "Tuna Sandwich",
                "A simple tuna sandwich made with bread and mayonnaise.",
                "10 minutes",
                "Easy",
                "1. Drain the tuna.\n" +
                        "2. Mix tuna with mayonnaise.\n" +
                        "3. Spread on bread.\n" +
                        "4. Close the sandwich.\n" +
                        "5. Serve.",
                new String[]{"Bread", "Tuna", "Mayonnaise"},
                new double[]{2, 1, 15},
                new String[]{"slice", "can", "g"}
        );

        addRecipeToDatabase(
                db,
                "Chicken Sandwich",
                "A chicken sandwich with bread, mayonnaise and lettuce.",
                "15 minutes",
                "Medium",
                "1. Cook the chicken.\n" +
                        "2. Toast the bread.\n" +
                        "3. Add chicken and lettuce.\n" +
                        "4. Add mayonnaise.\n" +
                        "5. Serve.",
                new String[]{"Bread", "Chicken", "Mayonnaise", "Lettuce"},
                new double[]{2, 100, 15, 2},
                new String[]{"slice", "g", "g", "leaf"}
        );

        addRecipeToDatabase(
                db,
                "Rice and Eggs",
                "Rice served with cooked eggs and seasoning.",
                "20 minutes",
                "Easy",
                "1. Cook the rice.\n" +
                        "2. Cook the eggs.\n" +
                        "3. Combine the rice and eggs.\n" +
                        "4. Add salt.\n" +
                        "5. Serve.",
                new String[]{"Rice", "Eggs", "Salt"},
                new double[]{150, 2, 1},
                new String[]{"g", "piece", "pinch"}
        );

        addRecipeToDatabase(
                db,
                "Tomato Pasta",
                "Pasta made with tomato, garlic and salt.",
                "25 minutes",
                "Medium",
                "1. Cook the pasta.\n" +
                        "2. Cook tomato and garlic.\n" +
                        "3. Add salt.\n" +
                        "4. Mix with pasta.\n" +
                        "5. Serve.",
                new String[]{"Pasta", "Tomato", "Garlic", "Salt"},
                new double[]{200, 2, 2, 1},
                new String[]{"g", "piece", "clove", "pinch"}
        );

        addRecipeToDatabase(
                db,
                "Fruit Salad",
                "A simple fruit salad made with apple, banana and orange.",
                "10 minutes",
                "Easy",
                "1. Wash the fruit.\n" +
                        "2. Peel the banana.\n" +
                        "3. Cut the fruit.\n" +
                        "4. Mix together.\n" +
                        "5. Serve.",
                new String[]{"Apple", "Banana", "Orange"},
                new double[]{1, 1, 1},
                new String[]{"piece", "piece", "piece"}
        );

        addRecipeToDatabase(
                db,
                "Milkshake",
                "A creamy milkshake made with milk, banana and ice cream.",
                "5 minutes",
                "Easy",
                "1. Add milk to a blender.\n" +
                        "2. Add banana.\n" +
                        "3. Add sugar and ice cream.\n" +
                        "4. Blend until smooth.\n" +
                        "5. Serve.",
                new String[]{"Milk", "Banana", "Sugar", "Ice Cream"},
                new double[]{250, 1, 1, 2},
                new String[]{"ml", "piece", "tbsp", "scoop"}
        );
    }

    // ---------------------------------------------------------
    // ADD COMPLETE RECIPE
    // ---------------------------------------------------------

    private void addRecipeToDatabase(
            SQLiteDatabase db,
            String name,
            String description,
            String cookingTime,
            String difficulty,
            String instructions,
            String[] ingredientNames,
            double[] quantities,
            String[] units
    ) {

        ContentValues recipeValues =
                new ContentValues();

        recipeValues.put(
                COLUMN_RECIPE_NAME,
                name
        );

        recipeValues.put(
                COLUMN_RECIPE_DESCRIPTION,
                description
        );

        recipeValues.put(
                COLUMN_COOKING_TIME,
                cookingTime
        );

        recipeValues.put(
                COLUMN_DIFFICULTY,
                difficulty
        );

        recipeValues.put(
                COLUMN_INSTRUCTIONS,
                instructions
        );

        long recipeId =
                db.insert(
                        TABLE_RECIPES,
                        null,
                        recipeValues
                );

        if (recipeId == -1) {
            return;
        }

        for (int i = 0;
             i < ingredientNames.length;
             i++) {

            ContentValues ingredientValues =
                    new ContentValues();

            ingredientValues.put(
                    COLUMN_RECIPE_ID_FK,
                    recipeId
            );

            ingredientValues.put(
                    COLUMN_INGREDIENT_NAME,
                    ingredientNames[i]
            );

            ingredientValues.put(
                    COLUMN_REQUIRED_QUANTITY,
                    quantities[i]
            );

            ingredientValues.put(
                    COLUMN_REQUIRED_UNIT,
                    units[i]
            );

            db.insert(
                    TABLE_RECIPE_INGREDIENTS,
                    null,
                    ingredientValues
            );
        }
    }
}