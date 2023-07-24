package com.example.happybankbook.contract;

import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.presenter.BasePresenter;

import java.util.ArrayList;

public interface ListContract {

    interface View{
        void setItems(ArrayList<MemoData> items);
    }

    interface Presenter extends BasePresenter {
        void setView(ListContract.View view);
        void getData(MemoDao memoDao);
    }

}
