package com.example.happybankbook.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.happybankbook.R;
import com.example.happybankbook.adapter.MemoAdapter;
import com.example.happybankbook.adapter.MemoType;
import com.example.happybankbook.contract.ListContract;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.db.RoomDB;
import com.example.happybankbook.presenter.ListPresenter;

import java.util.ArrayList;

public class MemoDetailFragment extends Fragment implements ListContract.View  {

    private ViewPager2 viewPager;
    private ImageView imgForward;
    private ImageView imgBack;

    private ListPresenter presenter;
    private MemoAdapter adapter;

    static private int position;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ConditionFragment 정렬 값 받기
        getParentFragmentManager().setFragmentResultListener("memoRequestKey2", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                int fromDate=result.getInt("fromDate");
                int toDate=result.getInt("toDate");
                int count=result.getInt("count");
                boolean isNewSort=result.getBoolean("sort");
                Log.d("Memo","Detail get sort condition");

                adapter.setSort(isNewSort);

                if(fromDate>toDate){
                    int temp=fromDate;
                    fromDate=toDate;
                    toDate=temp;
                }

                if(count==0){
                    count=presenter.getDataCount(RoomDB.getInstance(getContext()).memoDao());
                }

                if(isNewSort){
                    presenter.getDataDesc(RoomDB.getInstance(getContext()).memoDao(),fromDate,toDate,count);
                }else{
                    presenter.getDataAsc(RoomDB.getInstance(getContext()).memoDao(),fromDate,toDate,count);
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_memo_detail, container, false);
        init(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.releaseView();
    }

    public void init(View view){
        viewPager=view.findViewById(R.id.viewPager2);
        imgForward=view.findViewById(R.id.imgForward);

        presenter=new ListPresenter();
        presenter.setView(this);

        adapter=new MemoAdapter(getContext(), MemoType.VIEWPAGER);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void setItems(ArrayList<MemoData> items) {
        adapter.setItems(items);
    }

}