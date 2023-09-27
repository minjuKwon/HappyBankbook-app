package com.example.happybankbook.view;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
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
    private final Runnable postRunnable =new Runnable(){
        @Override
        public void run() {
            imgForward.setColorFilter(Color.TRANSPARENT);
            imgBack.setColorFilter(Color.TRANSPARENT);
        }
    };

    private ViewPager2 viewPager;
    private ImageView imgForward;
    private ImageView imgBack;

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
    private boolean isFirst2=true;
    private float fontSize=12;

    private Context mContext;
    private Activity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
        if(context instanceof Activity){
            mActivity=(Activity)context;
        }
    }

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

        SharedPreferences preferences= mActivity.getSharedPreferences(getResources().getString(R.string.memoDetailTextSetting),Context.MODE_PRIVATE);
        fontSize=preferences.getFloat(getResources().getString(R.string.fontSize),12);
        adapter.setFont(fontSize);
        
        //SearchFragment에서 검색 후 키보드 내리지 않고 바로 viewpager 이동 하면,
        //계속 키보드 올려지는 경우 방지
        InputMethodManager imm=(InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    @Override
    public void onDetach() {
        super.onDetach();
        mContext=null;
        mActivity=null;
    }

    public void init(View view){
        viewPager=view.findViewById(R.id.viewPager2);
        imgForward=view.findViewById(R.id.imgForward);
        imgBack=view.findViewById(R.id.imgBack);
        TextView txtPrevious=view.findViewById(R.id.memoDetailPrevious);

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
            imgForward.setColorFilter(ContextCompat.getColor(mContext,R.color.darkerGray));
            //처음 1번째 아이템 클릭하여 이동한 viewpager에서 이전 데이터로 이동하지 않은 오류 해결
            if(!isFirst&&isFirst2&&adapterPosition==1&&currentPosition==1){
                //notifyItemChanged 호출하면 화면 버벅거림
                adapter.notifyDataSetChanged();
                viewPager.setCurrentItem(0,false);
                isFirst2=false;
            }else{
                viewPager.setCurrentItem(currentPosition-1,false);
            }
            handler.postDelayed(postRunnable,3000);
        }else if(v.getId()==R.id.imgBack){
            imgBack.setColorFilter(ContextCompat.getColor(mContext,R.color.darkerGray));
            viewPager.setCurrentItem(currentPosition+1,false);
            handler.postDelayed(postRunnable,3000);
        }else if(v.getId()==R.id.memoDetailPrevious){
            ((MainActivity)mActivity).removeFragment(this);
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
        SharedPreferences preferences= mActivity.getSharedPreferences(getResources().getString(R.string.memoDetailTextSetting), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putFloat(getResources().getString(R.string.fontSize), fontSize);

        editor.apply();
    }

}

