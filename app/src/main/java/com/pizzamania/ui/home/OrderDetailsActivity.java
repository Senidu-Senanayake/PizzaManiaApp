package com.pizzamania.ui.home;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pizzamania.R;
import com.pizzamania.data.db.AppDbHelper;
import com.pizzamania.ui.home.OrderItemWithName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView txtOrderId, txtOrderStatus, txtOrderDate, txtOrderTotal;
    private RecyclerView recyclerView;
    private OrderItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        txtOrderId = findViewById(R.id.txt_order_id);
        txtOrderStatus = findViewById(R.id.txt_order_status);
        txtOrderDate = findViewById(R.id.txt_order_date);
        txtOrderTotal = findViewById(R.id.txt_order_total);
        recyclerView = findViewById(R.id.recycler_order_items);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int orderId = getIntent().getIntExtra("order_id", -1);
        if (orderId != -1) {
            loadOrderDetails(orderId);
        }
    }

    private void loadOrderDetails(int orderId) {
        AppDbHelper dbHelper = new AppDbHelper(this);

        // Load order info
        Cursor c = dbHelper.getReadableDatabase().rawQuery(
                "SELECT * FROM `Order` WHERE order_id=?",
                new String[]{String.valueOf(orderId)}
        );

        if (c.moveToFirst()) {
            String status = c.getString(c.getColumnIndexOrThrow("status"));
            long createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
            int totalCents = c.getInt(c.getColumnIndexOrThrow("total_cents"));

            txtOrderId.setText("Order #" + orderId);
            txtOrderStatus.setText("Status: " + status);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            txtOrderDate.setText("Date: " + sdf.format(createdAt));

            txtOrderTotal.setText("Total: Rs. " + (totalCents / 100.0));
        }
        c.close();

        // Load order items (join OrderItem + MenuItem)
        Cursor ci = dbHelper.getReadableDatabase().rawQuery(
                "SELECT oi.qty, oi.unit_price_cents, mi.name " +
                        "FROM OrderItem oi " +
                        "JOIN MenuItem mi ON oi.item_id = mi.item_id " +
                        "WHERE oi.order_id=?",
                new String[]{String.valueOf(orderId)}
        );

        List<OrderItemWithName> items = new ArrayList<>();
        while (ci.moveToNext()) {
            String name = ci.getString(ci.getColumnIndexOrThrow("name"));
            int qty = ci.getInt(ci.getColumnIndexOrThrow("qty"));
            int price = ci.getInt(ci.getColumnIndexOrThrow("unit_price_cents"));
            items.add(new OrderItemWithName(name, qty, price));
        }
        ci.close();

        adapter = new OrderItemAdapter(items);
        recyclerView.setAdapter(adapter);
    }
}
