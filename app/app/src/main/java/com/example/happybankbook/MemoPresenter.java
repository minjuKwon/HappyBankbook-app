package com.example.happybankbook;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MemoPresenter implements MemoContract.Presenter{

    private CompositeDisposable disposable;

    MemoPresenter(){this.disposable=new CompositeDisposable();}

    @Override
    public void releaseView() {
        disposable.clear();
    }

    @Override
    public void insertMemo(MemoDao memoDao, MemoData memoData) {
        disposable.add(
                Observable.just(memoData)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                            item->{
                                memoDao.insert(memoData);
                            }
                        )
        );
    }

}
