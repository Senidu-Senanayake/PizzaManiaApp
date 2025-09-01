package com.pizzamania.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pizzamania.R;
import com.pizzamania.data.model.Branch;
import com.pizzamania.data.repo.BranchRepository;

import java.util.List;

public class BranchesFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_branches, container, false);

        recyclerView = root.findViewById(R.id.branchesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        BranchRepository repo = new BranchRepository(requireContext());
        List<Branch> branches = repo.getAllBranches();

        BranchAdapter adapter = new BranchAdapter(branches);
        recyclerView.setAdapter(adapter);

        return root;
    }
}
