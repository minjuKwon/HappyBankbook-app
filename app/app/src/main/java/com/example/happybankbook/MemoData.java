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
    public int idx;

    public int date;

    public int price=0;

    public String content;

    public Bitmap image;

    public void setDate(int date){this.date=date;}
    public void setPrice(int price){this.price=price;}
    public void setContent(String content){this.content=content;}
    public void setBitmap(Bitmap image){this.image=image;}

    public int getIdx() {
        return idx;
    }

    public int getDate() {
        return date;
    }

    public int getPrice() {
        return price;
    }

    public String getContent() {
        return content;
    }

    public Bitmap getImage() {
        return image;
    }
    
}
