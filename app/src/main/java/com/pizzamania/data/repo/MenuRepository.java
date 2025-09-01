package com.pizzamania.data.repo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pizzamania.data.db.AppDbHelper;
import com.pizzamania.data.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuRepository {
    private final AppDbHelper dbHelper;

    public MenuRepository(Context context) {
        dbHelper = new AppDbHelper(context);
    }

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query("MenuItem",
                null,   // all columns
                null, null, null, null, null);

        while (c.moveToNext()) {
            int itemId = c.getInt(c.getColumnIndexOrThrow("item_id"));
            String name = c.getString(c.getColumnIndexOrThrow("name"));
            String desc = c.getString(c.getColumnIndexOrThrow("description"));
            int price = c.getInt(c.getColumnIndexOrThrow("price_cents"));
            String imageUri = c.getString(c.getColumnIndexOrThrow("image_uri"));
            String category = c.getString(c.getColumnIndexOrThrow("category"));
            boolean available = c.getInt(c.getColumnIndexOrThrow("is_available")) == 1;

            items.add(new MenuItem(itemId, name, desc, price, imageUri, category, available));
        }
        c.close();
        return items;
    }
}
