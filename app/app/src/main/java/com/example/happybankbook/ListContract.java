package com.example.happybankbook;

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
