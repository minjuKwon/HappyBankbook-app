package com.example.happybankbook;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MemoDao {

    @Insert
    void insertMemo(MemoData memo);

    @Query("SELECT * FROM MemoData_table WHERE content LIKE '%' || :keyword || '%'")
    List<MemoData> searchKeyword(String keyword);

    @Query("SELECT * FROM MemoData_table WHERE date BETWEEN :from AND :to")
    List<MemoData> searchDuration(int from, int to);

    @Query("SELECT * FROM MemoData_table LIMIT :cnt")
    List<MemoData> searchCount(int cnt);

    @Query("SELECT * FROM MemoData_table ORDER BY date ASC")
    List<MemoData> sortDateAsc();

    @Query("SELECT * FROM MemoData_table ORDER BY date DESC")
    List<MemoData> sortDateDesc();

}
