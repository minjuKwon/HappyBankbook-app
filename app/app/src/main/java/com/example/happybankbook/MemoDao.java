package com.example.happybankbook;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface MemoDao {

    void add(MemoData memo);

}
