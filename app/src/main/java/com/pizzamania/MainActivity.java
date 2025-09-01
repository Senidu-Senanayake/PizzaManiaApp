package com.pizzamania;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pizzamania.ui.home.OrdersFragment;
import com.pizzamania.ui.menu.CartFragment;
import com.pizzamania.ui.menu.MenuFragment;
import com.pizzamania.ui.home.BranchesFragment;
import com.pizzamania.ui.auth.AccountFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 🔹 Check if user is logged in
        int userId = getSharedPreferences("PizzaManiaPrefs", MODE_PRIVATE)
                .getInt("logged_in_user", -1);

        if (userId == -1) {
            // No logged in user → go to LoginActivity
            startActivity(new Intent(this, com.pizzamania.ui.auth.LoginActivity.class));
            finish();
            return;
        }

        // If logged in → load Main UI
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;

            int id = item.getItemId();
            if (id == R.id.nav_menu) {
                selected = new MenuFragment();
            }else if (id == R.id.nav_cart) {
                selected = new CartFragment();}
            else if (id == R.id.nav_orders) {
                selected = new OrdersFragment();
            } else if (id == R.id.nav_branches) {
                selected = new BranchesFragment();
            } else if (id == R.id.nav_account) {
                selected = new AccountFragment();
            }

            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selected)
                        .commit();
            }
            return true;
        });

        // Load default fragment (Menu)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MenuFragment())
                    .commit();
        }
    }
}
