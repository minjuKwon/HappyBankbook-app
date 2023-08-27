package com.example.happybankbook.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface MemoDao {

    @Insert
    void insert(MemoData memo);

    @Query("SELECT * FROM MemoData_table ORDER BY num DESC")
    Flowable<List<MemoData>> getAll();

    @Query("SELECT * FROM MemoData_table WHERE content LIKE '%' || :keyword || '%'")
    Flowable<List<MemoData>> searchKeyword(String keyword);

    @Query("SELECT * FROM MemoData_table WHERE date BETWEEN :from AND :to ORDER BY num ASC LIMIT :cnt")
    Flowable<List<MemoData>> searchAsc(int from, int to, int cnt);

    @Query("SELECT * FROM MemoData_table WHERE date BETWEEN :from AND :to ORDER BY num DESC LIMIT :cnt")
    Flowable<List<MemoData>> searchDesc(int from, int to, int cnt);

    @Query("SELECT COUNT(idx) FROM MemoData_table")
    int getRowCount();

    @Query("SELECT COUNT(idx) FROM MemoData_table WHERE date<=:date")
    int getRangeCount(int date);

    @Query("UPDATE MemoData_table SET num=num+1 WHERE date>:date")
    void changeNum(int date);

}
