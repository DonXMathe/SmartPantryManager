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
    private static final int DATABASE_VERSION = 1;

    // pantry_items columns
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "item_name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_EXPIRATION = "expiration_date";

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
        String CREATE_PANTRY_TABLE = "CREATE TABLE " + TABLE_PANTRY + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_QUANTITY + " REAL NOT NULL DEFAULT 1, "
                + COLUMN_UNIT + " TEXT NOT NULL DEFAULT 'unit', "
                + COLUMN_CATEGORY + " TEXT NOT NULL, "
                + COLUMN_EXPIRATION + " TEXT);";
        db.execSQL(CREATE_PANTRY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple destructive migration. Acceptable for this assignment's scope.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // ---------- CREATE ----------

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
        return id; // -1 if the insert failed
    }

    // ---------- READ ----------

    public Cursor getAllPantryItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_PANTRY
                        + " ORDER BY " + COLUMN_EXPIRATION + " ASC",
                null);
    }

    public List<PantryItem> getAllPantryItemsAsList() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_PANTRY
                        + " ORDER BY " + COLUMN_EXPIRATION + " ASC",
                null);

        if (cursor.moveToFirst()) {
            do {
                items.add(cursorToPantryItem(cursor));
            } while (cursor.moveToNext());
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
        if (cursor.moveToFirst()) {
            item = cursorToPantryItem(cursor);
        }
        cursor.close();
        return item;
    }

    // ---------- UPDATE ----------

    public int updatePantryItem(int id, String name, double quantity, String unit,
                                String category, String expirationDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name.trim());
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit.trim().toLowerCase());
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_EXPIRATION, expirationDate);

        int rows = db.update(
                TABLE_PANTRY, values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    // ---------- DELETE ----------

    public int deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(
                TABLE_PANTRY,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    // ---------- MAPPING ----------

    private PantryItem cursorToPantryItem(Cursor cursor) {
        return new PantryItem(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIT)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPIRATION))
        );
    }
}
