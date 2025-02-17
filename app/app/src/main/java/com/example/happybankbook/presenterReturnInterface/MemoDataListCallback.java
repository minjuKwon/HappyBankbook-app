package com.example.happybankbook.presenterReturnInterface;

import com.example.happybankbook.db.MemoData;

import java.util.ArrayList;

public interface MemoDataListCallback {
    void onMemoDataListResult(ArrayList<MemoData>list);
}