package com.example.happybankbook.presenter;

import com.example.happybankbook.db.MemoData;

import java.util.ArrayList;

public interface MemoDataListCallback {
    void onMemoDataListResult(ArrayList<MemoData>list);
}