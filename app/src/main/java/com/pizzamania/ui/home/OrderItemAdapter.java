package com.pizzamania.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pizzamania.R;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ItemViewHolder> {

    private List<OrderItemWithName> items;

    public OrderItemAdapter(List<OrderItemWithName> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        OrderItemWithName item = items.get(position);
        holder.txtName.setText(item.getName());
        holder.txtQty.setText("×" + item.getQty());
        holder.txtPrice.setText("Rs. " + (item.getUnitPriceCents() / 100.0));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtQty, txtPrice;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_item_name);
            txtQty = itemView.findViewById(R.id.txt_item_qty);
            txtPrice = itemView.findViewById(R.id.txt_item_price);
        }
    }
}
