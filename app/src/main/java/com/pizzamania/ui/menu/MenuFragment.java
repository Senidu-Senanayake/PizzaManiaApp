package com.pizzamania.ui.menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.card.MaterialCardView;
import com.pizzamania.R;
import com.pizzamania.data.model.MenuItem;
import com.pizzamania.data.repo.MenuRepository;
import com.pizzamania.data.repo.CartRepository;

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
        setupCartButtons();
        loadMenuItems();

        updateCartBadge();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartBadge();
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerMenu.setLayoutManager(gridLayoutManager);
        recyclerMenu.setHasFixedSize(true);

        int spacing = getResources().getDimensionPixelSize(R.dimen.menu_item_spacing);
        recyclerMenu.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
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
                        animateChipSelection(selectedChip);
                    }
                }
            });
        }
    }

    private String extractCategoryFromChip(String chipText) {
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

    private void setupCartButtons() {
        if (fabCart != null) {
            fabCart.setOnClickListener(v -> {
                openCartPage();
                animateCartClick();
            });
        }

        if (cartBadgeContainer != null) {
            cartBadgeContainer.setOnClickListener(v -> openCartPage());
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
        new Thread(() -> {
            try {
                menuItems = menuRepository.getAllMenuItems();

                if (getActivity() != null) {
                    getActivity().runOnUiThread(this::setupAdapter);
                }
            } catch (Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(this::showEmptyState);
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
            showEmptyState();
        }
    }

    private void showEmptyState() {
        Toast.makeText(getContext(), "No menu items available", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddToCart(MenuItem item, int quantity) {
        updateCartBadge();
    }

    @Override
    public void onItemClick(MenuItem item) {
        if (item != null && getContext() != null) {
            Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
            intent.putExtra("menu_item", item);
            startActivity(intent);
        }
    }

    //Count total quantity of items in cart and update badge
    private void updateCartBadge() {
        if (cartBadgeContainer != null && cartBadgeText != null) {
            // Get logged in user
            int userId = requireContext()
                    .getSharedPreferences("PizzaManiaPrefs", Context.MODE_PRIVATE)
                    .getInt("logged_in_user", -1);

            if (userId == -1) {
                cartBadgeContainer.setVisibility(View.GONE);
                return;
            }

            // Query cart repository
            CartRepository cartRepo = new CartRepository(requireContext());
            int productCount = cartRepo.getCartItemCount(userId);

            if (productCount > 0) {
                cartBadgeContainer.setVisibility(View.VISIBLE);

                String displayCount = productCount > 99 ? "99+" : String.valueOf(productCount);
                cartBadgeText.setText(displayCount);

                Log.d("MenuFragment", "Cart badge updated: " + displayCount);
            } else {
                cartBadgeContainer.setVisibility(View.GONE);
                Log.d("MenuFragment", "Cart empty → hiding badge");
            }
        }
    }




    private void openCartPage() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new CartFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(@NonNull android.graphics.Rect outRect,
                                   @NonNull View view,
                                   @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;
                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }
}
