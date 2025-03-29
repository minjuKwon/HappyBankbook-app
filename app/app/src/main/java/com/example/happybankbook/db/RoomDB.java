package com.example.happybankbook.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities ={MemoData.class}, version = 2)
@TypeConverters(Converters.class)
public abstract class RoomDB extends RoomDatabase {
    public abstract MemoDao memoDao();
}