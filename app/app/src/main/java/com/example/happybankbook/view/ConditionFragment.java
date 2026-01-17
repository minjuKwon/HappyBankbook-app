package com.example.happybankbook.view;

import static com.example.happybankbook.Utils.convertDateToInt;
import static com.example.happybankbook.Utils.formatDateToString;
import static com.example.happybankbook.Utils.hideKeyboard;
import static com.example.happybankbook.Utils.setCurrentDate;
import static com.example.happybankbook.Utils.setDate;
import static com.example.happybankbook.constants.ConditionDefaults.DEFAULT_COUNT;
import static com.example.happybankbook.constants.ConditionDefaults.DEFAULT_FROM_DATE;
import static com.example.happybankbook.constants.ConditionDefaults.DEFAULT_TO_DATE;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.happybankbook.R;

public class ConditionFragment extends Fragment
        implements View.OnClickListener, RadioGroup.OnCheckedChangeListener
{
    private Context mContext;
    private Activity mActivity;

    private TextView durationTextView, fromDurationTextView, toDurationTextView;
    private EditText itemCountEditText;
    private ImageView clseeImageView;
    private Button submitButton, initButton;
    private RadioGroup radioGroupSort;
    private RadioButton oldestSortRadioButton, newestSortRadioButton;

    private boolean isClickedDuration=true;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
        if (context instanceof Activity) {
            mActivity = (Activity)context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_condition, container, false);

        initViews(view);
        setListener();
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
        setCurrentDate(fromDurationTextView);
        setCurrentDate(toDurationTextView);
        getConditionState();
    }

    private void getConditionState(){
        ListConditionState state = ((MainActivity) requireActivity()).getListState();
        if(state.fromDate==DEFAULT_FROM_DATE&&state.toDate==DEFAULT_TO_DATE){
            isClickedDuration=false;
        }else{
            isClickedDuration=true;
            fromDurationTextView.setText(formatDateToString(state.fromDate));
            toDurationTextView.setText(formatDateToString(state.toDate));
        }

        clickDuration();

        newestSortRadioButton.setChecked(state.isNewestSort);
        oldestSortRadioButton.setChecked(!state.isNewestSort);

        String count= "";
        if(state.count!=0 ) count=String.format(java.util.Locale.getDefault(), Integer.toString(state.count));
        itemCountEditText.setText(count);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
            hideKeyboard(mContext,itemCountEditText);
            ((MainActivity)mActivity).removeFragment(this);
        }else if(v.getId()==R.id.duration){
            clickDuration();
        }else if(v.getId()==R.id.toDuration){
            setDate(toDurationTextView,mContext);
        }else if(v.getId()==R.id.fromDuration){
            setDate(fromDurationTextView,mContext);
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

    private void clickDuration(){
        if(isClickedDuration){
            isDurationClick();
        }else{
            isNotDurationClick();
        }
    }

    private void isDurationClick(){
        durationTextView.setTextColor(ContextCompat.getColor(mContext,R.color.black));
        fromDurationTextView.setVisibility(View.VISIBLE);
        toDurationTextView.setVisibility(View.VISIBLE);
        isClickedDuration=false;
    }

    private void isNotDurationClick(){
        durationTextView.setTextColor(ContextCompat.getColor(mContext,R.color.darkGray));
        fromDurationTextView.setVisibility(View.GONE);
        toDurationTextView.setVisibility(View.GONE);
        isClickedDuration=true;
    }

    private void submit(){
        int toDate, fromDate, count;
        String countStr= itemCountEditText.getText().toString();
        //정렬 날짜 범위 지정
        if(isClickedDuration){
            fromDate=DEFAULT_FROM_DATE;
            toDate=DEFAULT_TO_DATE;
        }else{
            fromDate= convertDateToInt(fromDurationTextView);
            toDate= convertDateToInt(toDurationTextView);
        }
        //조회할 메모 개수 얻기
        if(TextUtils.isEmpty(countStr)){
            count= DEFAULT_COUNT;
        }else{
            count=Integer.parseInt(countStr);
        }
        //memo recyclerView로 정렬 데이터 전달
        sendCondition(fromDate, toDate, count, newestSortRadioButton.isChecked());

        hideKeyboard(mContext,itemCountEditText);

        ((MainActivity)mActivity).onConditionChanged();
        ((MainActivity) mActivity).removeFragment(this);
    }

    private void reset(){
        setCurrentDate(fromDurationTextView);
        setCurrentDate(toDurationTextView);
        isNotDurationClick();
        newestSortRadioButton.setChecked(true);
        itemCountEditText.setText(null);

        sendCondition(DEFAULT_FROM_DATE,DEFAULT_TO_DATE,DEFAULT_COUNT,true);
    }

    private void sendCondition(int fromDate, int toDate, int count, boolean sort){
          ListConditionState state = ((MainActivity) requireActivity()).getListState();
          state.fromDate=fromDate;
          state.toDate=toDate;
          state.count=count;
          state.isNewestSort= sort;
    }

}