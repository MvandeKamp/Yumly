package com.mvandekamp.yumly.ui.inventory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mvandekamp.yumly.models.data.AppDatabase;

public class InventoryViewModelFactory implements ViewModelProvider.Factory {
    private final AppDatabase database;

    public InventoryViewModelFactory(AppDatabase database) {
        this.database = database;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(InventoryViewModel.class)) {
            return (T) new InventoryViewModel(database);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}