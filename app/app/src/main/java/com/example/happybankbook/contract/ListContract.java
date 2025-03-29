package com.example.happybankbook.contract;

import android.content.Context;

import com.example.happybankbook.presenter.DataPresenter;
import com.example.happybankbook.view.BaseView;

public interface ListContract {

    interface View extends BaseView {

    }

    interface Presenter extends DataPresenter<View> {
        void getData();
        void getDataAsc(int from, int to, int cnt);
        void getDataDesc(int from, int to, int cnt);
        void getDataCount();
        void getSumPrice(Context context);
    }

}
