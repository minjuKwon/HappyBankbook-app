package com.example.happybankbook.presenter;

import android.util.Log;

import com.example.happybankbook.presenterReturnInterface.GetReturnMemoDataList;
import com.example.happybankbook.presenterReturnInterface.GetReturnStringBuffer;
import com.example.happybankbook.contract.OutputContract;
import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.db.MemoData;

import java.util.ArrayList;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OutputPresenter implements OutputContract.Presenter {

    private final CompositeDisposable disposable;

    private GetReturnStringBuffer getReturnStringBuffer;
    private GetReturnMemoDataList getReturnMemoDataList;

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
                                error->Log.d("Memo","export file data error : "+error)
                        )
        );
    }

    @Override
    public void getDataToPdf(MemoDao memoDao) {
        disposable.add(
          memoDao.getAll()
                  .subscribeOn(Schedulers.io())
                  .subscribe(
                        item->getReturnMemoDataList.getMemoDataList((ArrayList<MemoData>)item)

                  )
        );
    }

    public void setGetReturnValue(GetReturnStringBuffer getReturnStringBuffer){
        this.getReturnStringBuffer = getReturnStringBuffer;
    }

    public void setReturnMemoDataList(GetReturnMemoDataList getReturnMemoDataList){
        this.getReturnMemoDataList=getReturnMemoDataList;
    }

}
