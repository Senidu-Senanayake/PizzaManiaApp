package com.pizzamania.ui.checkout;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.pizzamania.R;
import com.pizzamania.data.db.AppDbHelper;
import com.pizzamania.data.model.User;
import com.pizzamania.data.repo.CartRepository;
import com.pizzamania.data.repo.UserRepository;

public class CheckoutActivity extends AppCompatActivity {

    private TextView txtUserInfo, txtCheckoutTotal, txtDeliveryFee, txtFinalTotal;
    private EditText edtAddress;
    private RadioGroup radioGroupPayment;
    private MaterialButton btnPlaceOrder;

    private int userId;
    private double subtotal = 0;
    private static final int DELIVERY_FEE = 300; // Rs.300 fixed delivery fee

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        txtUserInfo = findViewById(R.id.txt_user_info);
        txtCheckoutTotal = findViewById(R.id.txt_checkout_total);
        txtDeliveryFee = findViewById(R.id.txt_delivery_fee);
        txtFinalTotal = findViewById(R.id.txt_final_total);
        edtAddress = findViewById(R.id.edt_address);
        radioGroupPayment = findViewById(R.id.radio_group_payment);
        btnPlaceOrder = findViewById(R.id.btn_place_order);

        userId = getSharedPreferences("PizzaManiaPrefs", MODE_PRIVATE)
                .getInt("logged_in_user", -1);

        loadUserDetails();
        calculateTotals();

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void loadUserDetails() {
        UserRepository userRepo = new UserRepository(this);
        User user = userRepo.getUserById(userId);

        if (user != null) {
            txtUserInfo.setText("User: " + user.getFullName()
                    + "\nPhone: " + user.getPhone()
                    + "\nEmail: " + user.getEmail());

            if (user.getAddress() != null && !user.getAddress().isEmpty()) {
                edtAddress.setText(user.getAddress());
            }
        } else {
            txtUserInfo.setText("Guest User");
        }
    }

    private void calculateTotals() {
        CartRepository cartRepo = new CartRepository(this);
        Cursor c = cartRepo.getCartItems(userId);

        subtotal = 0;
        while (c.moveToNext()) {
            int qty = c.getInt(c.getColumnIndexOrThrow("qty"));
            int unitPrice = c.getInt(c.getColumnIndexOrThrow("unit_price_cents"));
            subtotal += (unitPrice / 100.0) * qty;
        }
        c.close();

        double finalTotal = subtotal + DELIVERY_FEE;

        txtCheckoutTotal.setText("Subtotal: Rs. " + subtotal);
        txtDeliveryFee.setText("Delivery Fee: Rs. " + DELIVERY_FEE);
        txtFinalTotal.setText("Final Total: Rs. " + finalTotal);
    }

    private void placeOrder() {
        String address = edtAddress.getText().toString().trim();
        if (address.isEmpty()) {
            Toast.makeText(this, "Please enter delivery address", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = radioGroupPayment.getCheckedRadioButtonId();
        String paymentMethod = "Cash";
        String status = "Pending";

        if (selectedId == R.id.radio_card) {
            paymentMethod = "Card";
            status = "Paid";
        } else if (selectedId == R.id.radio_wallet) {
            paymentMethod = "Wallet";
            status = "Paid";
        }

        double finalTotal = subtotal + DELIVERY_FEE;

        AppDbHelper dbHelper = new AppDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues orderValues = new ContentValues();
        orderValues.put("user_id", userId);
        orderValues.put("branch_id", 1); // hardcoded for now
        orderValues.put("status", status);
        orderValues.put("created_at", System.currentTimeMillis());
        orderValues.put("total_cents", (int) (finalTotal * 100));
        orderValues.put("delivery_address", address);
        orderValues.put("payment_method", paymentMethod);

        long orderId = db.insert("`Order`", null, orderValues);

        if (orderId != -1) {
            Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show();

            CartRepository cartRepo = new CartRepository(this);
            cartRepo.clearCart(userId);

            finish();
        } else {
            Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show();
        }
    }
}
