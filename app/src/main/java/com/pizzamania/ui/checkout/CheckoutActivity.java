package com.pizzamania.ui.checkout;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.pizzamania.R;
import com.pizzamania.data.db.AppDbHelper;
import com.pizzamania.data.model.MenuItem;
import com.pizzamania.data.repo.CartRepository;
import com.pizzamania.data.repo.MenuRepository;
import com.pizzamania.data.repo.OrderRepository;

public class CheckoutActivity extends AppCompatActivity {

    private TextView txtTotal;
    private EditText edtAddress;
    private MaterialButton btnPlaceOrder;

    private int totalCents = 0; // store in cents for DB consistency
    private int userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        userId = getSharedPreferences("PizzaManiaPrefs", MODE_PRIVATE)
                .getInt("logged_in_user", -1);

        txtTotal = findViewById(R.id.txt_checkout_total);
        edtAddress = findViewById(R.id.edt_address);
        btnPlaceOrder = findViewById(R.id.btn_place_order);

        // Calculate and show total
        totalCents = calculateTotalCents();
        txtTotal.setText("Total: Rs. " + (totalCents / 100.0));

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {
        String address = edtAddress.getText().toString().trim();
        if (address.isEmpty()) {
            edtAddress.setError("Address required");
            return;
        }

        CartRepository cartRepo = new CartRepository(this);
        MenuRepository menuRepo = new MenuRepository(this);
        OrderRepository orderRepo = new OrderRepository(this);

        // 1. Insert into Order table
        long orderId = orderRepo.insertOrder(
                userId,       // user_id (hardcoded for now)
                1,       // branch_id (Colombo for now)
                0,       // total will be updated later
                address,
                "Cash"   // payment method
        );


        // 2. Insert items & calculate total
        Cursor c = cartRepo.getCartItems(userId);
        int runningTotal = 0;

        while (c.moveToNext()) {
            int itemId = c.getInt(c.getColumnIndexOrThrow("item_id"));
            int qty = c.getInt(c.getColumnIndexOrThrow("qty"));

            for (MenuItem m : menuRepo.getAllMenuItems()) {
                if (m.getItemId() == itemId) {
                    int unitPriceCents = m.getPriceCents();
                    runningTotal += unitPriceCents * qty;

                    orderRepo.insertOrderItem(orderId, itemId, qty, unitPriceCents);
                }
            }
        }
        c.close();

        // 3. Update order total
        SQLiteDatabase db = new AppDbHelper(this).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("total_cents", runningTotal);
        db.update("`Order`", cv, "order_id=?", new String[]{String.valueOf(orderId)});
        db.close();

        // 4. Clear cart
        cartRepo.clearCart(userId);

        // 5. Success
        android.widget.Toast.makeText(this, "Order placed! 🎉", android.widget.Toast.LENGTH_LONG).show();
        finish();
    }

    private int calculateTotalCents() {
        CartRepository cartRepo = new CartRepository(this);
        MenuRepository menuRepo = new MenuRepository(this);

        Cursor c = cartRepo.getCartItems(userId);
        int total = 0;

        while (c.moveToNext()) {
            int itemId = c.getInt(c.getColumnIndexOrThrow("item_id"));
            int qty = c.getInt(c.getColumnIndexOrThrow("qty"));

            for (MenuItem m : menuRepo.getAllMenuItems()) {
                if (m.getItemId() == itemId) {
                    total += m.getPriceCents() * qty;
                }
            }
        }
        c.close();
        return total;
    }
}
