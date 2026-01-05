package com.example.happybankbook.view;

import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.db.UiMemoData;

import java.util.ArrayList;

public interface BaseView {
    void setItems(ArrayList<UiMemoData> items);
}
