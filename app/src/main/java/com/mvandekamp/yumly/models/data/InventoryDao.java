package com.mvandekamp.yumly.models.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mvandekamp.yumly.models.Inventory;

import java.util.List;

@Dao
public interface InventoryDao {
    @Insert
    void insert(Inventory inventory);

    @Update
    void update(Inventory inventory);

    @Delete
    void delete(Inventory inventory);

    @Query("SELECT * FROM Inventory WHERE id = :id")
    Inventory getInventoryById(int id);

    @Query("SELECT * FROM Inventory")
    List<Inventory> getAllInventories();
}
