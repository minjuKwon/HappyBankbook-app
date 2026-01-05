package com.example.happybankbook.presenter;

import com.example.happybankbook.db.UiMemoData;

import java.util.ArrayList;

public interface MemoDataListCallback {
    void onMemoDataListResult(ArrayList<UiMemoData>list);
}