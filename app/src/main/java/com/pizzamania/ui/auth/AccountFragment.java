package com.pizzamania.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.pizzamania.R;
import com.pizzamania.data.model.User;
import com.pizzamania.data.repo.UserRepository;
import com.pizzamania.ui.auth.LoginActivity;

public class AccountFragment extends Fragment {

    private TextView txtName, txtEmail, txtPhone, txtAddress;
    private MaterialButton btnLogout;

    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        txtName = root.findViewById(R.id.txt_name);
        txtEmail = root.findViewById(R.id.txt_email);
        txtPhone = root.findViewById(R.id.txt_phone);
        txtAddress = root.findViewById(R.id.txt_address);
        btnLogout = root.findViewById(R.id.btn_logout);

        userId = requireContext()
                .getSharedPreferences("PizzaManiaPrefs", requireContext().MODE_PRIVATE)
                .getInt("logged_in_user", -1);

        loadUserDetails(userId);

        MaterialButton btnEditProfile = root.findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditProfileActivity.class);
            startActivity(intent);
        });

        MaterialButton btnChangePassword = root.findViewById(R.id.btn_change_password);
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChangePasswordActivity.class);
            startActivity(intent);
        });



        btnLogout.setOnClickListener(v -> {
            requireContext()
                    .getSharedPreferences("PizzaManiaPrefs", requireContext().MODE_PRIVATE)
                    .edit()
                    .remove("logged_in_user")
                    .apply();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return root;
    }

    private void loadUserDetails(int userId) {
        if (userId == -1) {
            txtName.setText("Guest");
            txtEmail.setText("-");
            txtPhone.setText("-");
            txtAddress.setText("-");
            return;
        }

        UserRepository repo = new UserRepository(requireContext());
        User user = repo.getUserById(userId);

        if (user != null) {
            txtName.setText(user.getFullName());
            txtEmail.setText(user.getEmail());
            txtPhone.setText(user.getPhone());
            txtAddress.setText(user.getAddress());
        } else {
            txtName.setText("Unknown User");
        }
    }

}
