package com.example.happybankbook.view;

import android.app.Activity;
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
import com.example.happybankbook.presenterReturnInterface.IntResultCallback;
import com.example.happybankbook.presenterReturnInterface.LongResultCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ListFragment extends Fragment implements View.OnClickListener, ListContract.View{

    private TextView totalPriceTextView;

    private ListPresenter presenter;
    private MemoAdapter adapter;

    private int itemCount;
    private int fromDate;
    private int toDate;
    private int clickCountCondition=1;
    private int textLine=2;
    private float fontSize=15;
    private boolean isNewestSort;
    private boolean hasTextEllipsize=true;
    private boolean isInitialization;

    private Activity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity)context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences= mActivity.getSharedPreferences(getResources().getString(R.string.isStop),Context.MODE_PRIVATE);
        isInitialization=preferences.getBoolean(getResources().getString(R.string.IsInitialization),false);

        //ConditionFragment 정렬 값 받기
        getParentFragmentManager().setFragmentResultListener(getResources().getString(R.string.memoRequestKey), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                fromDate=result.getInt(getResources().getString(R.string.fromDate));
                toDate=result.getInt(getResources().getString(R.string.toDate));
                itemCount=result.getInt(getResources().getString(R.string.memoCount));
                isNewestSort =result.getBoolean(getResources().getString(R.string.memoSort));

                keepCondition();
            }
        });
        //ConditionFragment 클릭 시 한 개의 Fragment만 생성하기 위한 변수 받기
        getParentFragmentManager().setFragmentResultListener(getResources().getString(R.string.removeFragment), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                clickCountCondition=result.getInt(getResources().getString(R.string.ConditionFragment));
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
                hasTextEllipsize=result.getBoolean(getResources().getString(R.string.textEllipsize));
                adapter.setTextEllipsize(hasTextEllipsize);
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

        SharedPreferences preferences= mActivity.getSharedPreferences(getResources().getString(R.string.listTextSetting),Context.MODE_PRIVATE);
        hasTextEllipsize=preferences.getBoolean(getResources().getString(R.string.textEllipsize),true);
        textLine=preferences.getInt(getResources().getString(R.string.textLine),2);
        fontSize=preferences.getFloat(getResources().getString(R.string.fontSize),15);

        adapter.setTextEllipsize(hasTextEllipsize);
        adapter.setTextLine(textLine);
        adapter.setFont(fontSize);
    }

    @Override
    public void onStop() {
        super.onStop();

        //onStop()때 ConditionFragment 값 초기화
        Bundle bundle=new Bundle();
        bundle.putBoolean(getResources().getString(R.string.IsInitialization),true);
        getParentFragmentManager().setFragmentResult(getResources().getString(R.string.listIsInitialization), bundle);
        resetTextSetting();

        isInitialization=true;
        SharedPreferences preferences= mActivity.getSharedPreferences(getResources().getString(R.string.isStop), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(getResources().getString(R.string.IsInitialization),isInitialization);
        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.releaseView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity=null;
    }

    private void init(View v){
        TextView searchTextView=v.findViewById(R.id.txtSearch);
        TextView conditionTextView=v.findViewById(R.id.txtCondition);
        totalPriceTextView=v.findViewById(R.id.priceTotalTxt);
        RecyclerView recyclerView=v.findViewById(R.id.recyclerMemo);

        searchTextView.setOnClickListener(this);
        conditionTextView.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new MemoAdapter(getContext(), MemoType.RECYCLER, fontSize, textLine, hasTextEllipsize);
        recyclerView.setAdapter(adapter);

        presenter=new ListPresenter();
        presenter.setView(this);

        boolean hasVisitedViewPager=adapter.hasVisitedViewpager();
        //viewpager 방문 후 ListFragment 돌아온 경우 검색 조건 값 유지
        //viewpager 제외한 다른 프래그먼트 방문 후, ListFragment 돌아온 경우는 데이터 조건을 초기화하여 모든 데이터 보여줌
        if(!hasVisitedViewPager||isInitialization){
            presenter.getData(RoomDB.getInstance(getContext()).memoDao());
        }else{
            keepCondition();
        }

        //메모 총합 표시
        presenter.setLongResultCallback(new LongResultCallback() {
            @Override
            public void onLongResult(long value) {
                String priceFormatPattern="###,###";
                DecimalFormat priceFormat = new DecimalFormat(priceFormatPattern);
                String strPrice= priceFormat.format(value);
                totalPriceTextView.setText(strPrice);
            }
        });
        presenter.getSumPrice(RoomDB.getInstance(getContext()).memoDao(), getContext());

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.txtSearch){
            ((MainActivity)mActivity).replaceFragment(new SearchFragment());
        }else if(v.getId()==R.id.txtCondition){
            //addFragment 1일 때만 addFragment()하여 여러 번 클릭 시 중복 생성을 막음
            if(clickCountCondition==1){
                ((MainActivity)mActivity).addFragment(new ConditionFragment());
                ++clickCountCondition;
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

        if(itemCount==0){
            presenter.setIntResultCallback(new IntResultCallback() {
                @Override
                public void onIntResult(int value) {
                    if(isNewestSort){
                        presenter.getDataDesc(RoomDB.getInstance(getContext()).memoDao(),fromDate,toDate,value);
                    }else{
                        presenter.getDataAsc(RoomDB.getInstance(getContext()).memoDao(),fromDate,toDate,value);
                    }
                }
            });
            presenter.getDataCount(RoomDB.getInstance(getContext()).memoDao());
        }else{
            if(isNewestSort){
                presenter.getDataDesc(RoomDB.getInstance(getContext()).memoDao(),fromDate,toDate,itemCount);
            }else{
                presenter.getDataAsc(RoomDB.getInstance(getContext()).memoDao(),fromDate,toDate,itemCount);
            }
        }
    }

    private void resetTextSetting(){
        SharedPreferences preferences= mActivity.getSharedPreferences(getResources().getString(R.string.listTextSetting), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(getResources().getString(R.string.textEllipsize), hasTextEllipsize);
        editor.putInt(getResources().getString(R.string.textLine), textLine);
        editor.putFloat(getResources().getString(R.string.fontSize), fontSize);

        editor.apply();
    }

}