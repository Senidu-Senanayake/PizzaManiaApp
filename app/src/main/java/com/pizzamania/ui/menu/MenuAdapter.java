package com.pizzamania.ui.menu;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pizzamania.R;
import com.pizzamania.data.model.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private final Context context;
    private final List<MenuItem> menuItems;
    private final List<MenuItem> filteredItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MenuItem item);
        void onAddToCart(MenuItem item, int quantity);
    }

    public MenuAdapter(Context context, List<MenuItem> menuItems) {
        this.context = context;
        this.menuItems = menuItems != null ? menuItems : new ArrayList<>();
        this.filteredItems = new ArrayList<>(this.menuItems);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        if (position < filteredItems.size()) {
            MenuItem item = filteredItems.get(position);
            holder.bind(item);
        }
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    public void updateItems(List<MenuItem> newItems) {
        if (newItems != null) {
            this.menuItems.clear();
            this.menuItems.addAll(newItems);
            this.filteredItems.clear();
            this.filteredItems.addAll(newItems);
            notifyDataSetChanged();
        }
    }

    public void filterBySearch(String query) {
        filteredItems.clear();
        if (query == null || query.isEmpty()) {
            filteredItems.addAll(menuItems);
        } else {
            String q = query.toLowerCase(Locale.ROOT);
            for (MenuItem item : menuItems) {
                if (item != null && item.getName() != null &&
                        item.getName().toLowerCase(Locale.ROOT).contains(q)) {
                    filteredItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterByCategory(String category) {
        filteredItems.clear();
        if (category == null || category.equalsIgnoreCase("All") || category.isEmpty()) {
            filteredItems.addAll(menuItems);
        } else {
            for (MenuItem item : menuItems) {
                if (item != null && item.getCategory() != null &&
                        item.getCategory().equalsIgnoreCase(category)) {
                    filteredItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView descriptionText;
        private final TextView priceText;
        private final ImageView itemImage;
        private final Button addToCartButton;

        MenuViewHolder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.txt_name);
            descriptionText = itemView.findViewById(R.id.txt_desc);
            priceText = itemView.findViewById(R.id.txt_price);
            itemImage = itemView.findViewById(R.id.img_item);
            addToCartButton = itemView.findViewById(R.id.btn_add);

            // Add to cart button → go to ProductDetails
            if (addToCartButton != null) {
                addToCartButton.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION &&
                            position < filteredItems.size()) {
                        MenuItem item = filteredItems.get(position);
                        Intent intent = new Intent(context, ProductDetailsActivity.class);
                        intent.putExtra("menu_item", item);
                        context.startActivity(intent);
                    }
                });
            }

            // Whole card clickable → go to ProductDetails
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION &&
                        position < filteredItems.size()) {
                    MenuItem item = filteredItems.get(position);
                    Intent intent = new Intent(context, ProductDetailsActivity.class);
                    intent.putExtra("menu_item", item);
                    context.startActivity(intent);
                }
            });
        }

        void bind(MenuItem item) {
            if (item == null) return;

            // Name & description
            nameText.setText(item.getName() != null ? item.getName() : "Unknown Item");
            descriptionText.setText(item.getDescription() != null ? item.getDescription() : "No description available");

            // Price (divide cents → rupees)
            double price = item.getPriceCents();
            priceText.setText(String.format(Locale.ROOT, "Rs. %.2f", price));

            // Image
            if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
                int resId = context.getResources().getIdentifier(
                        item.getImageUri(), "drawable", context.getPackageName()
                );
                if (resId != 0) {
                    itemImage.setImageResource(resId);
                } else {
                    Glide.with(context)
                            .load(item.getImageUri()) // fallback if URL
                            .placeholder(R.drawable.ic_pizza_logo)
                            .into(itemImage);
                }
            } else {
                itemImage.setImageResource(R.drawable.ic_pizza_logo);
            }

            // Availability
            boolean isAvailable = true;
            try {
                isAvailable = item.isAvailable();
            } catch (Exception ignored) {}

            addToCartButton.setEnabled(isAvailable);
            addToCartButton.setText(isAvailable ? "Add" : "Unavailable");
            addToCartButton.setAlpha(isAvailable ? 1.0f : 0.5f);
        }
    }
}
