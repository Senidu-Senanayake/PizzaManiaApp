package com.pizzamania.data.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pizzamania.data.db.AppDbHelper;

public class CartRepository {
    private final AppDbHelper dbHelper;

    public CartRepository(Context context) {
        dbHelper = new AppDbHelper(context);
    }

    // Updated: include unit_price_cents and size
    public void addToCart(int userId, int itemId, int qty, int unitPriceCents, String size) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("item_id", itemId);
        cv.put("qty", qty);
        cv.put("unit_price_cents", unitPriceCents);
        cv.put("size", size);

        // This will REPLACE if same (userId + itemId) exists
        db.insertWithOnConflict("CartItem", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public Cursor getCartItems(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query("CartItem", null, "user_id=?",
                new String[]{String.valueOf(userId)}, null, null, null);
    }

    public void clearCart(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("CartItem", "user_id=?", new String[]{String.valueOf(userId)});
    }
}
