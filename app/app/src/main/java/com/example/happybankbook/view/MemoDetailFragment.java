package com.example.happybankbook.view;

import static com.example.happybankbook.Utils.hideKeyboard;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_IS_NEWEST_SORT;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_TEXT_SIZE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_RETAIN_SORT;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_VIEWPAGER_TEXT_SIZE;
import static com.example.happybankbook.constants.PreferencesDefaults.PREF_DEFAULT_TEXT_SIZE_SMALL;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_SIZE;
import static com.example.happybankbook.constants.PreferencesNames.PREF_NAME_VIEWPAGER_TEXT_STYLE;
import static com.example.happybankbook.constants.TextStyles.TEXT_SIZE_DEFAULT_SMALL;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.happybankbook.R;
import com.example.happybankbook.adapter.DetailPagerAdapter;
import com.example.happybankbook.contract.ListContract;
import com.example.happybankbook.db.UiMemoData;
import com.example.happybankbook.presenter.ListPresenter;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MemoDetailFragment extends Fragment implements ListContract.View,View.OnClickListener{
    @Inject
    ListPresenter presenter;

    private static final String ARG_ITEM_ID = "item_id";

    private Context mContext;
    private Activity mActivity;

    private DetailPagerAdapter adapter;
    private Handler handler;
    private ViewPager2 viewPager;
    private ImageView forwardImageView, backImageView;

    public static MemoDetailFragment newInstance(int itemId) {
        Bundle args = new Bundle();
        args.putInt(ARG_ITEM_ID, itemId);

        MemoDetailFragment fragment = new MemoDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //일정 시간 후 화살표 이미지 투명화 위한 Runnable
    private final Runnable changeImgAlphaRunnable =new Runnable(){
        @Override
        public void run() {
            forwardImageView.setImageAlpha(0);
            backImageView.setImageAlpha(0);
        }
    };

    private float textSize= TEXT_SIZE_DEFAULT_SMALL;
    private int itemCount, fromDate, toDate, rowCount, currentPosition;
    private boolean isNewestSort;
    private Integer pendingItemId;


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

        getFragmentResult();
    }

    private void getFragmentResult(){
        //변경 text size 값
        getParentFragmentManager()
                .setFragmentResultListener(
                        REQUEST_KEY_VIEWPAGER_TEXT_SIZE,
                        this,
                        (requestKey, result) -> {
                            textSize=result.getFloat(BUNDLE_KEY_TEXT_SIZE);
                            adapter.setTextSize(textSize);
                        }
                );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_memo_detail, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        viewPager=view.findViewById(R.id.viewPager2);
        forwardImageView=view.findViewById(R.id.imgForward);
        backImageView=view.findViewById(R.id.imgBack);
        TextView previousTextView=view.findViewById(R.id.memoDetailPrevious);

        forwardImageView.setOnClickListener(this);
        backImageView.setOnClickListener(this);
        previousTextView.setOnClickListener(this);

        adapter=new DetailPagerAdapter(textSize);
        viewPager.setAdapter(adapter);

        int argItemId = getArguments() != null
                ? getArguments().getInt(ARG_ITEM_ID, -1)
                : -1;

        if (argItemId != -1) {
            pendingItemId = argItemId;
        }
        presenter.setView(this);

        //ConditionFragment 정렬 값 받기
        ListConditionState state = ((MainActivity) requireActivity()).getListState();
        fromDate= state.fromDate;
        toDate=state.toDate;
        itemCount= state.count;
        isNewestSort= state.isNewestSort;

        getRowCount();

        changePage();
    }

    private void moveToItem(int itemId) {
        int position = adapter.getPositionById(itemId);
        if (position >= 0) {
            viewPager.setCurrentItem(position, false);
        }
    }

    private void getRowCount(){
        presenter.setIntResultCallback(value -> rowCount=value);
        presenter.getDataCount();
    }

    private void changePage(){
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition=position;
                if(currentPosition==0){
                    forwardImageView.setVisibility(View.INVISIBLE);
                    backImageView.setVisibility(View.VISIBLE);
                }else if(currentPosition==(rowCount-1)){
                    forwardImageView.setVisibility(View.VISIBLE);
                    backImageView.setVisibility(View.INVISIBLE);
                }else{
                    forwardImageView.setVisibility(View.VISIBLE);
                    backImageView.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(fromDate>toDate){
            int temp=fromDate;
            fromDate=toDate;
            toDate=temp;
        }

        if(itemCount==0){
            presenter.setIntResultCallback(value -> {
                if(isNewestSort){
                    presenter.getDataDesc(fromDate, toDate, value);
                }else{
                    presenter.getDataAsc(fromDate, toDate, value);
                }
            });
            presenter.getDataCount();
        }else{
            if(isNewestSort){
                presenter.getDataDesc(fromDate, toDate, itemCount);
            }else{
                presenter.getDataAsc(fromDate, toDate, itemCount);
            }
        }

        SharedPreferences preferences=
                mActivity.getSharedPreferences(PREF_NAME_VIEWPAGER_TEXT_STYLE,Context.MODE_PRIVATE);
        textSize=preferences.getFloat(PREF_KEY_TEXT_SIZE, PREF_DEFAULT_TEXT_SIZE_SMALL);
        adapter.setTextSize(textSize);

        //SearchFragment에서 검색 후 키보드 내리지 않고 바로 viewpager 이동 하면,
        //계속 키보드 올려지는 경우 방지
        hideKeyboard(mContext,view);
    }

    @Override
    public void onStop() {
        super.onStop();

        Bundle bundle=new Bundle();
        bundle.putBoolean(BUNDLE_KEY_IS_NEWEST_SORT,true);
        getParentFragmentManager().setFragmentResult(REQUEST_KEY_RETAIN_SORT, bundle);

        resetTextStyle();
    }

    private void resetTextStyle(){
        SharedPreferences preferences=
                mActivity.getSharedPreferences(PREF_NAME_VIEWPAGER_TEXT_STYLE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putFloat(PREF_KEY_TEXT_SIZE, textSize);

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
        mContext=null;
        mActivity=null;
    }

    @Override
    public void setItems(ArrayList<UiMemoData> items) {
        adapter.setItems(items);
        if (pendingItemId != null) {
            moveToItem(pendingItemId);
            pendingItemId = null;
        }
        adapter.notifyDataSetChanged();
        adapter.setCondition(true);
    }

    @Override
    public void onClick(View v) {
        final int delayTime=3000;
        if(v.getId()==R.id.imgForward){
            forwardImageView.setImageAlpha(255);
            viewPager.setCurrentItem(currentPosition-1,false);
            handler.postDelayed(changeImgAlphaRunnable,delayTime);
        }else if(v.getId()==R.id.imgBack){
            backImageView.setImageAlpha(255);
            viewPager.setCurrentItem(currentPosition+1,false);
            handler.postDelayed(changeImgAlphaRunnable,delayTime);
        }else if(v.getId()==R.id.memoDetailPrevious){
            ((MainActivity)mActivity).popFragment();
        }
    }

}