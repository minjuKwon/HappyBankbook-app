package com.example.happybankbook.contract;

import android.content.Context;

import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.presenter.DataPresenter;
import com.example.happybankbook.view.BaseView;

public interface ListContract {

    interface View extends BaseView {

    }

    interface Presenter extends DataPresenter<View> {
        void getData(MemoDao memoDao);
        void getDataAsc(MemoDao memoDao, int from, int to, int cnt);
        void getDataDesc(MemoDao memoDao, int from, int to, int cnt);
        void getDataCount(MemoDao memoDao);
        void getSumPrice(MemoDao memoDao, Context context);
    }

}
