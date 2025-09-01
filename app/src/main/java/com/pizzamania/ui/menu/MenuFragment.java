package com.pizzamania.ui.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pizzamania.R;
import com.pizzamania.data.model.MenuItem;
import com.pizzamania.data.repo.MenuRepository;

import java.util.List;

public class MenuFragment extends Fragment {

    private RecyclerView recyclerView;
    private MenuAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        MenuRepository repo = new MenuRepository(requireContext());
        List<MenuItem> items = repo.getAllMenuItems();

        adapter = new MenuAdapter(requireContext(), items);
        recyclerView.setAdapter(adapter);
    }
}
