package com.example.happybankbook.contract;

import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.presenter.BasePresenter;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

public interface OutputContract {

    interface Presenter extends BasePresenter {
        void getDataToFile(MemoDao memoDao, char split);
    }
}
