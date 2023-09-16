package com.example.happybankbook.presenter;

import com.example.happybankbook.contract.MemoContract;
import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.presenterReturnInterface.GetReturnInt;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MemoPresenter implements MemoContract.Presenter {

    private CompositeDisposable disposable;
    private GetReturnInt getReturnInt;

    public MemoPresenter(){this.disposable=new CompositeDisposable();}

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
                            item-> memoDao.insert(memoData)

                        )
        );
    }

    @Override
    public void getDataRange(MemoDao memoDao, int date) {
        disposable.add(
                Observable.just(memoDao)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                value->getReturnInt.getInt(value.getRangeCount(date))
                        )
        );
    }

    @Override
    public void changeNum(MemoDao memoDao, int date) {
        disposable.add(
                Observable.just(memoDao)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                value->value.changeNum(date)
                        )
        );
    }

    public void setReturnInt(GetReturnInt getReturnInt){
        this.getReturnInt=getReturnInt;
    }

}
