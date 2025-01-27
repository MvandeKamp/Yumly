package com.mvandekamp.yumly.ui.inventory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.mvandekamp.yumly.models.Inventory;
import com.mvandekamp.yumly.models.data.AppDatabase;
import com.mvandekamp.yumly.models.data.DatabaseClient;

import java.util.List;

public class InventoryViewModel extends ViewModel {
    private final LiveData<List<Inventory>> inventories;
    private final AppDatabase db;

    public InventoryViewModel(AppDatabase database) {
        this.db = database;
        this.inventories = db.inventoryDao().getAllInventories();
    }

    public LiveData<List<Inventory>> getInventories() {
        return inventories;
    }

    public void insertInventory(Inventory inventory) {
        new Thread(() -> db.inventoryDao().insert(inventory)).start();
    }

    public void updateInventory(Inventory inventory) {
        new Thread(() -> db.inventoryDao().update(inventory)).start();
    }
}