package com.pizzamania.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

        holder.txtSize.setText("Size: " + (item.getSize() != null ? item.getSize() : "Default"));
        holder.txtExtras.setText("Extras: " + (item.getExtras() != null ? item.getExtras() : "None"));

        if (item.getImageUri() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUri())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.imgItem);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtQty, txtPrice, txtSize, txtExtras;
        ImageView imgItem;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.img_item);
            txtName = itemView.findViewById(R.id.txt_item_name);
            txtQty = itemView.findViewById(R.id.txt_item_qty);
            txtPrice = itemView.findViewById(R.id.txt_item_price);
            txtSize = itemView.findViewById(R.id.txt_item_size);
            txtExtras = itemView.findViewById(R.id.txt_item_extras);
        }
    }
}
