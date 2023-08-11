package com.example.happybankbook.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface MemoDao {

    @Insert
    void insert(MemoData memo);

    @Query("SELECT * FROM MemoData_table ORDER BY idx DESC")
    Flowable<List<MemoData>> getAll();

    @Query("SELECT * FROM MemoData_table WHERE content LIKE '%' || :keyword || '%'")
    Flowable<List<MemoData>> searchKeyword(String keyword);

    @Query("SELECT * FROM MemoData_table WHERE date BETWEEN :from AND :to ORDER BY idx ASC LIMIT :cnt")
    Flowable<List<MemoData>> searchAsc(int from, int to, int cnt);

    @Query("SELECT * FROM MemoData_table WHERE date BETWEEN :from AND :to ORDER BY idx DESC LIMIT :cnt")
    Flowable<List<MemoData>> searchDesc(int from, int to, int cnt);

}
