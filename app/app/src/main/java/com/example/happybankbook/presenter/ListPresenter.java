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

    private final MemoDao memoDao;
    private final CompositeDisposable disposable;
    private LongResultCallback longResultCallback;
    private IntResultCallback intResultCallback;
    private ListContract.View view;


    public ListPresenter(MemoDao memoDao){
        this.memoDao= memoDao;
        this.disposable=new CompositeDisposable();
    }

    public void setLongResultCallback(LongResultCallback callback){
        this.longResultCallback = callback;
    }

    public void setIntResultCallback(IntResultCallback callback){
        this.intResultCallback = callback;
    }

    public void setView(ListContract.View view) {
        this.view = view;
    }

    @Override
    public void releaseView() {
        disposable.clear();
    }

    @Override
    public void getData() {
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
    public void getDataAsc(int from, int to, int cnt) {
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
    public void getDataDesc(int from, int to, int cnt) {
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
    public void getDataCount() {
        disposable.add(
                Observable.just(memoDao)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                value-> intResultCallback.onIntResult(value.getRowCount())
                        )
        );
    }

    @Override
    public void getSumPrice(Context context) {
        disposable.add(
                Observable.just(memoDao)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                value-> longResultCallback.onLongResult(value.getTotalPrice()),
                                err->Toast.makeText(
                                        context,
                                        context.getResources().getText(R.string.totalPriceOver),
                                        Toast.LENGTH_LONG
                                ).show()
                        )
        );
    }

}