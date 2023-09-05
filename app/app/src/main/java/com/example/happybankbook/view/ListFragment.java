package com.example.happybankbook.view;

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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ListFragment extends Fragment implements View.OnClickListener, ListContract.View{

    private TextView txtSearch;
    private TextView txtCondition;
    private TextView txtPrice;
    private RecyclerView recyclerView;

    private ListPresenter presenter;
    private MemoAdapter adapter;

    private int addFragment=1;
    private int textLine=2;
    private float fontSize=15;
    private boolean textEllipsize=true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ConditionFragment 정렬 값 받기
        getParentFragmentManager().setFragmentResultListener("memoRequestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                int fromDate=result.getInt("fromDate");
                int toDate=result.getInt("toDate");
                int count=result.getInt("count");
                boolean isNewSort=result.getBoolean("sort");

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
        //ConditionFragment 클릭 시 한 개의 Fragment만 생성하기 위한 변수 받기
        getParentFragmentManager().setFragmentResultListener("removeFragment", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                addFragment=result.getInt("ConditionFragment");
            }
        });
        //변경 font size 값
        getParentFragmentManager().setFragmentResultListener("fontSize1", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                fontSize=result.getFloat("fontSize");
                adapter.setFont(fontSize);
            }
        });
        //변경 text line 값
        getParentFragmentManager().setFragmentResultListener("textLine1", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                textLine=result.getInt("textLine");
                adapter.setTextLine(textLine);
            }
        });
        //변경 text ellipsize 값
        getParentFragmentManager().setFragmentResultListener("textEllipsize1", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                textEllipsize=result.getBoolean("textEllipsize");
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
    public void onStop() {
        super.onStop();
        //onStop()때 ConditionFragment 값 초기화
        Bundle bundle=new Bundle();
        bundle.putBoolean("stop",true);
        getParentFragmentManager().setFragmentResult("recyclerStop", bundle);
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
        presenter.getData(RoomDB.getInstance(getContext()).memoDao());

        //메모 총합 표시
        long totalPrice=presenter.getSumPrice(RoomDB.getInstance(getContext()).memoDao(),getContext());
        DecimalFormat priceFormat = new DecimalFormat("###,###");
        String strPrice= priceFormat.format(totalPrice);
        txtPrice.setText(strPrice);

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

}