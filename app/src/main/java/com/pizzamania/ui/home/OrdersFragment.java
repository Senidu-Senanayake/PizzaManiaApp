package com.pizzamania.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.pizzamania.R;
import com.pizzamania.data.repo.OrderRepository;

public class OrdersFragment extends Fragment {

    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_orders, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recycler_orders);

        userId = requireContext()
                .getSharedPreferences("PizzaManiaPrefs", requireContext().MODE_PRIVATE)
                .getInt("logged_in_user", -1);

        OrderRepository repo = new OrderRepository(requireContext());
        OrdersAdapter adapter = new OrdersAdapter(repo.getOrders(userId)); // user_id=1 for now
        recyclerView.setAdapter(adapter);

        return root;
    }
}
