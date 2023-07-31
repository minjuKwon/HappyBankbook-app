package com.example.happybankbook.presenter;

import com.example.happybankbook.contract.ListContract;
import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ListPresenter implements ListContract.Presenter {

    private ListContract.View view;
    private CompositeDisposable disposable;

    public ListPresenter(){this.disposable=new CompositeDisposable();}

    public void setView(ListContract.View view) {
        this.view = view;
    }

    @Override
    public void releaseView() {
        disposable.clear();
    }

    @Override
    public void getData(MemoDao memoDao) {
        disposable.add(
                memoDao.getAll()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            item->{
                                view.setItems((ArrayList<MemoData>)item);
                            }
                        )
        );
    }

}
