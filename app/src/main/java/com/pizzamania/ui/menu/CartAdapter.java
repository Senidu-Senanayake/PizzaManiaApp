package com.pizzamania.ui.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pizzamania.R;
import com.pizzamania.data.repo.CartRepository;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private final List<CartItemDisplay> items;
    private final CartRepository cartRepo;
    private final OnCartChangedListener listener;

    public interface OnCartChangedListener {
        void onCartUpdated(double newTotal);
    }

    public static class CartItemDisplay {
        public int itemId;
        public String name;
        public int qty;
        public double price;

        public CartItemDisplay(int itemId, String name, int qty, double price) {
            this.itemId = itemId;
            this.name = name;
            this.qty = qty;
            this.price = price;
        }
    }

    public CartAdapter(Context context, List<CartItemDisplay> items, OnCartChangedListener listener) {
        this.context = context;
        this.items = items;
        this.cartRepo = new CartRepository(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItemDisplay item = items.get(position);

        holder.txtName.setText(item.name);
        holder.txtQty.setText("x" + item.qty);
        holder.txtPrice.setText("Rs. " + String.format("%.2f", item.price));

        holder.btnRemove.setOnClickListener(v -> {
            int userId = context.getSharedPreferences("PizzaManiaPrefs", Context.MODE_PRIVATE)
                    .getInt("logged_in_user", -1);

            // remove from DB
            cartRepo.removeFromCart(userId, item.itemId);

            // remove from list
            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size());

            // recalc total
            double newTotal = 0;
            for (CartItemDisplay i : items) {
                newTotal += i.price;
            }

            if (listener != null) {
                listener.onCartUpdated(newTotal);
            }

            Toast.makeText(context, item.name + " removed from cart", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtQty, txtPrice;
        ImageView btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_cart_name);
            txtQty = itemView.findViewById(R.id.txt_cart_qty);
            txtPrice = itemView.findViewById(R.id.txt_cart_price);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}
