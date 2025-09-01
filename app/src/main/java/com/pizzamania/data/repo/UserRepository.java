package com.pizzamania.data.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pizzamania.data.db.AppDbHelper;
import com.pizzamania.data.model.User;

public class UserRepository {
    private final AppDbHelper dbHelper;

    public UserRepository(Context context) {
        dbHelper = new AppDbHelper(context);
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(
                "User",
                null,
                "user_id=?",
                new String[]{String.valueOf(userId)},
                null, null, null
        );

        User user = null;
        if (c.moveToFirst()) {
            user = new User(
                    c.getInt(c.getColumnIndexOrThrow("user_id")),
                    c.getString(c.getColumnIndexOrThrow("full_name")),
                    c.getString(c.getColumnIndexOrThrow("phone")),
                    c.getString(c.getColumnIndexOrThrow("email")),
                    c.getString(c.getColumnIndexOrThrow("password_hash")),
                    c.getString(c.getColumnIndexOrThrow("address")),
                    c.getDouble(c.getColumnIndexOrThrow("lat")),
                    c.getDouble(c.getColumnIndexOrThrow("lng"))
            );
        }
        c.close();
        db.close();
        return user;
    }

    public void updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("full_name", user.getFullName());
        cv.put("phone", user.getPhone());
        cv.put("email", user.getEmail());
        cv.put("address", user.getAddress());
        cv.put("lat", user.getLat());
        cv.put("lng", user.getLng());

        db.update("User", cv, "user_id=?", new String[]{String.valueOf(user.getUserId())});
        db.close();
    }
}
