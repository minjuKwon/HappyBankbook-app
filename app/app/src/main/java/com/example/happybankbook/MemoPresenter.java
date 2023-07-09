package com.example.happybankbook;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MemoPresenter implements MemoContract.Presenter{

    @Override
    public void insertMemo(MemoDao memoDao, MemoData memoData) {

        Observable.just(memoData)
                .subscribeOn(Schedulers.io())
                .subscribe(
                  item->{
                      memoDao.insert(memoData);
                  }
                );

    }

}
