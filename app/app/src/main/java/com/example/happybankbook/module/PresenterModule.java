package com.example.happybankbook.module;

import com.example.happybankbook.db.MemoDao;
import com.example.happybankbook.presenter.ListPresenter;
import com.example.happybankbook.presenter.MemoPresenter;
import com.example.happybankbook.presenter.OutputPresenter;
import com.example.happybankbook.presenter.SearchPresenter;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;

@Module
@InstallIn(FragmentComponent.class)
public class PresenterModule {

    @Provides
    public static ListPresenter provideListPresenter(MemoDao memoDao){
        return new ListPresenter(memoDao);
    }

    @Provides
    public static MemoPresenter provideMemoPresenter(MemoDao memoDao){
        return new MemoPresenter(memoDao);
    }

    @Provides
    public static OutputPresenter provideOutputPresenter(MemoDao memoDao){
        return new OutputPresenter(memoDao);
    }

    @Provides
    public static SearchPresenter provideSearchPresenter(MemoDao memoDao){
        return new SearchPresenter(memoDao);
    }

}
