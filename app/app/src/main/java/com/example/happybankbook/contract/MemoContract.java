package com.example.happybankbook.contract;

import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.presenter.BasePresenter;

public interface MemoContract {

    interface Presenter extends BasePresenter {
        void insertMemo(MemoDao memoDao, MemoData memoData);
    }
}
