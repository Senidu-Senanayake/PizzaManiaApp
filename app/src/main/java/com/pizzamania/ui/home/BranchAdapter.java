package com.pizzamania.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pizzamania.R;
import com.pizzamania.data.model.Branch;

import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {
    private List<Branch> branches;

    public BranchAdapter(List<Branch> branches) {
        this.branches = branches;
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
        Branch branch = branches.get(position);
        holder.txtName.setText(branch.getName());
        holder.txtAddress.setText(branch.getAddress());
        holder.txtPhone.setText("📞 " + branch.getPhone());

        // Open Google Maps when clicking the item
        holder.itemView.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:" + branch.getLat() + "," + branch.getLng() +
                    "?q=" + Uri.encode(branch.getName()));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            v.getContext().startActivity(mapIntent);
        });

        // Dial phone when clicking phone text
        holder.txtPhone.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + branch.getPhone()));
            v.getContext().startActivity(callIntent);
        });
    }

    @Override
    public int getItemCount() {
        return branches.size();
    }

    static class BranchViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtAddress, txtPhone;
        public BranchViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtBranchName);
            txtAddress = itemView.findViewById(R.id.txtBranchAddress);
            txtPhone = itemView.findViewById(R.id.txtBranchPhone);
        }
    }
}
