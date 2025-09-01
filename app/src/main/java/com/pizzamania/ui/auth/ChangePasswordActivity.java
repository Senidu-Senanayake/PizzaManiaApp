package com.pizzamania.ui.auth;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.pizzamania.R;
import com.pizzamania.data.db.AppDbHelper;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private MaterialButton btnSave;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edtOldPassword = findViewById(R.id.edt_old_password);
        edtNewPassword = findViewById(R.id.edt_new_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        btnSave = findViewById(R.id.btn_save_password);

        SharedPreferences prefs = getSharedPreferences("PizzaManiaPrefs", MODE_PRIVATE);
        userId = prefs.getInt("logged_in_user", -1);

        btnSave.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPwd = edtOldPassword.getText().toString().trim();
        String newPwd = edtNewPassword.getText().toString().trim();
        String confirmPwd = edtConfirmPassword.getText().toString().trim();

        if (oldPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPwd.equals(confirmPwd)) {
            Toast.makeText(this, "New password and confirm password do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verify old password
        SQLiteDatabase db = new AppDbHelper(this).getReadableDatabase();
        Cursor c = db.query("User",
                new String[]{"password_hash"},
                "user_id=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        if (c.moveToFirst()) {
            String currentHash = c.getString(c.getColumnIndexOrThrow("password_hash"));
            if (!currentHash.equals(oldPwd)) { // for now using plain text, ideally hash
                Toast.makeText(this, "Old password incorrect", Toast.LENGTH_SHORT).show();
                c.close();
                db.close();
                return;
            }
        }
        c.close();

        // Update password
        ContentValues cv = new ContentValues();
        cv.put("password_hash", newPwd);
        db = new AppDbHelper(this).getWritableDatabase();
        db.update("User", cv, "user_id=?", new String[]{String.valueOf(userId)});
        db.close();

        Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
