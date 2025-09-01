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
        this.dbHelper = new AppDbHelper(context);
    }

    // Insert a new order, return order_id
    public long insertOrder(int userId, int branchId, int totalCents, String address, String paymentMethod) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("branch_id", branchId);
        values.put("status", "Pending");
        values.put("created_at", System.currentTimeMillis());
        values.put("total_cents", totalCents);
        values.put("delivery_address", address);
        values.put("payment_method", paymentMethod);

        return db.insert("`Order`", null, values);
    }

    // Insert items for an order
    public void insertOrderItem(long orderId, int itemId, int qty, int unitPriceCents) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("order_id", orderId);
        values.put("item_id", itemId);
        values.put("qty", qty);
        values.put("unit_price_cents", unitPriceCents);

        db.insert("OrderItem", null, values);
    }

    public List<Order> getOrders(int userId) {
        List<Order> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query("`Order`",
                new String[]{"order_id", "user_id", "branch_id", "status", "created_at", "total_cents"},
                "user_id=?",
                new String[]{String.valueOf(userId)},
                null, null, "created_at DESC");

        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndexOrThrow("order_id"));
            int uId = c.getInt(c.getColumnIndexOrThrow("user_id"));
            int branchId = c.getInt(c.getColumnIndexOrThrow("branch_id"));
            String status = c.getString(c.getColumnIndexOrThrow("status"));
            long createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
            int total = c.getInt(c.getColumnIndexOrThrow("total_cents"));

            list.add(new Order(id, uId, branchId, status, createdAt, total));
        }
        c.close();
        return list;
    }

    public void updateOrderStatus(long orderId, String newStatus) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", newStatus);
        db.update("`Order`", cv, "order_id=?", new String[]{String.valueOf(orderId)});
        db.close();
    }

}
