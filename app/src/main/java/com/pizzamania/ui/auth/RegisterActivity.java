package com.pizzamania.ui.auth;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pizzamania.R;
import com.pizzamania.data.db.AppDbHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPassword;
    private Button btnRegister, btnGoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edt_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnRegister = findViewById(R.id.btn_register);
        btnGoLogin = findViewById(R.id.btn_go_login);

        btnRegister.setOnClickListener(v -> registerUser());
        btnGoLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
    }

    private void registerUser() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDbHelper dbHelper = new AppDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("full_name", name);
        cv.put("email", email);
        cv.put("password_hash", password);

        long id = db.insert("User", null, cv);
        if (id != -1) {
            Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Registration failed (email may exist)", Toast.LENGTH_SHORT).show();
        }
    }
}
