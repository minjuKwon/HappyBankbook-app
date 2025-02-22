package com.example.happybankbook.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.happybankbook.MainActivity;
import com.example.happybankbook.R;

public class ConditionFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private static final int DEFAULT_FROM_DATE=0;
    private static final int DEFAULT_TO_DATE=30000000;
    private static final int DEFAULT_COUNT=0;

    private TextView durationTextView;
    private TextView fromDurationTextView;
    private TextView toDurationTextView;
    private RadioButton oldestSortRadioButton;
    private RadioButton newestSortRadioButton;
    private EditText itemCountEditText;

    private boolean isClickedDuration=true;
    private boolean hasVisitedViewPager=false;

    private Context mContext;
    private Activity mActivity;

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
        //viewPager 후 recyclerView로 돌아 왔을 때 condition 값을 유지 하기 위한 변수 얻기
        getParentFragmentManager().setFragmentResultListener(getResources().getString(R.string.request_key_retain_sort), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                hasVisitedViewPager=result.getBoolean(getResources().getString(R.string.is_newest_sort));
                if(!hasVisitedViewPager){
                    reset();
                }
            }
        });
        //ListFragment에서 isInitialization 받을 때는 SharedPreferences 대신 값 초기화
        getParentFragmentManager().setFragmentResultListener(getResources().getString(R.string.request_key_initialization), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                boolean isInitialization=result.getBoolean(getResources().getString(R.string.is_initialization));
                if(isInitialization){
                    reset();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_condition, container, false);
        init(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //조회 날짜 기본 값 설정
        ((MainActivity)mActivity).setCurrentDate(fromDurationTextView);
        ((MainActivity)mActivity).setCurrentDate(toDurationTextView);

        //SharedPreferences에 저장된 정렬 값 가져오기
        SharedPreferences preferences= mActivity.getSharedPreferences(getResources().getString(R.string.pref_sort),Context.MODE_PRIVATE);

        boolean isClick=preferences.getBoolean(getResources().getString(R.string.is_clicked_duration),true);
        isClickedDuration=(!isClick);
        clickDuration();

        fromDurationTextView.setText(preferences.getString(getResources().getString(R.string.from_date),((MainActivity)mActivity).setCurrentDate()));
        toDurationTextView.setText(preferences.getString(getResources().getString(R.string.to_date),((MainActivity)mActivity).setCurrentDate()));

        boolean isCheckedRadioNew=preferences.getBoolean(getResources().getString(R.string.is_newest_sort),true);
        newestSortRadioButton.setChecked(isCheckedRadioNew);
        oldestSortRadioButton.setChecked(!isCheckedRadioNew);

        itemCountEditText.setText(preferences.getString(getResources().getString(R.string.item_count),null));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //ConditionFragment 중복 생성을 막기 위한 변수 전달
        Bundle bundle=new Bundle();
        bundle.putInt(getResources().getString(R.string.is_clicked_once),1);
        getParentFragmentManager().setFragmentResult(getResources().getString(R.string.request_key_remove_fragment),bundle);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext=null;
        mActivity=null;
    }

    private void init(View view){
        ImageView clseeImageView=view.findViewById(R.id.close);
        durationTextView=view.findViewById(R.id.duration);
        fromDurationTextView=view.findViewById(R.id.fromDuration);
        toDurationTextView=view.findViewById(R.id.toDuration);
        oldestSortRadioButton=view.findViewById(R.id.radioOldest);
        RadioGroup radioGroupSort=view.findViewById(R.id.radioGroupSort);
        newestSortRadioButton=view.findViewById(R.id.radioNewest);
        itemCountEditText=view.findViewById(R.id.editCount);
        Button submitButton=view.findViewById(R.id.buttonSubmit);
        Button initButton=view.findViewById(R.id.buttonInit);

        newestSortRadioButton.setChecked(true);
        oldestSortRadioButton.setChecked(false);

        clseeImageView.setOnClickListener(this);
        durationTextView.setOnClickListener(this);
        fromDurationTextView.setOnClickListener(this);
        toDurationTextView.setOnClickListener(this);

       radioGroupSort.setOnCheckedChangeListener(this);

       submitButton.setOnClickListener(this);
       initButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.close){
            hideKeyboard();
            ((MainActivity)mActivity).removeFragment(this);
        }else if(v.getId()==R.id.duration){
            clickDuration();
        }else if(v.getId()==R.id.toDuration){
            ((MainActivity)mActivity).setDate(toDurationTextView,getContext());
        }else if(v.getId()==R.id.fromDuration){
            ((MainActivity)mActivity).setDate(fromDurationTextView,getContext());
        }else if(v.getId()==R.id.buttonSubmit){
            submit();
        }else if(v.getId()==R.id.buttonInit){
            reset();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId==R.id.radioNewest){
            newestSortRadioButton.setChecked(true);
            oldestSortRadioButton.setChecked(false);
            newestSortRadioButton.setBackgroundColor(ContextCompat.getColor(mContext,R.color.green));
            oldestSortRadioButton.setBackground(ContextCompat.getDrawable(mContext,R.drawable.radio_sort));
        }else if(checkedId==R.id.radioOldest){
            oldestSortRadioButton.setChecked(true);
            newestSortRadioButton.setChecked(false);
            oldestSortRadioButton.setBackgroundColor(ContextCompat.getColor(mContext,R.color.green));
            newestSortRadioButton.setBackground(ContextCompat.getDrawable(mContext,R.drawable.radio_sort));
        }
    }

    public void clickDuration(){
        if(isClickedDuration){
            isDurationClick();
        }else{
            isNotDurationClick();
        }
    }

    public void submit(){
        int toDate, fromDate, count;
        //정렬 날짜 범위 지정
        if(isClickedDuration){
            fromDate=DEFAULT_FROM_DATE;
            toDate=DEFAULT_TO_DATE;
        }else{
            fromDate=((MainActivity)mActivity).convertDateToInt(fromDurationTextView);
            toDate=((MainActivity)mActivity).convertDateToInt(toDurationTextView);
        }
        //조회할 메모 개수 얻기
        if(TextUtils.isEmpty(itemCountEditText.getText().toString())){
            count= DEFAULT_COUNT;
        }else{
            count=Integer.parseInt(itemCountEditText.getText().toString());
        }
        //memo recyclerView로 정렬 데이터 전달
        sendBundle(fromDate, toDate, count, newestSortRadioButton.isChecked());

        setSharedPreferences();

        hideKeyboard();

        ((MainActivity)mActivity).removeFragment(this);
    }

    public void reset(){
        ((MainActivity)mActivity).setCurrentDate(fromDurationTextView);
        ((MainActivity)mActivity).setCurrentDate(toDurationTextView);
        isNotDurationClick();
        newestSortRadioButton.setChecked(true);
        itemCountEditText.setText(null);

        setSharedPreferences();

        sendBundle(DEFAULT_FROM_DATE,DEFAULT_TO_DATE,DEFAULT_COUNT,true);
    }

    public void isDurationClick(){
        durationTextView.setTextColor(ContextCompat.getColor(mContext,R.color.black));
        fromDurationTextView.setVisibility(View.VISIBLE);
        toDurationTextView.setVisibility(View.VISIBLE);
        isClickedDuration=false;
    }

    public void isNotDurationClick(){
        durationTextView.setTextColor(ContextCompat.getColor(mContext,R.color.darkGray));
        fromDurationTextView.setVisibility(View.GONE);
        toDurationTextView.setVisibility(View.GONE);
        isClickedDuration=true;
    }

    public void sendBundle(int fromDate, int toDate, int count, boolean sort){
        Bundle bundle=new Bundle();
        bundle.putInt(getResources().getString(R.string.from_date),fromDate);
        bundle.putInt(getResources().getString(R.string.to_date),toDate);
        bundle.putInt(getResources().getString(R.string.item_count),count);
        bundle.putBoolean(getResources().getString(R.string.is_newest_sort),sort);

        getParentFragmentManager().setFragmentResult(getResources().getString(R.string.request_key_recyclerview_sort), bundle);
        getParentFragmentManager().setFragmentResult(getResources().getString(R.string.request_key_viewpager_sort), bundle);
    }

    public void setSharedPreferences(){
        SharedPreferences preferences= mActivity.getSharedPreferences(getResources().getString(R.string.pref_sort), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(getResources().getString(R.string.is_clicked_duration),isClickedDuration);
        editor.putString(getResources().getString(R.string.from_date),fromDurationTextView.getText().toString());
        editor.putString(getResources().getString(R.string.to_date),toDurationTextView.getText().toString());
        editor.putBoolean(getResources().getString(R.string.is_newest_sort),newestSortRadioButton.isChecked());
        editor.putString(getResources().getString(R.string.item_count),itemCountEditText.getText().toString());
        editor.apply();
    }

    public void hideKeyboard(){
        InputMethodManager imm=(InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(itemCountEditText.getWindowToken(),0);
    }

}