package com.example.happybankbook.helper;

public class TestMemoData {
    private final String memo;
    private final String price;
    public TestMemoData(String memo, String price){
        this.memo=memo;
        this.price=price;
    }
    public String getMemo(){
        return memo;
    }
    public String getPrice(){
        return price;
    }
}