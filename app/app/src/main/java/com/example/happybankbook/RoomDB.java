package com.example.happybankbook;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities ={MemoData.class},version = 1)
public abstract class RoomDB extends RoomDatabase {
    
    private static RoomDB database;
    private static String DATABASE_NAME="database";

    public abstract MemoDao memoDao();

    public synchronized static RoomDB getInstance(Context context){
        if(database==null){
            database= Room.databaseBuilder(context.getApplicationContext(),RoomDB.class,DATABASE_NAME)
                    .build();
        }
        return database;
    }

}
