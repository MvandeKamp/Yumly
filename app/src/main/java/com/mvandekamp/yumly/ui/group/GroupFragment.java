package com.mvandekamp.yumly.ui.group;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mvandekamp.yumly.R;
import com.mvandekamp.yumly.models.CookingGroup;
import com.mvandekamp.yumly.models.data.CookingGroupDao;
import com.mvandekamp.yumly.models.data.AppDatabase;
import com.mvandekamp.yumly.models.data.DatabaseClient;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {

    private RecyclerView groupRecyclerView;
    private GroupAdapter adapter;
    private CookingGroupDao cookingGroupDao;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupRecyclerView = view.findViewById(R.id.groupRecyclerView);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize the database and DAO
        AppDatabase db = DatabaseClient.getInstance(requireContext()).getAppDatabase();
        cookingGroupDao = db.cookingGroupDao();

        // Load groups from the database
        loadGroupsFromDatabase();
    }

    private void loadGroupsFromDatabase() {
        new Thread(() -> {
            // Query all groups from the database
            List<CookingGroup> groups = cookingGroupDao.getAllGroups();

            // Update the RecyclerView on the main thread
            requireActivity().runOnUiThread(() -> {
                adapter = new GroupAdapter(groups, groupId -> {
                    // Navigate to GroupSelectedFragment when a group is clicked
                    Bundle bundle = new Bundle();
                    bundle.putInt("groupId", groupId);

                    GroupSelectedFragment fragment = new GroupSelectedFragment();
                    fragment.setArguments(bundle);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.groupRecyclerView, fragment) // Replace with your container ID
                            .addToBackStack(null)
                            .commit();
                });
                groupRecyclerView.setAdapter(adapter);
            });
        }).start();
    }
}