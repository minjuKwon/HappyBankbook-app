package com.example.happybankbook.presenter;

import com.example.happybankbook.contract.SearchContract;
import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchPresenter implements SearchContract.Presenter {

    private SearchContract.View view;
    private final CompositeDisposable disposable;

    public SearchPresenter(){this.disposable=new CompositeDisposable();}

    public void setView(SearchContract.View view) {
        this.view = view;
    }

    @Override
    public void releaseView() {
        disposable.clear();
    }

    @Override
    public void getData(MemoDao memoDao,String keyword) {
        disposable.add(
                memoDao.searchKeyword(keyword)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                item-> view.setItems((ArrayList<MemoData>)item)

                        )
        );
    }

}
