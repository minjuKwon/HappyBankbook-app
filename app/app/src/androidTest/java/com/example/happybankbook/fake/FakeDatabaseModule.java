package com.example.happybankbook.fake;

import android.content.Context;

import androidx.room.Room;

import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.RoomDB;
import com.example.happybankbook.module.DatabaseModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;

@Module
@TestInstallIn(
        components = SingletonComponent.class,
        replaces = DatabaseModule.class
)
public class FakeDatabaseModule {
    @Provides
    @Singleton
    public static RoomDB provideDatabase(@ApplicationContext Context context){
        return Room.inMemoryDatabaseBuilder(
                context,
                RoomDB.class
        ).allowMainThreadQueries().build();
    }

    @Provides
    public static MemoDao provideMemoDao(RoomDB db){
        return db.memoDao();
    }
}
