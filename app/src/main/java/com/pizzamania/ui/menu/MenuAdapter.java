package com.pizzamania.ui.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pizzamania.R;
import com.pizzamania.data.model.MenuItem;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
        private final TextView ratingText;
        private final ImageView itemImage;
        private final Button addToCartButton;

        MenuViewHolder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.txt_name);
            descriptionText = itemView.findViewById(R.id.txt_desc);
            priceText = itemView.findViewById(R.id.txt_price);
            ratingText = itemView.findViewById(R.id.txt_rating);
            itemImage = itemView.findViewById(R.id.img_item);
            addToCartButton = itemView.findViewById(R.id.btn_add);

            if (addToCartButton != null) {
                addToCartButton.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION &&
                            position < filteredItems.size() &&
                            listener != null) {
                        listener.onAddToCart(filteredItems.get(position), 1);
                    }
                });
            }

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION &&
                        position < filteredItems.size() &&
                        listener != null) {
                    listener.onItemClick(filteredItems.get(position));
                }
            });
        }

        void bind(MenuItem item) {
            if (item == null) return;

            // Name & description (these are expected to exist)
            nameText.setText(item.getName() != null ? item.getName() : "Unknown Item");
            descriptionText.setText(item.getDescription() != null ? item.getDescription() : "No description available");

            // Price: use reflection-safe lookup
            Double priceVal = getNumericFromItem(item, new String[] {
                    "getPrice", "getItemPrice", "getCost", "getAmount", "getPriceInCents", "price", "priceInCents"
            });
            if (priceVal != null) {
                // If price appears to be cents (very large int), still display as Rs. with two decimals
                priceText.setText(String.format(Locale.ROOT, "Rs. %.2f", priceVal));
            } else {
                // try string based getters/fields
                String priceStr = getStringFromItem(item, new String[] {
                        "getPriceString", "getPriceText", "getPriceLabel", "priceLabel"
                });
                priceText.setText(priceStr != null && !priceStr.isEmpty() ? priceStr : "Rs. 0.00");
            }

            // Rating: reflection-safe lookup
            Double ratingVal = getNumericFromItem(item, new String[] {
                    "getRating", "getAvgRating", "getStars", "rating", "score"
            });
            if (ratingVal != null) {
                ratingText.setText(String.format(Locale.ROOT, "%.1f ⭐", ratingVal));
            } else {
                String ratingStr = getStringFromItem(item, new String[] {
                        "getRatingText", "ratingText"
                });
                ratingText.setText(ratingStr != null && !ratingStr.isEmpty() ? ratingStr : "0.0 ⭐");
            }

            // Image - fallback to default drawable (replace with Glide/Picasso if you have URLs)
            try {
                itemImage.setImageResource(R.drawable.ic_pizza_logo);
            } catch (Exception ignored) {}

            // Availability - try boolean getters
            Boolean available = getBooleanFromItem(item, new String[] {
                    "isAvailable", "getAvailable", "available", "isInStock", "inStock"
            });
            if (addToCartButton != null) {
                boolean isAvailable = available == null ? true : available;
                addToCartButton.setEnabled(isAvailable);
                addToCartButton.setText(isAvailable ? "Add to Cart" : "Unavailable");
                addToCartButton.setAlpha(isAvailable ? 1.0f : 0.5f);
            }
        }

        // Reflection helpers ------------------------------------------------

        private Double getNumericFromItem(Object obj, String[] candidateNames) {
            if (obj == null) return null;

            // Try method names first
            for (String name : candidateNames) {
                try {
                    Method m = obj.getClass().getMethod(name);
                    Object res = m.invoke(obj);
                    Double v = parseToDouble(res);
                    if (v != null) return v;
                } catch (NoSuchMethodException ignored) {
                } catch (Exception ignored) {
                }
            }

            // Try fields
            for (String name : candidateNames) {
                try {
                    Field f = obj.getClass().getDeclaredField(name);
                    f.setAccessible(true);
                    Object res = f.get(obj);
                    Double v = parseToDouble(res);
                    if (v != null) return v;
                } catch (NoSuchFieldException ignored) {
                } catch (Exception ignored) {
                }
            }

            return null;
        }

        private String getStringFromItem(Object obj, String[] candidateNames) {
            if (obj == null) return null;

            for (String name : candidateNames) {
                try {
                    Method m = obj.getClass().getMethod(name);
                    Object res = m.invoke(obj);
                    if (res != null) return res.toString();
                } catch (NoSuchMethodException ignored) {
                } catch (Exception ignored) {
                }
            }

            for (String name : candidateNames) {
                try {
                    Field f = obj.getClass().getDeclaredField(name);
                    f.setAccessible(true);
                    Object res = f.get(obj);
                    if (res != null) return res.toString();
                } catch (NoSuchFieldException ignored) {
                } catch (Exception ignored) {
                }
            }

            return null;
        }

        private Boolean getBooleanFromItem(Object obj, String[] candidateNames) {
            if (obj == null) return null;

            for (String name : candidateNames) {
                try {
                    Method m = obj.getClass().getMethod(name);
                    Object res = m.invoke(obj);
                    if (res instanceof Boolean) return (Boolean) res;
                    if (res instanceof String) {
                        String s = ((String) res).toLowerCase(Locale.ROOT);
                        if (s.equals("true") || s.equals("false")) return Boolean.parseBoolean(s);
                    }
                    if (res instanceof Number) {
                        return ((Number) res).intValue() != 0;
                    }
                } catch (NoSuchMethodException ignored) {
                } catch (Exception ignored) {
                }
            }

            for (String name : candidateNames) {
                try {
                    Field f = obj.getClass().getDeclaredField(name);
                    f.setAccessible(true);
                    Object res = f.get(obj);
                    if (res instanceof Boolean) return (Boolean) res;
                    if (res instanceof String) {
                        String s = ((String) res).toLowerCase(Locale.ROOT);
                        if (s.equals("true") || s.equals("false")) return Boolean.parseBoolean(s);
                    }
                    if (res instanceof Number) {
                        return ((Number) res).intValue() != 0;
                    }
                } catch (NoSuchFieldException ignored) {
                } catch (Exception ignored) {
                }
            }

            return null;
        }

        private Double parseToDouble(Object res) {
            if (res == null) return null;
            if (res instanceof Number) {
                return ((Number) res).doubleValue();
            }
            if (res instanceof String) {
                String s = ((String) res).replaceAll("[^0-9.\\-]", "");
                if (s.isEmpty()) return null;
                try {
                    return Double.parseDouble(s);
                } catch (NumberFormatException ignored) {
                }
            }
            return null;
        }
    }
}
