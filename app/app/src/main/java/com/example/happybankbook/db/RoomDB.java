package com.example.happybankbook.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities ={MemoData.class}, version = 2)
@TypeConverters(Converters.class)
public abstract class RoomDB extends RoomDatabase {
    
    private static RoomDB database;
    private static String DATABASE_NAME="database";

    public abstract MemoDao memoDao();

    public synchronized static RoomDB getInstance(Context context){
        if(database==null){
            database= Room.databaseBuilder(context.getApplicationContext(),RoomDB.class,DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return database;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'MemoData_table' ADD COLUMN 'num' INTEGER NOT NULL DEFAULT 0");
        }
    };

}
