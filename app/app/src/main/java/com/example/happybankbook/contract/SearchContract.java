package com.example.happybankbook.contract;

import com.example.happybankbook.presenter.DataPresenter;
import com.example.happybankbook.view.BaseView;

public interface SearchContract {

    interface View extends BaseView {

    }

    interface Presenter extends DataPresenter<View> {
        void getData(String keyword);
    }
}
