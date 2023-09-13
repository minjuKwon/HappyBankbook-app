package com.example.happybankbook.presenter;

import android.util.Log;

import com.example.happybankbook.GetReturnStringBuffer;
import com.example.happybankbook.contract.OutputContract;
import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;

import java.util.ArrayList;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OutputPresenter implements OutputContract.Presenter {

    private CompositeDisposable disposable;

    private GetReturnStringBuffer getReturnStringBuffer;

    public OutputPresenter(){
        this.disposable=new CompositeDisposable();
    }

    @Override
    public void releaseView() {
        disposable.clear();
    }

    @Override
    public void getDataToFile(MemoDao memoDao, char split) {
        StringBuffer stringBuffer=new StringBuffer();
        disposable.add(
                memoDao.getAll()
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                item->{
                                    for(MemoData data : (ArrayList<MemoData>)item){
                                        stringBuffer.append(data.getNum()).append(split)
                                                .append(data.getDate()).append(split)
                                                .append(data.getContent()).append(split)
                                                .append(data.getPrice()).append('\n');
                                    }
                                    getReturnStringBuffer.getStringBuffer(stringBuffer);
                                },
                                error->{Log.d("Memo","export file data error : "+error);}
                        )
        );
    }

    public void setGetReturnValue(GetReturnStringBuffer getReturnStringBuffer){
        this.getReturnStringBuffer = getReturnStringBuffer;
    }

}
