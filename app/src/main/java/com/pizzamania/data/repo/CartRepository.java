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

    //Add to cart: if exists → update qty, else insert new
    public void addToCart(int userId, int itemId, int qty, int unitPriceCents, String size) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("CartItem",
                new String[]{"qty"},
                "user_id=? AND item_id=?",
                new String[]{String.valueOf(userId), String.valueOf(itemId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            // Already exists → update qty
            int currentQty = cursor.getInt(0);
            int newQty = currentQty + qty;

            ContentValues cv = new ContentValues();
            cv.put("qty", newQty);
            cv.put("unit_price_cents", unitPriceCents);
            cv.put("size", size);

            db.update("CartItem", cv, "user_id=? AND item_id=?",
                    new String[]{String.valueOf(userId), String.valueOf(itemId)});
        } else {
            // New item → insert
            ContentValues cv = new ContentValues();
            cv.put("user_id", userId);
            cv.put("item_id", itemId);
            cv.put("qty", qty);
            cv.put("unit_price_cents", unitPriceCents);
            cv.put("size", size);

            db.insert("CartItem", null, cv);
        }

        cursor.close();
        db.close();
    }

    // Get all cart items for a user
    public Cursor getCartItems(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query("CartItem", null, "user_id=?",
                new String[]{String.valueOf(userId)}, null, null, null);
    }

    // Clear cart for a user
    public void clearCart(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("CartItem", "user_id=?", new String[]{String.valueOf(userId)});
        db.close();
    }

    public int getCartItemCount(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(qty) FROM CartItem WHERE user_id=?",
                new String[]{String.valueOf(userId)}
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0); // sum of qty
        }

        cursor.close();
        db.close();
        return count;
    }
}
