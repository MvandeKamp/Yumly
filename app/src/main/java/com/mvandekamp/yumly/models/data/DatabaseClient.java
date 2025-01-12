package com.mvandekamp.yumly.models.data;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {
    private static DatabaseClient instance;
    private final AppDatabase appDatabase;

    private DatabaseClient(Context context) {
        // Build the database instance
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "yumly_database")
                .fallbackToDestructiveMigration() // Handle migrations (optional)
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}