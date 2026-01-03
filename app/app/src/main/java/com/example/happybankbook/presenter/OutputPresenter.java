package com.example.happybankbook.presenter;

import android.util.Log;

import com.example.happybankbook.contract.OutputContract;
import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OutputPresenter implements OutputContract.Presenter {

    private final MemoDao memoDao;
    private final CompositeDisposable disposable;

    private StringBufferResultCallback stringBufferResultCallback;
    private MemoDataListCallback memoDataListCallback;
    private IntResultCallback intResultCallback;

    public OutputPresenter(MemoDao memoDao){
        this.memoDao= memoDao;
        this.disposable=new CompositeDisposable();
    }

    public void setStringBufferResultCallback(StringBufferResultCallback callback){
        this.stringBufferResultCallback = callback;
    }

    public void setMemoDataListCallback(MemoDataListCallback callback){
        this.memoDataListCallback=callback;
    }

    public void setIntResultCallback(IntResultCallback callback){
        this.intResultCallback = callback;
    }

    @Override
    public void releaseView() {
        disposable.clear();
    }

    @Override
    public void getConvertedFile(char split) {
        StringBuffer stringBuffer=new StringBuffer();
        disposable.add(
                memoDao.getAll()
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                item->{
                                    for(int i=0;i<item.size();i++){
                                        MemoData data= item.get(i);
                                        stringBuffer.append(item.size()-i).append(split)
                                                .append(data.getDate()).append(split)
                                                .append(data.getContent()).append(split)
                                                .append(data.getPrice()).append('\n');
                                    }
                                    stringBufferResultCallback.onStringBufferResult(stringBuffer);
                                },
                                error->Log.d("Memo","export file data error : "+error)
                        )
        );
    }

    @Override
    public void getConvertedPdf() {
        disposable.add(
                memoDao.getAll()
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                item->memoDataListCallback
                                        .onMemoDataListResult((ArrayList<MemoData>)item)

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

}