package com.example.happybankbook.presenter;

import android.util.Log;

import com.example.happybankbook.presenterReturnInterface.MemoDataListCallback;
import com.example.happybankbook.presenterReturnInterface.StringBufferResultCallback;
import com.example.happybankbook.contract.OutputContract;
import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;

import java.util.ArrayList;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OutputPresenter implements OutputContract.Presenter {

    private final CompositeDisposable disposable;

    private StringBufferResultCallback stringBufferResultCallback;
    private MemoDataListCallback memoDataListCallback;

    public OutputPresenter(){
        this.disposable=new CompositeDisposable();
    }

    public void setStringBufferResultCallback(StringBufferResultCallback callback){
        this.stringBufferResultCallback = callback;
    }

    public void setMemoDataListCallback(MemoDataListCallback callback){
        this.memoDataListCallback=callback;
    }

    @Override
    public void releaseView() {
        disposable.clear();
    }

    @Override
    public void getConvertedFile(MemoDao memoDao, char split) {
        StringBuffer stringBuffer=new StringBuffer();
        disposable.add(
                memoDao.getAll()
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                item->{
                                    for(MemoData data : item){
                                        stringBuffer.append(data.getNum()).append(split)
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
    public void getConvertedPdf(MemoDao memoDao) {
        disposable.add(
                memoDao.getAll()
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                item->memoDataListCallback
                                        .onMemoDataListResult((ArrayList<MemoData>)item)

                        )
        );
    }

}