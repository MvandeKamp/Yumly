package com.mvandekamp.yumly.ui.inventory;

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
import com.mvandekamp.yumly.models.InventoryItem;
import com.mvandekamp.yumly.models.data.AppDatabase;
import com.mvandekamp.yumly.models.data.DatabaseClient;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;

    public InventoryFragment() {
        super(R.layout.fragment_inventory);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        AppDatabase db = DatabaseClient.getInstance(getContext()).getAppDatabase();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample data
        List<InventoryItem> inventoryItems = new ArrayList<>();
        inventoryItems.add(new InventoryItem("Milk", "2025-01-10", "2 liters"));
        inventoryItems.add(new InventoryItem("Eggs", "2025-01-15", "12 pieces"));
        inventoryItems.add(new InventoryItem("Bread", "2025-01-08", "1 loaf"));

        adapter = new InventoryAdapter(inventoryItems);
        recyclerView.setAdapter(adapter);
        return view;
    }
}