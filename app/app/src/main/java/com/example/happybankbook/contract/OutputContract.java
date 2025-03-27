package com.example.happybankbook.contract;

import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.presenter.BasePresenter;

public interface OutputContract {

    interface Presenter extends BasePresenter {
        void getConvertedFile(MemoDao memoDao, char split);
        void getConvertedPdf(MemoDao memoDao);
        void getDataCount(MemoDao memoDao);
    }
}
