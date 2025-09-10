package com.pizzamania.ui.menu;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.card.MaterialCardView;
import com.pizzamania.R;
import com.pizzamania.data.model.MenuItem;
import com.pizzamania.data.repo.MenuRepository;
import java.util.List;

public class MenuFragment extends Fragment implements MenuAdapter.OnItemClickListener {

    private RecyclerView recyclerMenu;
    private MenuAdapter menuAdapter;
    private TextInputEditText searchMenu;
    private ChipGroup chipGroupCategories;
    private FloatingActionButton fabCart;
    private MaterialCardView cartBadgeContainer;
    private TextView cartBadgeText;

    private MenuRepository menuRepository;
    private List<MenuItem> menuItems;
    private int cartItemCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initRepository();
        setupRecyclerView();
        setupSearchFunctionality();
        setupCategoryFilters();
        setupCartButton();
        loadMenuItems();
    }

    private void initViews(View view) {
        recyclerMenu = view.findViewById(R.id.recycler_menu);
        searchMenu = view.findViewById(R.id.search_menu);
        chipGroupCategories = view.findViewById(R.id.chip_group_categories);
        fabCart = view.findViewById(R.id.fab_cart);
        cartBadgeContainer = view.findViewById(R.id.cart_badge_container);
        cartBadgeText = view.findViewById(R.id.cart_badge_text);
    }

    private void initRepository() {
        menuRepository = new MenuRepository(getContext());
    }

    private void setupRecyclerView() {
        recyclerMenu.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerMenu.setHasFixedSize(true);

        // Add item decoration for spacing
        int spacing = getResources().getDimensionPixelSize(R.dimen.menu_item_spacing);
        recyclerMenu.addItemDecoration(new MenuItemDecoration(spacing));
    }

    private void setupSearchFunctionality() {
        if (searchMenu != null) {
            searchMenu.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (menuAdapter != null) {
                        menuAdapter.filterBySearch(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void setupCategoryFilters() {
        if (chipGroupCategories != null) {
            chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (!checkedIds.isEmpty()) {
                    int checkedId = checkedIds.get(0);
                    Chip selectedChip = group.findViewById(checkedId);
                    if (selectedChip != null && menuAdapter != null) {
                        String category = extractCategoryFromChip(selectedChip.getText().toString());
                        menuAdapter.filterByCategory(category);

                        // Add visual feedback
                        animateChipSelection(selectedChip);
                    }
                }
            });
        }
    }

    private String extractCategoryFromChip(String chipText) {
        // Remove emoji and extract category name
        return chipText.replaceAll("[^A-Za-z\\s]", "").trim();
    }

    private void animateChipSelection(Chip chip) {
        if (chip != null) {
            chip.animate()
                    .scaleX(1.05f)
                    .scaleY(1.05f)
                    .setDuration(100)
                    .withEndAction(() ->
                            chip.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(100)
                                    .start())
                    .start();
        }
    }

    private void setupCartButton() {
        if (fabCart != null) {
            fabCart.setOnClickListener(v -> {
                // Navigate to cart
                // Navigation logic here
                animateCartClick();
            });
        }
    }

    private void animateCartClick() {
        if (fabCart != null) {
            fabCart.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(100)
                    .withEndAction(() ->
                            fabCart.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(100)
                                    .start())
                    .start();
        }
    }

    private void loadMenuItems() {
        // In a real app, this would be done with a ViewModel and LiveData
        new Thread(() -> {
            try {
                menuItems = menuRepository.getAllMenuItems();

                // Switch back to main thread for UI updates
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        setupAdapter();
                    });
                }
            } catch (Exception e) {
                // Handle error case
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showEmptyState();
                    });
                }
            }
        }).start();
    }

    private void setupAdapter() {
        if (menuItems != null && !menuItems.isEmpty()) {
            menuAdapter = new MenuAdapter(getContext(), menuItems);
            menuAdapter.setOnItemClickListener(this);
            recyclerMenu.setAdapter(menuAdapter);
        } else {
            // Show empty state or error message
            showEmptyState();
        }
    }

    private void showEmptyState() {
        // Implement empty state UI
        // You could show a message saying "No menu items available"
    }

    @Override
    public void onAddToCart(MenuItem item, int quantity) {
        // Handle adding item to cart
        if (item != null && quantity > 0) {
            cartItemCount += quantity;
            updateCartBadge();

            // Show feedback to user
            showAddToCartFeedback(item, quantity);
        }
    }

    @Override
    public void onItemClick(MenuItem item) {
        // Handle item click - could show detailed view
        // Navigate to item details or show dialog
        if (item != null) {
            // Implement item detail navigation or dialog
        }
    }

    private void updateCartBadge() {
        if (cartBadgeContainer != null && cartBadgeText != null) {
            if (cartItemCount > 0) {
                cartBadgeContainer.setVisibility(View.VISIBLE);
                cartBadgeText.setText(String.valueOf(Math.min(cartItemCount, 99))); // Cap at 99

                // Animate badge appearance
                cartBadgeContainer.animate()
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(150)
                        .withEndAction(() ->
                                cartBadgeContainer.animate()
                                        .scaleX(1f)
                                        .scaleY(1f)
                                        .setDuration(150)
                                        .start())
                        .start();
            } else {
                cartBadgeContainer.setVisibility(View.GONE);
            }
        }
    }

    private void showAddToCartFeedback(MenuItem item, int quantity) {
        // You could show a Snackbar or Toast here
        if (getView() != null && item != null) {
            String message = quantity > 0 ?
                    item.getName() + " added to cart" :
                    item.getName() + " removed from cart";

            // Show feedback (implement Snackbar or custom animation)
            // Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    // Custom ItemDecoration for RecyclerView spacing
    private static class MenuItemDecoration extends RecyclerView.ItemDecoration {
        private final int spacing;

        public MenuItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(@NonNull android.graphics.Rect outRect,
                                   @NonNull View view,
                                   @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            outRect.bottom = spacing;

            // Add top margin for first item
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = spacing;
            }
        }
    }
}