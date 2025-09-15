package com.pizzamania.ui.menu;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.pizzamania.R;
import com.pizzamania.data.model.MenuItem;
import com.pizzamania.data.repo.CartRepository;
import com.pizzamania.data.repo.MenuRepository;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView txtTotal;
    private MaterialButton btnCheckout;

    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_cart);
        txtTotal = view.findViewById(R.id.txt_total);
        btnCheckout = view.findViewById(R.id.btn_checkout);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        userId = requireContext()
                .getSharedPreferences("PizzaManiaPrefs", requireContext().MODE_PRIVATE)
                .getInt("logged_in_user", -1);

        loadCart();
    }

    private void loadCart() {
        CartRepository cartRepo = new CartRepository(requireContext());
        MenuRepository menuRepo = new MenuRepository(requireContext());

        Cursor c = cartRepo.getCartItems(userId);
        List<CartAdapter.CartItemDisplay> items = new ArrayList<>();
        double total = 0;

        while (c.moveToNext()) {
            int itemId = c.getInt(c.getColumnIndexOrThrow("item_id"));
            int qty = c.getInt(c.getColumnIndexOrThrow("qty"));
            int unitPriceCents = c.getInt(c.getColumnIndexOrThrow("unit_price_cents"));

            // Find product name
            String name = "Unknown Item";
            for (MenuItem m : menuRepo.getAllMenuItems()) {
                if (m.getItemId() == itemId) {
                    name = m.getName();
                    break;
                }
            }

            // Calculate line total
            double price = unitPriceCents * qty;
            total += price;

            items.add(new CartAdapter.CartItemDisplay(itemId, name, qty, price));
        }
        c.close();

        CartAdapter adapter = new CartAdapter(requireContext(), items, newTotal -> {
            txtTotal.setText("Total: Rs. " + String.format("%.2f", newTotal));
        });
        recyclerView.setAdapter(adapter);


        txtTotal.setText("Total: Rs. " + String.format("%.2f", total));

        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), com.pizzamania.ui.checkout.CheckoutActivity.class);
            startActivity(intent);
        });
    }
}
