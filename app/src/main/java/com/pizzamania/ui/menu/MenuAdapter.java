package com.pizzamania.ui.menu;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pizzamania.R;
import com.pizzamania.data.model.MenuItem;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private final Context context;
    private final List<MenuItem> items;

    public MenuAdapter(Context context, List<MenuItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = items.get(position);
        holder.txtName.setText(item.getName());
        holder.txtDesc.setText(item.getDescription());
        holder.txtPrice.setText("Rs. " + (item.getPriceCents() / 100.0));

        if (item.getImageUri() != null) {
            Glide.with(context)
                    .load(item.getImageUri())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.imgItem);
        }

        // CLICK → open ProductDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("menu_item", item);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView txtName, txtDesc, txtPrice;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.img_item);
            txtName = itemView.findViewById(R.id.txt_name);
            txtDesc = itemView.findViewById(R.id.txt_desc);
            txtPrice = itemView.findViewById(R.id.txt_price);
        }
    }
}
