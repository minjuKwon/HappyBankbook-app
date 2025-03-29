package com.example.happybankbook.contract;

import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.presenter.BasePresenter;

public interface MemoContract {

    interface Presenter extends BasePresenter {
        void insertMemo(MemoData memoData);
        void getDataRange(int date);
        void changeNum(int date);
    }
}
