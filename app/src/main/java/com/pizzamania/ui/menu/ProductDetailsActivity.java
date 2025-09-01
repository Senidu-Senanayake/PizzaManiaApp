package com.pizzamania.ui.menu;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.pizzamania.R;
import com.pizzamania.data.model.MenuItem;
import com.pizzamania.data.repo.CartRepository;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView imgProduct;
    private TextView txtName, txtDesc, txtPrice, txtQuantity;
    private RadioGroup sizeGroup;
    private MaterialButton btnAddToCart, btnIncrease, btnDecrease;
    private CheckBox cbCheese, cbOlives, cbMushrooms;

    private MenuItem menuItem;
    private int userId;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        imgProduct = findViewById(R.id.img_product);
        txtName = findViewById(R.id.txt_name);
        txtDesc = findViewById(R.id.txt_desc);
        txtPrice = findViewById(R.id.txt_price);
        sizeGroup = findViewById(R.id.radio_group_size);
        txtQuantity = findViewById(R.id.txt_quantity);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnIncrease = findViewById(R.id.btn_increase);
        btnDecrease = findViewById(R.id.btn_decrease);

        cbCheese = findViewById(R.id.checkbox_cheese);
        cbOlives = findViewById(R.id.checkbox_olives);
        cbMushrooms = findViewById(R.id.checkbox_mushrooms);

        userId = getSharedPreferences("PizzaManiaPrefs", MODE_PRIVATE)
                .getInt("logged_in_user", -1);

        menuItem = (MenuItem) getIntent().getSerializableExtra("menu_item");

        if (menuItem != null) {
            txtName.setText(menuItem.getName());
            txtDesc.setText(menuItem.getDescription());
            txtPrice.setText("Rs. " + (menuItem.getPriceCents() / 100.0));

            Glide.with(this)
                    .load(menuItem.getImageUri())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(imgProduct);
        }

        // Quantity buttons
        btnIncrease.setOnClickListener(v -> {
            quantity++;
            txtQuantity.setText(String.valueOf(quantity));
        });

        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                txtQuantity.setText(String.valueOf(quantity));
            }
        });

        // Add to Cart
        btnAddToCart.setOnClickListener(v -> {
            if (menuItem != null) {
                int selectedId = sizeGroup.getCheckedRadioButtonId();
                String size = "Medium"; // default
                if (selectedId != -1) {
                    RadioButton selected = findViewById(selectedId);
                    size = selected.getText().toString();
                }

                int finalPrice = menuItem.getPriceCents();

                // Adjust size price
                if (size.equalsIgnoreCase("Small")) {
                    finalPrice = (int) (finalPrice * 0.8);
                } else if (size.equalsIgnoreCase("Large")) {
                    finalPrice = (int) (finalPrice * 1.2);
                }

                // Extra toppings
                if (cbCheese.isChecked()) finalPrice += 200;
                if (cbOlives.isChecked()) finalPrice += 150;
                if (cbMushrooms.isChecked()) finalPrice += 180;

                finalPrice = finalPrice * quantity;

                CartRepository cartRepo = new CartRepository(this);
                cartRepo.addToCart(userId, menuItem.getItemId(), quantity, finalPrice, size);

                Toast.makeText(this,
                        menuItem.getName() + " (" + size + ", x" + quantity + ") added to cart!",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }
}
