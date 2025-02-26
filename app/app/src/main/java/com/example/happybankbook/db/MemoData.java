package com.example.happybankbook.db;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "MemoData_table")
public class MemoData extends BaseItem {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public Integer idx;
    public int num;
    public int date;
    public int price=0;
    public String content;
    public Bitmap image;

    public void setNum(int num) {
        this.num = num;
    }
    public void setDate(int date){this.date=date;}
    public void setPrice(int price){this.price=price;}
    public void setContent(String content){this.content=content;}
    public void setImage(Bitmap image){this.image=image;}

    public int getNum() {
        return num;
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