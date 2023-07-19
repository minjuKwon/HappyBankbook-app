package com.example.happybankbook;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ListPresenter implements ListContract.Presenter{

    private ListContract.View view;
    private CompositeDisposable disposable;

    ListPresenter(){this.disposable=new CompositeDisposable();}

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
                        .subscribe(
                            item->{
                                view.setItems((ArrayList<MemoData>)item);
                            }
                        )
        );
    }

}
