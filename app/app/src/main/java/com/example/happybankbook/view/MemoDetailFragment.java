package com.example.happybankbook.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.happybankbook.MainActivity;
import com.example.happybankbook.R;
import com.example.happybankbook.adapter.MemoAdapter;
import com.example.happybankbook.adapter.MemoType;
import com.example.happybankbook.contract.ListContract;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.db.RoomDB;
import com.example.happybankbook.presenter.ListPresenter;
import com.example.happybankbook.presenterReturnInterface.GetReturnInt;

import java.util.ArrayList;

public class MemoDetailFragment extends Fragment implements ListContract.View,View.OnClickListener{

    //일정 시간 후 화살표 이미지 투명화 위한 Runnable
    private Runnable postRunnable =new Runnable(){
        @Override
        public void run() {
            imgForward.setColorFilter(Color.TRANSPARENT);
            imgBack.setColorFilter(Color.TRANSPARENT);
        }
    };

    private ViewPager2 viewPager;
    private ImageView imgForward;
    private ImageView imgBack;
    private TextView txtPrevious;

    private ListPresenter presenter;
    private MemoAdapter adapter;

    private Handler handler;

    private int count;
    private int fromDate;
    private int toDate;
    private int rowCnt;
    private int currentPosition;
    private int adapterPosition;
    private boolean isFirst=true;
    private float fontSize=12;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler=new Handler();

        //ConditionFragment 정렬 값 받기
        getParentFragmentManager().setFragmentResultListener(getResources().getString(R.string.memoRequestKey2), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                adapter.setCondition(false);

                fromDate=result.getInt(getResources().getString(R.string.fromDate));
                toDate=result.getInt(getResources().getString(R.string.toDate));
                count=result.getInt(getResources().getString(R.string.memoCount));
                boolean isNewSort=result.getBoolean(getResources().getString(R.string.memoSort));

                if(fromDate>toDate){
                    int temp=fromDate;
                    fromDate=toDate;
                    toDate=temp;
                }

                if(count==0){
                    presenter.setReturnInt(new GetReturnInt() {
                        @Override
                        public void getInt(int value) {
                            if(isNewSort){
                                presenter.getDataDesc(RoomDB.getInstance(getContext()).memoDao(),fromDate,toDate,value);
                            }else{
                                presenter.getDataAsc(RoomDB.getInstance(getContext()).memoDao(),fromDate,toDate,value);
                            }
                        }
                    });
                    presenter.getDataCount(RoomDB.getInstance(getContext()).memoDao());
                }else{
                    if(isNewSort){
                        presenter.getDataDesc(RoomDB.getInstance(getContext()).memoDao(),fromDate,toDate,count);
                    }else{
                        presenter.getDataAsc(RoomDB.getInstance(getContext()).memoDao(),fromDate,toDate,count);
                    }
                }

            }
        });
        //변경 font size 값
        getParentFragmentManager().setFragmentResultListener(getResources().getString(R.string.fontSize4), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                fontSize=result.getFloat(getResources().getString(R.string.fontSize));
                adapter.setFont(fontSize);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences= getActivity().getSharedPreferences(getResources().getString(R.string.memoDetailTextSetting),Context.MODE_PRIVATE);
        fontSize=preferences.getFloat(getResources().getString(R.string.fontSize),12);
        adapter.setFont(fontSize);
    }

    @Override
    public void onStop() {
        super.onStop();

        Bundle bundle=new Bundle();
        bundle.putBoolean(getResources().getString(R.string.memoSort),true);
        getParentFragmentManager().setFragmentResult(getResources().getString(R.string.viewpagerCondition), bundle);

        resetTextSetting();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.releaseView();
    }

    public void init(View view){
        viewPager=view.findViewById(R.id.viewPager2);
        imgForward=view.findViewById(R.id.imgForward);
        imgBack=view.findViewById(R.id.imgBack);
        txtPrevious=view.findViewById(R.id.memoDetailPrevious);

        imgForward.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        txtPrevious.setOnClickListener(this);

        presenter=new ListPresenter();
        presenter.setView(this);

        adapter=new MemoAdapter(getContext(), MemoType.VIEWPAGER, fontSize);
        viewPager.setAdapter(adapter);

        adapterPosition= adapter.getLocation();

        presenter.setReturnInt(new GetReturnInt() {
            @Override
            public void getInt(int value) {
                rowCnt=value;
            }
        });
        presenter.getDataCount(RoomDB.getInstance(getContext()).memoDao());

        changePage();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.imgForward){
            imgForward.setColorFilter(ContextCompat.getColor(getContext(),R.color.darkerGray));
            viewPager.setCurrentItem(currentPosition-1,false);
            handler.postDelayed(postRunnable,3000);
        }else if(v.getId()==R.id.imgBack){
            imgBack.setColorFilter(ContextCompat.getColor(getContext(),R.color.darkerGray));
            viewPager.setCurrentItem(currentPosition+1,false);
            handler.postDelayed(postRunnable,3000);
        }else if(v.getId()==R.id.memoDetailPrevious){
            ((MainActivity)getActivity()).removeFragment(this);
        }
    }

    @Override
    public void setItems(ArrayList<MemoData> items) {
        adapter.setItems(items);
        adapter.setCondition(true);
    }

    public void changePage(){
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //recyclerview position, viewpager position 더하여 클릭된 메모 데이터에서 슬라이드 하였을 때 다음 데이터 로딩 하기 위한 초기 값
                if(isFirst){
                    currentPosition=position+adapterPosition;
                }else{
                    currentPosition=position;
                }
                isFirst=false;

                if(currentPosition==0){
                    imgForward.setVisibility(View.INVISIBLE);
                    imgBack.setVisibility(View.VISIBLE);
                }else if(currentPosition==(rowCnt-1)){
                    imgForward.setVisibility(View.VISIBLE);
                    imgBack.setVisibility(View.INVISIBLE);
                }else{
                    imgForward.setVisibility(View.VISIBLE);
                    imgBack.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void resetTextSetting(){
        SharedPreferences preferences= getActivity().getSharedPreferences(getResources().getString(R.string.memoDetailTextSetting), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putFloat(getResources().getString(R.string.fontSize), fontSize);

        editor.apply();
    }

}

