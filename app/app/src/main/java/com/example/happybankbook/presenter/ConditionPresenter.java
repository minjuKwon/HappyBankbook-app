package com.example.happybankbook.presenter;

import com.example.happybankbook.contract.ConditionContract;
import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ConditionPresenter implements ConditionContract.Presenter {

    private ConditionContract.View view;
    private CompositeDisposable disposable;

    public ConditionPresenter(){
        this.disposable=new CompositeDisposable();
    }

    @Override
    public void setView(ConditionContract.View view) {
        this.view = view;
    }

    @Override
    public void releaseView() {
        disposable.clear();
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
        disposable.add(memoDao.searchDesc(from, to, cnt)
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
