package com.example.happybankbook.db;

import android.graphics.Bitmap;

public class UiMemoData extends BaseItem{
    public int idx;
    public int num;
    public int date;
    public int price;
    public String content;
    public Bitmap image;

    public UiMemoData(
            int idx,
            int num,
            int date,
            int price,
            String content,
            Bitmap image
    ){
        this.idx=idx;
        this.num=num;
        this.date=date;
        this.price=price;
        this.content=content;
        this.image=image;
    }

    public int getIdx(){
        return idx;
    }
    public int getNum(){
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
