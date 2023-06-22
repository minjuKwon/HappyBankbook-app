package com.example.happybankbook;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MemoData_table")
public class MemoData {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int idx=1;

    public int price=0;

    @ColumnInfo(name="total_price")
    public int totalPrice;

    public String content;

    public String date;

}
