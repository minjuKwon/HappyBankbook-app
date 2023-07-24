package com.example.happybankbook.contract;

import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;

public interface MemoContract {

    interface Presenter{
        void insertMemo(MemoDao memoDao, MemoData memoData);
        void releaseView();
    }
}
