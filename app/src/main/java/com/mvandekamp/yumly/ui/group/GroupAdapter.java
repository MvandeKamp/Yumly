package com.mvandekamp.yumly.ui.group;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mvandekamp.yumly.R;
import com.mvandekamp.yumly.models.CookingGroup;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private final List<CookingGroup> groupList;
    private final OnGroupClickListener onGroupClickListener;

    public interface OnGroupClickListener {
        void onGroupClick(int groupId);
    }

    public GroupAdapter(OnGroupClickListener onGroupClickListener) {
        this.groupList = new ArrayList<>(); // Initialize with an empty list
        this.onGroupClickListener = onGroupClickListener;
    }

    public void updateData(List<CookingGroup> newGroupList) {
        groupList.clear(); // Clear the old data
        groupList.addAll(newGroupList); // Add the new data
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        CookingGroup group = groupList.get(position);
        holder.groupNameTextView.setText(group.name);

        // Set click listener to pass the group ID
        holder.itemView.setOnClickListener(v -> onGroupClickListener.onGroupClick(group.id));
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {

        TextView groupNameTextView;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTextView = itemView.findViewById(R.id.groupNameTextView);
        }
    }
}