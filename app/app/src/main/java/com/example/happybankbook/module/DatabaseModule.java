package com.example.happybankbook.module;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.RoomDB;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    private static final String DATABASE_NAME="database";

    @Provides
    @Singleton
    public static RoomDB provideDatabase(@ApplicationContext Context context){
        return Room.databaseBuilder(context.getApplicationContext(),RoomDB.class,DATABASE_NAME)
                .addMigrations(MIGRATION_1_2)
                .build();
    }

    @Provides
    public static MemoDao provideMemoDao(RoomDB db){
        return db.memoDao();
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'MemoData_table' " +
                    "ADD COLUMN 'num' INTEGER NOT NULL DEFAULT 0");
        }
    };

}
