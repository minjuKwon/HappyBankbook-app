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

    public MemoPresenter(MemoDao memoDao){
        this.memoDao= memoDao;
        this.disposable=new CompositeDisposable();
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

}