// EditProfileActivity.java
package com.pizzamania.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.pizzamania.R;
import com.pizzamania.data.model.User;
import com.pizzamania.data.repo.UserRepository;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtName, edtPhone, edtEmail, edtAddress;
    private MaterialButton btnSave;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edtName = findViewById(R.id.edt_name);
        edtPhone = findViewById(R.id.edt_phone);
        edtEmail = findViewById(R.id.edt_email);
        edtAddress = findViewById(R.id.edt_address);
        btnSave = findViewById(R.id.btn_save);

        userId = getSharedPreferences("PizzaManiaPrefs", MODE_PRIVATE)
                .getInt("logged_in_user", -1);

        UserRepository repo = new UserRepository(this);
        User user = repo.getUserById(userId);

        if (user != null) {
            edtName.setText(user.getFullName());
            edtPhone.setText(user.getPhone());
            edtEmail.setText(user.getEmail());
            edtAddress.setText(user.getAddress());
        }

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "All fields required!", Toast.LENGTH_SHORT).show();
                return;
            }

            User updatedUser = new User(userId, name, phone, email, user.getPasswordHash(), address, user.getLat(), user.getLng());
            repo.updateUser(updatedUser);

            Toast.makeText(this, "Profile updated ✅", Toast.LENGTH_SHORT).show();

            // Go back to account screen
            finish();
        });
    }
}
