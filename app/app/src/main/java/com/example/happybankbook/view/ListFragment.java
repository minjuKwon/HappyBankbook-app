package com.example.happybankbook.view;

import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_FROM_DATE;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_IS_CLICKED_ONCE;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_IS_INITIALIZATION;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_IS_NEWEST_SORT;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_ITEM_COUNT;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_TEXT_LINE;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_TEXT_SIZE;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_TO_DATE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_INITIALIZATION;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_RECYCLERVIEW_SORT;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_RECYCLERVIEW_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_RECYCLERVIEW_TEXT_LINE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_RECYCLERVIEW_TEXT_SIZE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_REMOVE_FRAGMENT;
import static com.example.happybankbook.constants.PreferencesDefaults.PREF_DEFAULT_IS_INITIALIZATION;
import static com.example.happybankbook.constants.PreferencesDefaults.PREF_DEFAULT_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.PreferencesDefaults.PREF_DEFAULT_TEXT_LINE;
import static com.example.happybankbook.constants.PreferencesDefaults.PREF_DEFAULT_TEXT_SIZE_LARGE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_IS_INITIALIZATION;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_LINE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_SIZE;
import static com.example.happybankbook.constants.PreferencesNames.PREF_NAME_LIST_TEXT_STYLE;
import static com.example.happybankbook.constants.PreferencesNames.PREF_NAME_SET_INITIALIZATION;
import static com.example.happybankbook.constants.TextStyles.TEXT_ELLIPSIZE_DEFAULT;
import static com.example.happybankbook.constants.TextStyles.TEXT_LINE_DEFAULT;
import static com.example.happybankbook.constants.TextStyles.TEXT_SIZE_DEFAULT_LARGE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.happybankbook.adapter.MemoAdapter;
import com.example.happybankbook.R;
import com.example.happybankbook.adapter.MemoType;
import com.example.happybankbook.contract.ListContract;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.presenter.ListPresenter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ListFragment extends Fragment implements View.OnClickListener, ListContract.View{
    @Inject
    ListPresenter presenter;

    private Context mContext;
    private Activity mActivity;
    private MemoAdapter adapter;
    private TextView totalPriceTextView;

    private int clickCountCondition=1;
    private int textLine= TEXT_LINE_DEFAULT;
    private float textSize= TEXT_SIZE_DEFAULT_LARGE;
    private boolean hasTextEllipsize= TEXT_ELLIPSIZE_DEFAULT;
    private int itemCount, fromDate, toDate;
    private boolean isNewestSort, isInitialization;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
        if (context instanceof Activity) {
            mActivity = (Activity)context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences=
                mActivity.getSharedPreferences(PREF_NAME_SET_INITIALIZATION,Context.MODE_PRIVATE);
        isInitialization=
                preferences.getBoolean(PREF_KEY_IS_INITIALIZATION,PREF_DEFAULT_IS_INITIALIZATION);

        getFragmentResult();
    }

    private void getFragmentResult(){
        //ConditionFragment 정렬 값 받기
        getParentFragmentManager()
                .setFragmentResultListener(
                        REQUEST_KEY_RECYCLERVIEW_SORT,
                        this,
                        (requestKey, result) -> {
                            fromDate=result.getInt(BUNDLE_KEY_FROM_DATE);
                            toDate=result.getInt(BUNDLE_KEY_TO_DATE);
                            itemCount=result.getInt(BUNDLE_KEY_ITEM_COUNT);
                            isNewestSort =result.getBoolean(BUNDLE_KEY_IS_NEWEST_SORT);

                            keepCondition();
                        }
                );
        //ConditionFragment 클릭 시 한 개의 Fragment만 생성하기 위한 변수 받기
        getParentFragmentManager()
                .setFragmentResultListener(
                        REQUEST_KEY_REMOVE_FRAGMENT,
                        this,
                        (requestKey, result) ->
                                clickCountCondition=result.getInt(BUNDLE_KEY_IS_CLICKED_ONCE)
                );
        //변경 text size 값
        getParentFragmentManager()
                .setFragmentResultListener(
                        REQUEST_KEY_RECYCLERVIEW_TEXT_SIZE,
                        this,
                        (requestKey, result) -> {
                            textSize=result.getFloat(BUNDLE_KEY_TEXT_SIZE);
                            adapter.setTextSize(textSize);
                        }
                );
        //변경 text line 값
        getParentFragmentManager()
                .setFragmentResultListener(
                        REQUEST_KEY_RECYCLERVIEW_TEXT_LINE,
                        this,
                        (requestKey, result) -> {
                            textLine=result.getInt(BUNDLE_KEY_TEXT_LINE);
                            adapter.setTextLine(textLine);
                        }
                );
        //변경 text ellipsize 값
        getParentFragmentManager()
                .setFragmentResultListener(
                        REQUEST_KEY_RECYCLERVIEW_TEXT_ELLIPSIZE,
                        this,
                        (requestKey, result) -> {
                            hasTextEllipsize=result.getBoolean(BUNDLE_KEY_TEXT_ELLIPSIZE);
                            adapter.setTextEllipsize(hasTextEllipsize);
                        }
                );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        init(view);
        return view;
    }

    private void init(View v){
        RecyclerView recyclerView=v.findViewById(R.id.recyclerMemo);
        TextView searchTextView=v.findViewById(R.id.txtSearch);
        TextView conditionTextView=v.findViewById(R.id.txtCondition);
        totalPriceTextView=v.findViewById(R.id.priceTotalTxt);

        searchTextView.setOnClickListener(this);
        conditionTextView.setOnClickListener(this);

        presenter.setView(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter=new MemoAdapter(mContext, MemoType.RECYCLER, textSize, textLine, hasTextEllipsize);
        recyclerView.setAdapter(adapter);

        getMemoTotalPrice();

        setMemoListCondition();
    }

    private void getMemoTotalPrice(){
        presenter.setLongResultCallback(value -> {
            String formatPatternPrice="###,###";
            DecimalFormat formattedPrice = new DecimalFormat(formatPatternPrice);
            String priceStr= formattedPrice.format(value);
            totalPriceTextView.setText(priceStr);
        });
        presenter.getSumPrice(mContext);
    }

    private void setMemoListCondition(){
        boolean hasVisitedViewPager=adapter.hasVisitedViewpager();
        //viewpager 방문 후 ListFragment 돌아온 경우 검색 조건 값 유지
        //viewpager 제외한 다른 프래그먼트 방문 후,
        // ListFragment 돌아온 경우는 데이터 조건을 초기화하여 모든 데이터 보여줌
        if(!hasVisitedViewPager||isInitialization){
            presenter.getData();
        }else{
            keepCondition();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences=
                mActivity.getSharedPreferences(PREF_NAME_LIST_TEXT_STYLE,Context.MODE_PRIVATE);
        hasTextEllipsize=preferences.getBoolean(PREF_KEY_TEXT_ELLIPSIZE,PREF_DEFAULT_TEXT_ELLIPSIZE);
        textLine=preferences.getInt(PREF_KEY_TEXT_LINE,PREF_DEFAULT_TEXT_LINE);
        textSize=preferences.getFloat(PREF_KEY_TEXT_SIZE,PREF_DEFAULT_TEXT_SIZE_LARGE);

        adapter.setTextEllipsize(hasTextEllipsize);
        adapter.setTextLine(textLine);
        adapter.setTextSize(textSize);
    }

    @Override
    public void onStop() {
        super.onStop();

        //onStop()때 ConditionFragment 값 초기화
        Bundle bundle=new Bundle();
        bundle.putBoolean(BUNDLE_KEY_IS_INITIALIZATION,true);
        getParentFragmentManager().setFragmentResult(REQUEST_KEY_INITIALIZATION, bundle);
        resetTextStyle();

        isInitialization=true;
        SharedPreferences preferences=
                mActivity.getSharedPreferences(PREF_NAME_SET_INITIALIZATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(PREF_KEY_IS_INITIALIZATION,isInitialization);
        editor.apply();
    }

    private void resetTextStyle(){
        SharedPreferences preferences=
                mActivity.getSharedPreferences(PREF_NAME_LIST_TEXT_STYLE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(PREF_KEY_TEXT_ELLIPSIZE, hasTextEllipsize);
        editor.putInt(PREF_KEY_TEXT_LINE, textLine);
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

    private void keepCondition(){
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
                    presenter.getDataAsc(fromDate, toDate, value );
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
    }

    @Override
    public void setItems(ArrayList<MemoData> items) {
        adapter.setItems(items);
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

}