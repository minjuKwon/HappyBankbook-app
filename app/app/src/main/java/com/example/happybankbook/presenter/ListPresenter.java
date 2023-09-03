package com.example.happybankbook.presenter;

import com.example.happybankbook.contract.ListContract;
import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ListPresenter implements ListContract.Presenter {

    private ListContract.View view;
    private CompositeDisposable disposable;
    private int result;
    private long price;

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

    @Override
    public void getDataAsc(MemoDao memoDao, int from, int to, int cnt) {
        disposable.add(
                memoDao.searchAsc(from, to, cnt)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                item->{
                                    view.setItems((ArrayList<MemoData>)item);
                                }
                        )
        );
    }

    @Override
    public void getDataDesc(MemoDao memoDao, int from, int to, int cnt) {
        disposable.add(
                memoDao.searchDesc(from, to, cnt)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        item->{
                            view.setItems((ArrayList<MemoData>)item);
                        }
                )
        );
    }

    @Override
    public int getDataCount(MemoDao memoDao) {
        disposable.add(
                Observable.just(memoDao)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                            value->{result=value.getRowCount();}
                        )
        );
        try{Thread.sleep(500);}catch (InterruptedException  e){}
        return result;
    }

    @Override
    public long getSumPrice(MemoDao memoDao) {
        disposable.add(
                Observable.just(memoDao)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                value->{price=value.getTotalPrice();}
                        )
        );
        try{Thread.sleep(500);}catch (InterruptedException  e){}
        return price;
    }
}
