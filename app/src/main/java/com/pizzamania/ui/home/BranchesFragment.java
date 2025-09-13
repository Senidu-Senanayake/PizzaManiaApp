package com.pizzamania.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.pizzamania.R;
import com.pizzamania.data.model.Branch;
import com.pizzamania.data.repo.BranchRepository;

import java.util.List;

public class BranchesFragment extends Fragment implements BranchAdapter.OnBranchClickListener {

    private RecyclerView recyclerView;
    private TextInputEditText searchEditText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BranchAdapter adapter;
    private BranchRepository branchRepository;
    private List<Branch> branches;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_branches, container, false);

        initViews(root);
        setupRecyclerView();
        setupSearch();
        setupSwipeRefresh();
        loadBranches();

        return root;
    }

    private void initViews(View root) {
        recyclerView = root.findViewById(R.id.branchesRecyclerView);
        searchEditText = root.findViewById(R.id.search_branches);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);

        branchRepository = new BranchRepository(requireContext());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        // Add item decoration for spacing
        int spacing = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        recyclerView.addItemDecoration(new SpacingItemDecoration(spacing));
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadBranches);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.pizza_pepperoni_red,
                R.color.pizza_cheese_gold,
                R.color.pizza_sauce_red
        );
    }

    private void loadBranches() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        try {
            branches = branchRepository.getAllBranches();

            if (branches != null && !branches.isEmpty()) {
                setupAdapter();
            } else {
                showEmptyState();
            }
        } catch (Exception e) {
            showError("Failed to load branches: " + e.getMessage());
        } finally {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void setupAdapter() {
        if (adapter == null) {
            adapter = new BranchAdapter(branches, requireContext());
            adapter.setOnBranchClickListener(this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateBranches(branches);
        }
    }

    private void showEmptyState() {
        // Show empty state view - you can implement this based on your design
        Toast.makeText(getContext(), "No branches available", Toast.LENGTH_SHORT).show();
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    // BranchAdapter.OnBranchClickListener implementation
    @Override
    public void onBranchClick(Branch branch) {
        // Handle branch selection - could navigate to branch details or set as selected
        Toast.makeText(getContext(), "Selected: " + branch.getName(), Toast.LENGTH_SHORT).show();

        // You can implement branch selection logic here
        // For example, save selected branch to SharedPreferences
        // or navigate to a detailed branch view
    }

    @Override
    public void onDirectionsClick(Branch branch) {
        // Analytics or logging for directions click
        // Could also show a toast or update UI state
    }

    @Override
    public void onCallClick(Branch branch) {
        // Analytics or logging for call click
        // Could also show a toast or update UI state
        Toast.makeText(getContext(), "Calling " + branch.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up references
        recyclerView = null;
        searchEditText = null;
        swipeRefreshLayout = null;
        adapter = null;
    }

    // Item decoration class for spacing
    private static class SpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spacing;

        public SpacingItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(@NonNull android.graphics.Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.left = spacing;
            outRect.right = spacing;
            outRect.bottom = spacing;

            // Add top margin only for the first item
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = spacing;
            }
        }
    }
}