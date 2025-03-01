package com.example.happybankbook.view;

import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_FROM_DATE;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_IS_CLICKED_ONCE;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_IS_INITIALIZATION;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_IS_NEWEST_SORT;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_ITEM_COUNT;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_TO_DATE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_INITIALIZATION;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_RECYCLERVIEW_SORT;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_REMOVE_FRAGMENT;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_RETAIN_SORT;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_VIEWPAGER_SORT;
import static com.example.happybankbook.constants.PreferencesDefaults.PREF_DEFAULT_IS_CLICKED_DURATION;
import static com.example.happybankbook.constants.PreferencesDefaults.PREF_DEFAULT_IS_NEWEST_SORT;
import static com.example.happybankbook.constants.PreferencesDefaults.PREF_DEFAULT_ITEM_COUNT;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_FROM_DATE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_IS_CLICKED_DURATION;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_IS_NEWEST_SORT;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_ITEM_COUNT;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TO_DATE;
import static com.example.happybankbook.constants.PreferencesNames.PREF_NAME_SORT;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

public class ConditionFragment extends Fragment
        implements View.OnClickListener, RadioGroup.OnCheckedChangeListener
{

    private static final int DEFAULT_FROM_DATE=0;
    private static final int DEFAULT_TO_DATE=30000000;
    private static final int DEFAULT_COUNT=0;

    private Context mContext;
    private Activity mActivity;

    private TextView durationTextView, fromDurationTextView, toDurationTextView;
    private EditText itemCountEditText;
    private ImageView clseeImageView;
    private Button submitButton, initButton;
    private RadioGroup radioGroupSort;
    private RadioButton oldestSortRadioButton, newestSortRadioButton;

    private boolean isClickedDuration=true;
    private boolean hasVisitedViewPager=false;


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
        getParentFragmentManager()
                .setFragmentResultListener(
                        REQUEST_KEY_RETAIN_SORT,
                        this,
                        (requestKey, result) -> {
                            hasVisitedViewPager=result.getBoolean(BUNDLE_KEY_IS_NEWEST_SORT);
                            if(!hasVisitedViewPager){
                                reset();
                            }
                        }
                );
        //ListFragment에서 isInitialization 받을 때는 SharedPreferences 대신 값 초기화
        getParentFragmentManager()
                .setFragmentResultListener(
                        REQUEST_KEY_INITIALIZATION,
                        this,
                        (requestKey, result) -> {
                            boolean isInitialization=result.getBoolean(BUNDLE_KEY_IS_INITIALIZATION);
                            if(isInitialization){
                                reset();
                            }
                        }
                );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_condition, container, false);

        initViews(view);
        setListener();
        newestSortRadioButton.setChecked(true);
        oldestSortRadioButton.setChecked(false);

        return view;
    }

    private void initViews(View view){
        clseeImageView=view.findViewById(R.id.close);
        durationTextView=view.findViewById(R.id.duration);
        fromDurationTextView=view.findViewById(R.id.fromDuration);
        toDurationTextView=view.findViewById(R.id.toDuration);
        oldestSortRadioButton=view.findViewById(R.id.radioOldest);
        radioGroupSort=view.findViewById(R.id.radioGroupSort);
        newestSortRadioButton=view.findViewById(R.id.radioNewest);
        itemCountEditText=view.findViewById(R.id.editCount);
        submitButton=view.findViewById(R.id.buttonSubmit);
        initButton=view.findViewById(R.id.buttonInit);
    }

    private void setListener(){
        clseeImageView.setOnClickListener(this);
        durationTextView.setOnClickListener(this);
        fromDurationTextView.setOnClickListener(this);
        toDurationTextView.setOnClickListener(this);
        radioGroupSort.setOnCheckedChangeListener(this);
        submitButton.setOnClickListener(this);
        initButton.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //조회 날짜 기본 값 설정
        ((MainActivity)mActivity).setCurrentDate(fromDurationTextView);
        ((MainActivity)mActivity).setCurrentDate(toDurationTextView);

        getSharedPreferences();
    }

    private void getSharedPreferences(){
        SharedPreferences preferences=
                mActivity.getSharedPreferences(PREF_NAME_SORT, Context.MODE_PRIVATE);

        boolean isClick=
                preferences.getBoolean(PREF_KEY_IS_CLICKED_DURATION,PREF_DEFAULT_IS_CLICKED_DURATION);
        isClickedDuration=(!isClick);
        clickDuration();

        String fromDurationStr=
                preferences.getString(PREF_KEY_FROM_DATE, ((MainActivity)mActivity).setCurrentDate());
        String toDurationStr=
                preferences.getString(PREF_KEY_TO_DATE, ((MainActivity)mActivity).setCurrentDate());
        fromDurationTextView.setText(fromDurationStr);
        toDurationTextView.setText(toDurationStr);

        boolean isCheckedRadioNew=
                preferences.getBoolean(PREF_KEY_IS_NEWEST_SORT,PREF_DEFAULT_IS_NEWEST_SORT);
        newestSortRadioButton.setChecked(isCheckedRadioNew);
        oldestSortRadioButton.setChecked(!isCheckedRadioNew);

        itemCountEditText.setText(preferences.getString(PREF_KEY_ITEM_COUNT,PREF_DEFAULT_ITEM_COUNT));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //ConditionFragment 중복 생성을 막기 위한 변수 전달
        Bundle bundle=new Bundle();
        bundle.putInt(BUNDLE_KEY_IS_CLICKED_ONCE,1);
        getParentFragmentManager().setFragmentResult(REQUEST_KEY_REMOVE_FRAGMENT,bundle);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext=null;
        mActivity=null;
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
            newestSortRadioButton
                    .setBackgroundColor(ContextCompat.getColor(mContext,R.color.green));
            oldestSortRadioButton
                    .setBackground(ContextCompat.getDrawable(mContext,R.drawable.radio_sort));
        }else if(checkedId==R.id.radioOldest){
            oldestSortRadioButton.setChecked(true);
            newestSortRadioButton.setChecked(false);
            oldestSortRadioButton
                    .setBackgroundColor(ContextCompat.getColor(mContext,R.color.green));
            newestSortRadioButton
                    .setBackground(ContextCompat.getDrawable(mContext,R.drawable.radio_sort));
        }
    }

    public void clickDuration(){
        if(isClickedDuration){
            isDurationClick();
        }else{
            isNotDurationClick();
        }
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

    public void submit(){
        int toDate, fromDate, count;
        String countStr= itemCountEditText.getText().toString();
        //정렬 날짜 범위 지정
        if(isClickedDuration){
            fromDate=DEFAULT_FROM_DATE;
            toDate=DEFAULT_TO_DATE;
        }else{
            fromDate=((MainActivity)mActivity).convertDateToInt(fromDurationTextView);
            toDate=((MainActivity)mActivity).convertDateToInt(toDurationTextView);
        }
        //조회할 메모 개수 얻기
        if(TextUtils.isEmpty(countStr)){
            count= DEFAULT_COUNT;
        }else{
            count=Integer.parseInt(countStr);
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

        sendBundle(DEFAULT_FROM_DATE,DEFAULT_TO_DATE,DEFAULT_COUNT,true);

        setSharedPreferences();
    }

    public void sendBundle(int fromDate, int toDate, int count, boolean sort){
        Bundle bundle=new Bundle();
        bundle.putInt(BUNDLE_KEY_FROM_DATE,fromDate);
        bundle.putInt(BUNDLE_KEY_TO_DATE,toDate);
        bundle.putInt(BUNDLE_KEY_ITEM_COUNT,count);
        bundle.putBoolean(BUNDLE_KEY_IS_NEWEST_SORT,sort);

        getParentFragmentManager().setFragmentResult(REQUEST_KEY_RECYCLERVIEW_SORT, bundle);
        getParentFragmentManager().setFragmentResult(REQUEST_KEY_VIEWPAGER_SORT, bundle);
    }

    public void setSharedPreferences(){
        SharedPreferences preferences=
                mActivity.getSharedPreferences(PREF_NAME_SORT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(PREF_KEY_IS_CLICKED_DURATION,isClickedDuration);
        editor.putString(PREF_KEY_FROM_DATE,fromDurationTextView.getText().toString());
        editor.putString(PREF_KEY_TO_DATE,toDurationTextView.getText().toString());
        editor.putBoolean(PREF_KEY_IS_NEWEST_SORT,newestSortRadioButton.isChecked());
        editor.putString(PREF_KEY_ITEM_COUNT,itemCountEditText.getText().toString());
        editor.apply();
    }

    public void hideKeyboard(){
        InputMethodManager imm=
                (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(itemCountEditText.getWindowToken(),0);
    }

}