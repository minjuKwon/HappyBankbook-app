package com.example.happybankbook;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MemoData_table")
public class MemoData {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int idx=1;

    public int date;

    public int price=0;

    public String content;

    public Bitmap image;

}
