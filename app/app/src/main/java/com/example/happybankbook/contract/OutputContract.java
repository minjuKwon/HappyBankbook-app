package com.example.happybankbook.contract;

import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.presenter.BasePresenter;

public interface OutputContract {

    interface Presenter extends BasePresenter {
        void getDataToFile(MemoDao memoDao, char split);
        void getDataToPdf(MemoDao memoDao);
    }
}
