package com.example.happybankbook.contract;

import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;

import java.util.ArrayList;

public interface ListContract {

    interface View{
        void setItems(ArrayList<MemoData> items);
    }

    interface Presenter{
        void setView(ListContract.View view);
        void releaseView();
        void getData(MemoDao memoDao);
    }

}
