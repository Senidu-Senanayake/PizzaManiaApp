package com.pizzamania.ui.home;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.pizzamania.R;
import com.pizzamania.data.db.AppDbHelper;
import com.pizzamania.data.model.MenuItem;
import com.pizzamania.data.model.User;
import com.pizzamania.data.repo.MenuRepository;
import com.pizzamania.data.repo.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView txtOrderId, txtDate, txtStatus, txtPayment, txtAddress, txtUser, txtEstimate, txtTotals;
    private LinearLayout itemsContainer;

    private int orderId;
    private static final int DELIVERY_FEE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        txtOrderId = findViewById(R.id.txt_order_id);
        txtDate = findViewById(R.id.txt_order_date);
        txtStatus = findViewById(R.id.txt_order_status);
        txtPayment = findViewById(R.id.txt_order_payment);
        txtAddress = findViewById(R.id.txt_order_address);
        txtUser = findViewById(R.id.txt_order_user);
        txtEstimate = findViewById(R.id.txt_order_estimate);
        txtTotals = findViewById(R.id.txt_order_totals);
        itemsContainer = findViewById(R.id.items_container);

        orderId = getIntent().getIntExtra("order_id", -1);
        if (orderId != -1) {
            loadOrderDetails(orderId);
        }
    }

    private void loadOrderDetails(int orderId) {
        AppDbHelper dbHelper = new AppDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM `Order` WHERE order_id=?", new String[]{String.valueOf(orderId)});
        if (c.moveToFirst()) {
            int userId = c.getInt(c.getColumnIndexOrThrow("user_id"));
            long createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"));
            String status = c.getString(c.getColumnIndexOrThrow("status"));
            String payment = c.getString(c.getColumnIndexOrThrow("payment_method"));
            String address = c.getString(c.getColumnIndexOrThrow("delivery_address"));
            int totalCents = c.getInt(c.getColumnIndexOrThrow("total_cents"));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            txtOrderId.setText("Order #" + orderId);
            txtDate.setText("Date: " + sdf.format(new Date(createdAt)));
            txtStatus.setText("Status: " + status);
            txtPayment.setText("Payment: " + payment);
            txtAddress.setText("Ship To: " + address);

            // user details
            UserRepository userRepo = new UserRepository(this);
            User user = userRepo.getUserById(userId);
            if (user != null) {
                txtUser.setText("Customer: " + user.getFullName() + " (" + user.getPhone() + ")");
            }

            // estimated delivery (45 mins later)
            txtEstimate.setText("Est. Delivery: " + sdf.format(new Date(createdAt + 45 * 60 * 1000)));

            // now load order items
            loadOrderItems(orderId);

            txtTotals.setText("Subtotal + Delivery Fee = Rs. " + (totalCents / 100.0));
        }
        c.close();
    }

    private void loadOrderItems(int orderId) {
        AppDbHelper dbHelper = new AppDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        MenuRepository menuRepo = new MenuRepository(this);

        Cursor c = db.rawQuery("SELECT * FROM OrderItem WHERE order_id=?", new String[]{String.valueOf(orderId)});
        while (c.moveToNext()) {
            int itemId = c.getInt(c.getColumnIndexOrThrow("item_id"));
            int qty = c.getInt(c.getColumnIndexOrThrow("qty"));
            int unitPrice = c.getInt(c.getColumnIndexOrThrow("unit_price_cents"));

            MenuItem mi = null;
            for (MenuItem m : menuRepo.getAllMenuItems()) {
                if (m.getItemId() == itemId) {
                    mi = m;
                    break;
                }
            }

            if (mi != null) {
                // inflate item layout dynamically
                LinearLayout itemLayout = new LinearLayout(this);
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                itemLayout.setPadding(0, 12, 0, 12);

                ImageView img = new ImageView(this);
                LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(200, 200);
                img.setLayoutParams(imgParams);

                Glide.with(this)
                        .load(mi.getImageUri())
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(img);

                LinearLayout textLayout = new LinearLayout(this);
                textLayout.setOrientation(LinearLayout.VERTICAL);
                textLayout.setPadding(20, 0, 0, 0);

                TextView name = new TextView(this);
                name.setText(mi.getName() + " x" + qty);

                TextView desc = new TextView(this);
                desc.setText(mi.getDescription());

                TextView price = new TextView(this);
                price.setText("Rs. " + ((unitPrice / 100.0) * qty));

                textLayout.addView(name);
                textLayout.addView(desc);
                textLayout.addView(price);

                itemLayout.addView(img);
                itemLayout.addView(textLayout);

                itemsContainer.addView(itemLayout);
            }
        }
        c.close();
    }
}
