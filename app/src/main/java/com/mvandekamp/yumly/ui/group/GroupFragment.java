package com.mvandekamp.yumly.ui.group;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mvandekamp.yumly.R;
import com.mvandekamp.yumly.models.CookingGroup;
import com.mvandekamp.yumly.models.data.AppDatabase;
import com.mvandekamp.yumly.models.data.CookingGroupDao;
import com.mvandekamp.yumly.models.data.DatabaseClient;

import java.util.List;

public class GroupFragment extends Fragment {

    private RecyclerView groupRecyclerView;
    private GroupAdapter adapter;
    private CookingGroupDao cookingGroupDao;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout that contains the RecyclerView and other UI elements
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find and configure the RecyclerView
        groupRecyclerView = view.findViewById(R.id.groupRecyclerView);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize the adapter with a click listener for editing an existing group
        adapter = new GroupAdapter(groupId -> {
            // Show the dialog for editing the selected group
            GroupSelectedDialogFragment dialogFragment = GroupSelectedDialogFragment.newInstance(groupId);
            dialogFragment.show(getParentFragmentManager(), "GroupSelectedDialog");
        });
        groupRecyclerView.setAdapter(adapter);

        // Initialize the database and DAO
        AppDatabase db = DatabaseClient.getInstance(requireContext()).getAppDatabase();
        cookingGroupDao = db.cookingGroupDao();

        // Load groups from the database
        loadGroupsFromDatabase();

        // Setup the button for creating a new group
        Button createGroupButton = view.findViewById(R.id.processImageButton);
        createGroupButton.setOnClickListener(v -> {
            // Show the dialog for creating a new group (no groupId passed)
            GroupSelectedDialogFragment dialogFragment = GroupSelectedDialogFragment.newInstance(-1);
            dialogFragment.show(getParentFragmentManager(), "GroupSelectedDialog");
        });
    }

    public void loadGroupsFromDatabase() {
        new Thread(() -> {
            // Query all groups from the database
            List<CookingGroup> groups = cookingGroupDao.getAllGroups();

            // Update the adapter on the main thread
            requireActivity().runOnUiThread(() -> adapter.updateData(groups));
        }).start();
    }
}