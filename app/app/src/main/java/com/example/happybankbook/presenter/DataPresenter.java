package com.example.happybankbook.presenter;

public interface DataPresenter<T> extends BasePresenter{
    void setView(T view);
}
