package com.example.happybankbook.presenter;

import android.content.Context;
import android.widget.Toast;

import com.example.happybankbook.R;
import com.example.happybankbook.contract.ListContract;
import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.presenterReturnInterface.IntResultCallback;
import com.example.happybankbook.presenterReturnInterface.LongResultCallback;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ListPresenter implements ListContract.Presenter {

    private ListContract.View view;
    private final CompositeDisposable disposable;
    private LongResultCallback longResultCallback;
    private IntResultCallback intResultCallback;

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
                            item->view.setItems((ArrayList<MemoData>)item)
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
                            item->view.setItems((ArrayList<MemoData>)item)
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
                    item->view.setItems((ArrayList<MemoData>)item)
                )
        );
    }

    @Override
    public void getDataCount(MemoDao memoDao) {
        disposable.add(
                Observable.just(memoDao)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        value-> intResultCallback.onIntResult(value.getRowCount())
                    )
        );
    }

    @Override
    public void getSumPrice(MemoDao memoDao, Context context) {
        disposable.add(
                Observable.just(memoDao)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        value-> longResultCallback.onLongResult(value.getTotalPrice()),
                        err->Toast.makeText(context,context.getResources().getText(R.string.totalPriceOver),Toast.LENGTH_LONG).show()
                    )
        );
    }

    public void setLongResultCallback(LongResultCallback callback){
        this.longResultCallback = callback;
    }

    public void setIntResultCallback(IntResultCallback callback){
        this.intResultCallback = callback;
    }

}