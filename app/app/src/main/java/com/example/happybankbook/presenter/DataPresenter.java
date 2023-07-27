package com.example.happybankbook.presenter;

import com.example.happybankbook.contract.ListContract;

public interface DataPresenter<T> extends BasePresenter{
    void setView(T view);
}
