package com.example.happybankbook.view;

import static com.example.happybankbook.constants.FragmentTag.SEARCH;
import static com.example.happybankbook.constants.PreferencesDefaults.PREF_DEFAULT_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.PreferencesDefaults.PREF_DEFAULT_TEXT_LINE;
import static com.example.happybankbook.constants.PreferencesDefaults.PREF_DEFAULT_TEXT_SIZE_LARGE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_LINE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_SIZE;
import static com.example.happybankbook.constants.PreferencesNames.PREF_NAME_LIST_TEXT_STYLE;
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

import com.example.happybankbook.adapter.ListAdapter;
import com.example.happybankbook.R;
import com.example.happybankbook.adapter.OnItemSelectedListener;
import com.example.happybankbook.contract.ListContract;
import com.example.happybankbook.db.UiMemoData;
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
    private OnItemSelectedListener callback;
    private ListAdapter adapter;
    private TextView totalPriceTextView;

    private int textLine= TEXT_LINE_DEFAULT;
    private float textSize= TEXT_SIZE_DEFAULT_LARGE;
    private boolean hasTextEllipsize= TEXT_ELLIPSIZE_DEFAULT;
    private boolean isNewestSort=true;
    private int itemCount, fromDate, toDate;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
        if (context instanceof Activity) {
            mActivity = (Activity)context;
        }
        if(context instanceof OnItemSelectedListener){
            callback = (OnItemSelectedListener) context;
        }
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
        adapter = new ListAdapter(textSize, textLine, hasTextEllipsize, item ->{
            callback.onItemSelected(item.getIdx());
        }
        );
        recyclerView.setAdapter(adapter);

        getMemoTotalPrice();

        getCondition();
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

    public void getCondition(){
        //ConditionFragment 정렬 값 받기
        ListConditionState state = ((MainActivity) requireActivity()).getListState();
        fromDate= state.fromDate;
        toDate=state.toDate;
        itemCount= state.count;
        isNewestSort= state.isNewestSort;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        keepCondition();

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

        resetTextStyle();
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

    public void keepCondition(){
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
    }

    @Override
    public void setItems(ArrayList<UiMemoData> items) {
        adapter.setItems(items);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.txtSearch){
            ((MainActivity)mActivity).replaceFragment(new SearchFragment(),SEARCH);
        }else if(v.getId()==R.id.txtCondition){
            ((MainActivity)mActivity).addFragment(new ConditionFragment());
        }
    }

}