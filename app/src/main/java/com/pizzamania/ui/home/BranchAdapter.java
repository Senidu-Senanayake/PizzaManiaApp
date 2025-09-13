package com.pizzamania.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pizzamania.R;
import com.pizzamania.data.model.Branch;

import java.util.ArrayList;
import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {
    private List<Branch> branches;
    private List<Branch> branchesFiltered;
    private Context context;
    private OnBranchClickListener listener;

    public interface OnBranchClickListener {
        void onBranchClick(Branch branch);
        void onDirectionsClick(Branch branch);
        void onCallClick(Branch branch);
    }

    public BranchAdapter(List<Branch> branches, Context context) {
        this.branches = branches;
        this.branchesFiltered = new ArrayList<>(branches);
        this.context = context;
    }

    public void setOnBranchClickListener(OnBranchClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_branch, parent, false);
        return new BranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchViewHolder holder, int position) {
        Branch branch = branchesFiltered.get(position);

        // Bind data to views
        holder.txtName.setText(branch.getName());
        holder.txtAddress.setText(branch.getAddress());
        holder.txtPhone.setText(branch.getFormattedPhone());
        holder.txtWorkingHours.setText("⏰ " + branch.getWorkingHours());

        // Set branch status indicator
        if (branch.isActive()) {
            holder.statusIndicator.setImageResource(R.drawable.ic_circle_green);
            holder.statusText.setText("Open");
            holder.statusText.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.statusIndicator.setImageResource(R.drawable.ic_circle_red);
            holder.statusText.setText("Closed");
            holder.statusText.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBranchClick(branch);
            }
        });

        // Open Google Maps when clicking directions button
        holder.btnDirections.setOnClickListener(v -> {
            try {
                Uri gmmIntentUri = Uri.parse("geo:" + branch.getLat() + "," + branch.getLng() +
                        "?q=" + Uri.encode(branch.getName() + ", " + branch.getAddress()));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                } else {
                    // Fallback to web browser if Maps app is not available
                    Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" +
                            branch.getLat() + "," + branch.getLng());
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
                    context.startActivity(webIntent);
                }

                if (listener != null) {
                    listener.onDirectionsClick(branch);
                }
            } catch (Exception e) {
                Toast.makeText(context, "Unable to open maps", Toast.LENGTH_SHORT).show();
            }
        });

        // Dial phone when clicking phone button
        holder.btnCall.setOnClickListener(v -> {
            try {
                Intent callIntent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + branch.getPhone()));
                context.startActivity(callIntent);

                if (listener != null) {
                    listener.onCallClick(branch);
                }
            } catch (Exception e) {
                Toast.makeText(context, "Unable to make call", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return branchesFiltered.size();
    }

    // Filter method for search functionality
    public void filter(String searchText) {
        branchesFiltered.clear();
        if (searchText.isEmpty()) {
            branchesFiltered.addAll(branches);
        } else {
            String filterPattern = searchText.toLowerCase().trim();
            for (Branch branch : branches) {
                if (branch.getName().toLowerCase().contains(filterPattern) ||
                        branch.getAddress().toLowerCase().contains(filterPattern) ||
                        branch.getPhone().contains(filterPattern)) {
                    branchesFiltered.add(branch);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Update data method
    public void updateBranches(List<Branch> newBranches) {
        this.branches.clear();
        this.branches.addAll(newBranches);
        this.branchesFiltered.clear();
        this.branchesFiltered.addAll(newBranches);
        notifyDataSetChanged();
    }

    static class BranchViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtAddress, txtPhone, txtWorkingHours, statusText;
        ImageView statusIndicator, btnDirections, btnCall;

        public BranchViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtBranchName);
            txtAddress = itemView.findViewById(R.id.txtBranchAddress);
            txtPhone = itemView.findViewById(R.id.txtBranchPhone);
            txtWorkingHours = itemView.findViewById(R.id.txtWorkingHours);
            statusText = itemView.findViewById(R.id.txtBranchStatus);
            statusIndicator = itemView.findViewById(R.id.imgStatusIndicator);
            btnDirections = itemView.findViewById(R.id.btnDirections);
            btnCall = itemView.findViewById(R.id.btnCall);
        }
    }
}