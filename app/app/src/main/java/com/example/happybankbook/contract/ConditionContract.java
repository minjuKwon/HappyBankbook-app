package com.example.happybankbook.contract;

import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.presenter.DataPresenter;
import com.example.happybankbook.view.BaseView;

public interface ConditionContract {

    interface View extends BaseView{

    }

    interface Presenter extends DataPresenter<View>{
        void getDataAsc(MemoDao memoDao, int from, int to, int cnt);
        void getDataDesc(MemoDao memoDao, int from, int to, int cnt);
    }

}
