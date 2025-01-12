package com.mvandekamp.yumly.models.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mvandekamp.yumly.models.CookingGroup;

import java.util.List;

@Dao
public interface CookingGroupDao {
    @Insert
    void insert(CookingGroup group);

    @Update
    void update(CookingGroup group);

    @Delete
    void delete(CookingGroup group);

    @Query("SELECT * FROM CookingGroup WHERE id = :id")
    CookingGroup getGroupById(int id);

    @Query("SELECT * FROM CookingGroup")
    List<CookingGroup> getAllGroups();
}
