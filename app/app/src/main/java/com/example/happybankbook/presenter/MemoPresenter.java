package com.example.happybankbook.presenter;

import com.example.happybankbook.contract.MemoContract;
import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MemoPresenter implements MemoContract.Presenter {

    private final MemoDao memoDao;
    private final CompositeDisposable disposable;
    private IntResultCallback callback;

    public MemoPresenter(MemoDao memoDao){
        this.memoDao= memoDao;
        this.disposable=new CompositeDisposable();
    }

    public void setIntResultCallback(IntResultCallback callback){
        this.callback = callback;
    }

    @Override
    public void releaseView() {
        disposable.clear();
    }

    @Override
    public void insertMemo(MemoData memoData) {
        disposable.add(
                Observable.just(memoData)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                            item-> memoDao.insert(memoData)

                        )
        );
    }

    @Override
    public void getDataRange(int date) {
        disposable.add(
                Observable.just(memoDao)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                value-> callback.onIntResult(value.getRangeCount(date))
                        )
        );
    }

    @Override
    public void changeNum(int date) {
        disposable.add(
                Observable.just(memoDao)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                value->value.changeNum(date)
                        )
        );
    }

}