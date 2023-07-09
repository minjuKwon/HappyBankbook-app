package com.example.happybankbook;

public interface MemoContract {

    interface Presenter{
        void insertMemo(MemoDao memoDao, MemoData memoData);
    }
}
