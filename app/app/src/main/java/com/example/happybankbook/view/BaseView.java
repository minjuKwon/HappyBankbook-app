package com.example.happybankbook.view;

import com.example.happybankbook.db.MemoData;

import java.util.ArrayList;

public interface BaseView {
    void setItems(ArrayList<MemoData> items);
}
