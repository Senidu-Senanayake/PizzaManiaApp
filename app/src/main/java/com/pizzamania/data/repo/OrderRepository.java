package com.pizzamania.data.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pizzamania.data.db.AppDbHelper;
import com.pizzamania.data.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private final AppDbHelper dbHelper;

    public OrderRepository(Context context) {
        dbHelper = new AppDbHelper(context);
    }

    public long insertOrder(int userId, int branchId, int totalCents, String address, String paymentMethod) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("branch_id", branchId);
        cv.put("status", "Pending");
        cv.put("created_at", System.currentTimeMillis());
        cv.put("total_cents", totalCents);
        cv.put("delivery_address", address);
        cv.put("payment_method", paymentMethod);
        return db.insert("`Order`", null, cv);
    }

    public void insertOrderItem(long orderId, int itemId, int qty, int unitPriceCents) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("order_id", orderId);
        cv.put("item_id", itemId);
        cv.put("qty", qty);
        cv.put("unit_price_cents", unitPriceCents);
        db.insert("OrderItem", null, cv);
    }

    public List<Order> getOrders(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query("`Order`", null, "user_id=?", new String[]{String.valueOf(userId)},
                null, null, "created_at DESC");

        List<Order> orders = new ArrayList<>();
        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndexOrThrow("order_id"));
            int branchId = c.getInt(c.getColumnIndexOrThrow("branch_id"));
            String status = c.getString(c.getColumnIndexOrThrow("status"));
            long createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
            int total = c.getInt(c.getColumnIndexOrThrow("total_cents"));
            String paymentMethod = c.getString(c.getColumnIndexOrThrow("payment_method"));

            orders.add(new Order(id, userId, branchId, status, createdAt, total, paymentMethod));
        }
        c.close();
        return orders;
    }
}
