package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 2;

    // pantry_items
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "item_name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_EXPIRATION = "expiration_date";

    // recipes
    public static final String TABLE_RECIPES = "recipes";
    public static final String COLUMN_RECIPE_ID = "id";
    public static final String COLUMN_RECIPE_NAME = "recipe_name";
    public static final String COLUMN_RECIPE_STEPS = "steps";

    // recipe_ingredients
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COLUMN_RI_ID = "id";
    public static final String COLUMN_RI_RECIPE_ID = "recipe_id";
    public static final String COLUMN_RI_NAME = "ingredient_name";
    public static final String COLUMN_RI_QUANTITY = "quantity";
    public static final String COLUMN_RI_UNIT = "unit";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_QUANTITY + " REAL NOT NULL DEFAULT 1, "
                + COLUMN_UNIT + " TEXT NOT NULL DEFAULT 'unit', "
                + COLUMN_CATEGORY + " TEXT NOT NULL, "
                + COLUMN_EXPIRATION + " TEXT);");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " ("
                + COLUMN_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_RECIPE_NAME + " TEXT NOT NULL, "
                + COLUMN_RECIPE_STEPS + " TEXT NOT NULL);");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
                + COLUMN_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_RI_RECIPE_ID + " INTEGER NOT NULL, "
                + COLUMN_RI_NAME + " TEXT NOT NULL, "
                + COLUMN_RI_QUANTITY + " REAL NOT NULL DEFAULT 1, "
                + COLUMN_RI_UNIT + " TEXT NOT NULL DEFAULT 'unit', "
                + "FOREIGN KEY (" + COLUMN_RI_RECIPE_ID + ") REFERENCES "
                + TABLE_RECIPES + "(" + COLUMN_RECIPE_ID + ") ON DELETE CASCADE);");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // ---------------- PANTRY CRUD ----------------

    public long addPantryItem(String name, double quantity, String unit,
                              String category, String expirationDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name.trim());
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit.trim().toLowerCase());
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_EXPIRATION, expirationDate);
        long id = db.insert(TABLE_PANTRY, null, values);
        db.close();
        return id;
    }

    public List<PantryItem> getAllPantryItemsAsList() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_PANTRY
                        + " ORDER BY " + COLUMN_EXPIRATION + " ASC", null);
        if (cursor.moveToFirst()) {
            do { items.add(cursorToPantryItem(cursor)); } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public PantryItem getPantryItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_PANTRY
                        + " WHERE " + COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        PantryItem item = null;
        if (cursor.moveToFirst()) { item = cursorToPantryItem(cursor); }
        cursor.close();
        return item;
    }

    public int updatePantryItem(int id, String name, double quantity, String unit,
                                String category, String expirationDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name.trim());
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit.trim().toLowerCase());
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_EXPIRATION, expirationDate);
        int rows = db.update(TABLE_PANTRY, values,
                COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public int deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_PANTRY,
                COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    private PantryItem cursorToPantryItem(Cursor cursor) {
        return new PantryItem(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIT)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPIRATION)));
    }

    // ---------------- RECIPES ----------------

    public List<Recipe> getAllRecipes() {
        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_RECIPES
                        + " ORDER BY " + COLUMN_RECIPE_NAME + " ASC", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Recipe(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_STEPS))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Recipe getRecipeById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_RECIPES
                        + " WHERE " + COLUMN_RECIPE_ID + " = ?",
                new String[]{String.valueOf(id)});
        Recipe recipe = null;
        if (cursor.moveToFirst()) {
            recipe = new Recipe(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECIPE_STEPS)));
        }
        cursor.close();
        return recipe;
    }

    public List<RecipeIngredient> getIngredientsForRecipe(int recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_RECIPE_INGREDIENTS
                        + " WHERE " + COLUMN_RI_RECIPE_ID + " = ?",
                new String[]{String.valueOf(recipeId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(new RecipeIngredient(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RI_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RI_RECIPE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RI_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_RI_QUANTITY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RI_UNIT))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // ---------------- SEEDING ----------------

    private void seedRecipes(SQLiteDatabase db) {

        addRecipe(db, "Tomato Pasta",
                "1. Boil pasta until tender.\n2. Heat oil, add chopped tomatoes.\n3. Stir in cooked pasta.\n4. Season and serve.",
                new String[]{"pasta:200:g", "tomato:2:units", "oil:1:tbsp"});

        addRecipe(db, "Cheese Omelette",
                "1. Beat eggs.\n2. Pour into a hot pan.\n3. Add grated cheese.\n4. Fold and serve.",
                new String[]{"egg:2:units", "cheese:50:g", "oil:1:tbsp"});

        addRecipe(db, "Garlic Rice",
                "1. Cook rice.\n2. Saute minced garlic in oil.\n3. Mix garlic with rice.\n4. Serve.",
                new String[]{"rice:1:cups", "garlic:2:cloves", "oil:1:tbsp"});

        addRecipe(db, "Onion Soup",
                "1. Slice onions and saute.\n2. Add water and simmer 20 min.\n3. Season and serve.",
                new String[]{"onion:2:units", "water:2:cups", "salt:1:tsp"});

        addRecipe(db, "Tomato Sandwich",
                "1. Slice tomato.\n2. Butter bread.\n3. Add tomato and close.\n4. Serve.",
                new String[]{"bread:2:slices", "tomato:1:units", "butter:1:tbsp"});

        addRecipe(db, "Egg Fried Rice",
                "1. Cook rice.\n2. Scramble eggs.\n3. Fry rice with eggs and soy sauce.",
                new String[]{"rice:1:cups", "egg:2:units", "oil:1:tbsp"});

        addRecipe(db, "Cheese Toast",
                "1. Toast bread.\n2. Add cheese.\n3. Grill until melted.",
                new String[]{"bread:2:slices", "cheese:50:g"});

        addRecipe(db, "Potato Mash",
                "1. Boil potatoes.\n2. Mash with butter and milk.\n3. Season.",
                new String[]{"potato:4:units", "butter:2:tbsp", "milk:100:ml"});

        addRecipe(db, "Chicken Stir Fry",
                "1. Slice chicken.\n2. Fry with vegetables.\n3. Add soy sauce.\n4. Serve hot.",
                new String[]{"chicken:200:g", "onion:1:units", "oil:2:tbsp", "soy sauce:1:tbsp"});

        addRecipe(db, "Pancakes",
                "1. Mix flour, milk, egg.\n2. Pour onto hot pan.\n3. Flip and serve.",
                new String[]{"flour:1:cups", "milk:200:ml", "egg:1:units"});

        addRecipe(db, "Milk Rice",
                "1. Cook rice in milk.\n2. Add sugar.\n3. Simmer until creamy.",
                new String[]{"rice:1:cups", "milk:2:cups", "sugar:2:tbsp"});

        addRecipe(db, "Garlic Bread",
                "1. Slice bread.\n2. Mix butter and garlic.\n3. Spread and grill.",
                new String[]{"bread:4:slices", "butter:2:tbsp", "garlic:2:cloves"});

        addRecipe(db, "Veggie Salad",
                "1. Chop lettuce and tomato.\n2. Add oil and salt.\n3. Toss and serve.",
                new String[]{"lettuce:1:cups", "tomato:1:units", "oil:1:tbsp", "salt:1:tsp"});

        addRecipe(db, "Pasta with Cheese",
                "1. Boil pasta.\n2. Melt cheese into pasta.\n3. Add butter.\n4. Serve.",
                new String[]{"pasta:200:g", "cheese:80:g", "butter:1:tbsp"});

        addRecipe(db, "Scrambled Eggs",
                "1. Beat eggs.\n2. Cook in butter, stirring.\n3. Season and serve.",
                new String[]{"egg:3:units", "butter:1:tbsp", "salt:1:tsp"});

        addRecipe(db, "Tomato Rice",
                "1. Cook rice.\n2. Add chopped tomato and salt.\n3. Simmer 5 min.",
                new String[]{"rice:1:cups", "tomato:2:units", "salt:1:tsp"});

        addRecipe(db, "Chicken Soup",
                "1. Boil chicken in water.\n2. Add chopped onion and salt.\n3. Simmer 30 min.",
                new String[]{"chicken:200:g", "water:4:cups", "onion:1:units", "salt:1:tsp"});

        addRecipe(db, "French Toast",
                "1. Beat egg with milk.\n2. Dip bread.\n3. Fry in butter.\n4. Serve.",
                new String[]{"bread:2:slices", "egg:1:units", "milk:100:ml", "butter:1:tbsp"});
    }

    private void addRecipe(SQLiteDatabase db, String name, String steps, String[] ingredients) {
        ContentValues rv = new ContentValues();
        rv.put(COLUMN_RECIPE_NAME, name);
        rv.put(COLUMN_RECIPE_STEPS, steps);
        long recipeId = db.insert(TABLE_RECIPES, null, rv);

        for (String entry : ingredients) {
            String[] parts = entry.split(":");
            if (parts.length != 3) continue;
            ContentValues iv = new ContentValues();
            iv.put(COLUMN_RI_RECIPE_ID, recipeId);
            iv.put(COLUMN_RI_NAME, parts[0].trim().toLowerCase());
            iv.put(COLUMN_RI_QUANTITY, Double.parseDouble(parts[1]));
            iv.put(COLUMN_RI_UNIT, parts[2].trim().toLowerCase());
            db.insert(TABLE_RECIPE_INGREDIENTS, null, iv);
        }
    }
}