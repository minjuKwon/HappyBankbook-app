package com.example.happybankbook.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.happybankbook.MainActivity;
import com.example.happybankbook.adapter.MemoAdapter;
import com.example.happybankbook.R;
import com.example.happybankbook.adapter.MemoType;
import com.example.happybankbook.contract.ListContract;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.db.RoomDB;
import com.example.happybankbook.presenter.ListPresenter;
import com.example.happybankbook.presenterReturnInterface.GetReturnInt;
import com.example.happybankbook.presenterReturnInterface.GetReturnLong;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ListFragment extends Fragment implements View.OnClickListener, ListContract.View{

    private TextView txtSearch;
    private TextView txtCondition;
    private TextView txtPrice;
    private RecyclerView recyclerView;

    private ListPresenter presenter;
    private MemoAdapter adapter;

    private int count;
    private int fromDate;
    private int toDate;
    private int addFragment=1;
    private int textLine=2;
    private float fontSize=15;
    private boolean isNewSort;
    private boolean textEllipsize=true;
    private boolean isStop;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences= getActivity().getSharedPreferences(getResources().getString(R.string.isStop),Context.MODE_PRIVATE);
        isStop=preferences.getBoolean(getResources().getString(R.string.stop),false);

        //ConditionFragment 정렬 값 받기
        getParentFragmentManager().setFragmentResultListener(getResources().getString(R.string.memoRequestKey), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                fromDate=result.getInt(getResources().getString(R.string.fromDate));
                toDate=result.getInt(getResources().getString(R.string.toDate));
                count=result.getInt(getResources().getString(R.string.memoCount));
                isNewSort=result.getBoolean(getResources().getString(R.string.memoSort));

                keepCondition();
            }
        });
        //ConditionFragment 클릭 시 한 개의 Fragment만 생성하기 위한 변수 받기
        getParentFragmentManager().setFragmentResultListener(getResources().getString(R.string.removeFragment), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                addFragment=result.getInt(getResources().getString(R.string.ConditionFragment));
            }
        });
        //변경 font size 값
        getParentFragmentManager().setFragmentResultListener(getResources().getString(R.string.fontSize1), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                fontSize=result.getFloat(getResources().getString(R.string.fontSize));
                adapter.setFont(fontSize);
            }
        });
        //변경 text line 값
        getParentFragmentManager().setFragmentResultListener(getResources().getString(R.string.textLine1), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                textLine=result.getInt(getResources().getString(R.string.textLine));
                adapter.setTextLine(textLine);
            }
        });
        //변경 text ellipsize 값
        getParentFragmentManager().setFragmentResultListener(getResources().getString(R.string.textEllipsize1), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                textEllipsize=result.getBoolean(getResources().getString(R.string.textEllipsize));
                adapter.setTextEllipsize(textEllipsize);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        init(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences= getActivity().getSharedPreferences(getResources().getString(R.string.listTextSetting),Context.MODE_PRIVATE);
        textEllipsize=preferences.getBoolean(getResources().getString(R.string.textEllipsize),true);
        textLine=preferences.getInt(getResources().getString(R.string.textLine),2);
        fontSize=preferences.getFloat(getResources().getString(R.string.fontSize),15);

        adapter.setTextEllipsize(textEllipsize);
        adapter.setTextLine(textLine);
        adapter.setFont(fontSize);
    }

    @Override
    public void onStop() {
        super.onStop();

        //onStop()때 ConditionFragment 값 초기화
        Bundle bundle=new Bundle();
        bundle.putBoolean(getResources().getString(R.string.stop),true);
        getParentFragmentManager().setFragmentResult(getResources().getString(R.string.recyclerStop), bundle);
        resetTextSetting();

        isStop=true;
        SharedPreferences preferences= getActivity().getSharedPreferences(getResources().getString(R.string.isStop), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(getResources().getString(R.string.stop),isStop);
        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.releaseView();
    }

    private void init(View v){
        txtSearch=v.findViewById(R.id.txtSearch);
        txtCondition=v.findViewById(R.id.txtCondition);
        txtPrice=v.findViewById(R.id.priceTotalTxt);
        recyclerView=v.findViewById(R.id.recyclerMemo);

        txtSearch.setOnClickListener(this);
        txtCondition.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new MemoAdapter(getContext(), MemoType.RECYCLER, fontSize, textLine, textEllipsize);
        recyclerView.setAdapter(adapter);

        presenter=new ListPresenter();
        presenter.setView(this);

        boolean visitedViewpager=adapter.getVisitedViewpager();
        //viewpager 방문 후 ListFragment 돌아온 경우 검색 조건 값 유지
        //viewpager 제외한 다른 프래그먼트 방문 후, ListFragment 돌아온 경우는 데이터 조건을 초기화하여 모든 데이터 보여줌
        if(!visitedViewpager||isStop){
            presenter.getData(RoomDB.getInstance(getContext()).memoDao());
        }else{
            keepCondition();
        }

        //메모 총합 표시
        presenter.setReturnLong(new GetReturnLong() {
            @Override
            public void getLong(long value) {
                long totalPrice=value;
                String priceFormatPattern="###,###";
                DecimalFormat priceFormat = new DecimalFormat(priceFormatPattern);
                String strPrice= priceFormat.format(totalPrice);
                txtPrice.setText(strPrice);
            }
        });
        presenter.getSumPrice(RoomDB.getInstance(getContext()).memoDao(), getContext());

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.txtSearch){
            ((MainActivity)getActivity()).replaceFragment(new SearchFragment());
        }else if(v.getId()==R.id.txtCondition){
            //addFragment 1일 때만 addFragment()하여 여러 번 클릭 시 중복 생성을 막음
            if(addFragment==1){
                ((MainActivity)getActivity()).addFragment(new ConditionFragment());
                ++addFragment;
            }
        }
    }

    @Override
    public void setItems(ArrayList<MemoData> items) {
        adapter.setItems(items);
    }

    private void keepCondition(){
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

    private void resetTextSetting(){
        SharedPreferences preferences= getActivity().getSharedPreferences(getResources().getString(R.string.listTextSetting), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(getResources().getString(R.string.textEllipsize), textEllipsize);
        editor.putInt(getResources().getString(R.string.textLine), textLine);
        editor.putFloat(getResources().getString(R.string.fontSize), fontSize);

        editor.apply();
    }

}