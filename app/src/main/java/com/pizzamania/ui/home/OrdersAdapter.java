package com.pizzamania.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pizzamania.R;
import com.pizzamania.data.model.Order;
import com.pizzamania.data.repo.OrderRepository;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private final List<Order> orders;

    public OrdersAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.txtOrderId.setText("Order #" + order.getOrderId());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        holder.txtOrderDate.setText(sdf.format(order.getCreatedAt()));

        // status text
        String status = order.getStatus();
        holder.txtOrderStatus.setText(status);

        // color coding by status
        switch (status) {
            case "Pending":
                holder.txtOrderStatus.setTextColor(Color.GRAY);
                break;
            case "Preparing":
                holder.txtOrderStatus.setTextColor(Color.parseColor("#FFA500")); // orange
                break;
            case "Out for Delivery":
                holder.txtOrderStatus.setTextColor(Color.BLUE);
                break;
            case "Delivered":
                holder.txtOrderStatus.setTextColor(Color.GREEN);
                break;
        }

        holder.txtOrderTotal.setText("Rs. " + (order.getTotalCents() / 100.0));

        // click → open details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), com.pizzamania.ui.home.OrderDetailsActivity.class);
            intent.putExtra("order_id", order.getOrderId());
            v.getContext().startActivity(intent);
        });

        // long press → simulate status change (for now)
        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenu().add("Set Pending");
            popup.getMenu().add("Set Preparing");
            popup.getMenu().add("Set Out for Delivery");
            popup.getMenu().add("Set Delivered");

            popup.setOnMenuItemClickListener(item -> {
                String newStatus = item.getTitle().toString();
                OrderRepository repo = new OrderRepository(v.getContext());
                repo.updateOrderStatus(order.getOrderId(), newStatus);

                // update UI immediately
                order.setStatus(newStatus);
                notifyItemChanged(position);
                return true;
            });

            popup.show();
            return true;
        });
    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtOrderDate, txtOrderStatus, txtOrderTotal;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txt_order_id);
            txtOrderDate = itemView.findViewById(R.id.txt_order_date);
            txtOrderStatus = itemView.findViewById(R.id.txt_order_status);
            txtOrderTotal = itemView.findViewById(R.id.txt_order_total);
        }
    }
}
