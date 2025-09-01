package com.pizzamania.ui.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pizzamania.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private final List<CartItemDisplay> items;

    public static class CartItemDisplay {
        public String name;
        public int qty;
        public double price;

        public CartItemDisplay(String name, int qty, double price) {
            this.name = name;
            this.qty = qty;
            this.price = price;
        }
    }

    public CartAdapter(Context context, List<CartItemDisplay> items) {
        this.context = context;
        this.items = items;
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
        holder.txtPrice.setText("Rs. " + item.price);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtQty, txtPrice;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_cart_name);
            txtQty = itemView.findViewById(R.id.txt_cart_qty);
            txtPrice = itemView.findViewById(R.id.txt_cart_price);
        }
    }
}
