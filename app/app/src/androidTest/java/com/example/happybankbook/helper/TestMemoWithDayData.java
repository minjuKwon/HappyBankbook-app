package com.example.happybankbook.helper;

public class TestMemoWithDayData extends TestMemoData {
    private final int day;
    public TestMemoWithDayData(String memo, String price, int day) {
        super(memo, price);
        this.day = day;
    }
    public int getDay(){
        return day;
    }
}