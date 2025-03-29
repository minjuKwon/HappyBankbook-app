package com.example.happybankbook.contract;

import com.example.happybankbook.presenter.BasePresenter;

public interface OutputContract {

    interface Presenter extends BasePresenter {
        void getConvertedFile(char split);
        void getConvertedPdf();
        void getDataCount();
    }
}
