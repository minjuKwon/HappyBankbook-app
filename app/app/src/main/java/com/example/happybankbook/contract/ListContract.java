package com.example.happybankbook.contract;

import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.presenter.BasePresenter;
import com.example.happybankbook.presenter.DataPresenter;
import com.example.happybankbook.view.BaseView;

import java.util.ArrayList;

public interface ListContract {

    interface View extends BaseView {

    }

    interface Presenter extends DataPresenter<View> {
        void getData(MemoDao memoDao);
    }

}
