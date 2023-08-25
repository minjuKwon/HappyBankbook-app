package com.example.happybankbook.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.text.TextUtils;
import android.util.Log;
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

    private ImageView imgClose;
    private TextView txtDuration;
    private TextView txtFromDuration;
    private TextView txtToDuration;
    private RadioGroup radioGroupSort;
    private RadioButton radioOld;
    private RadioButton radioNew;
    private EditText editCount;
    private Button submit;
    private Button init;

    private boolean isClick=true;

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
        ((MainActivity)getActivity()).setNowDate(txtFromDuration);
        ((MainActivity)getActivity()).setNowDate(txtToDuration);

        //SharedPreferences에 저장된 정렬 값 가져오기
        SharedPreferences preferences= getActivity().getSharedPreferences("sortInfo",Context.MODE_PRIVATE);

        boolean click=preferences.getBoolean("isClick",true);
        isClick=(!click);
        clickDuration();

        txtFromDuration.setText(preferences.getString("fromDate",((MainActivity)getActivity()).setNowDate()));
        txtToDuration.setText(preferences.getString("toDate",((MainActivity)getActivity()).setNowDate()));

        boolean newCheck=preferences.getBoolean("sort",true);
        radioNew.setChecked(newCheck);
        radioOld.setChecked(!newCheck);

        editCount.setText(preferences.getString("count",null));

        //ListFragment에서 recyclerStop 받을 때는 SharedPreferences 대신 값 초기화
        getParentFragmentManager().setFragmentResultListener("recyclerStop", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                boolean value=result.getBoolean("stop");
                if(value){
                    reset();
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //ConditionFragment 중복 생성을 막기 위한 변수 전달
        Bundle bundle=new Bundle();
        bundle.putInt("ConditionFragment",1);
        getParentFragmentManager().setFragmentResult("removeFragment",bundle);

    }

    private void init(View view){
        imgClose=view.findViewById(R.id.close);
        txtDuration=view.findViewById(R.id.duration);
        txtFromDuration=view.findViewById(R.id.fromDuration);
        txtToDuration=view.findViewById(R.id.toDuration);
        radioOld=view.findViewById(R.id.radioOldest);
        radioGroupSort=view.findViewById(R.id.radioGroupSort);
        radioNew=view.findViewById(R.id.radioNewest);
        editCount=view.findViewById(R.id.editCount);
        submit=view.findViewById(R.id.buttonSubmit);
        init=view.findViewById(R.id.buttonInit);

        imgClose.setOnClickListener(this);
        txtDuration.setOnClickListener(this);
        txtFromDuration.setOnClickListener(this);
        txtToDuration.setOnClickListener(this);

       radioGroupSort.setOnCheckedChangeListener(this);

       submit.setOnClickListener(this);
       init.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.close){
            hideKeyboard();
            ((MainActivity)getActivity()).removeFragment(this);
        }else if(v.getId()==R.id.duration){
            clickDuration();
        }else if(v.getId()==R.id.toDuration){
            ((MainActivity)getActivity()).setDate(txtToDuration,getContext());
        }else if(v.getId()==R.id.fromDuration){
            ((MainActivity)getActivity()).setDate(txtFromDuration,getContext());
        }else if(v.getId()==R.id.buttonSubmit){
            submit();
        }else if(v.getId()==R.id.buttonInit){
            reset();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId==R.id.radioNewest){
            radioNew.setChecked(true);
            radioOld.setChecked(false);
            radioNew.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.green));
            radioOld.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.radio_sort));
        }else if(checkedId==R.id.radioOldest){
            radioOld.setChecked(true);
            radioNew.setChecked(false);
            radioOld.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.green));
            radioNew.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.radio_sort));
        }
    }

    public void clickDuration(){
        if(isClick){
            isDurationClick();
        }else{
            isNotDurationClick();
        }
    }

    public void submit(){
        int count=0;
        int toDate, fromDate;
        //정렬 날짜 범위 지정
        if(isClick){
            fromDate=0; toDate=30000000;
        }else{
            fromDate=((MainActivity)getActivity()).dateIntToString(txtFromDuration);
            toDate=((MainActivity)getActivity()).dateIntToString(txtToDuration);
        }
        //조회할 메모 개수 얻기
        if(!TextUtils.isEmpty(editCount.getText().toString())){
            count=Integer.parseInt(editCount.getText().toString());
        }
        //memo recyclerView로 정렬 데이터 전달
        sendBundle(fromDate, toDate, count, radioNew.isChecked());

        setSharedPreferences();

        hideKeyboard();

        ((MainActivity)getActivity()).removeFragment(this);
    }

    public void reset(){
        ((MainActivity)getActivity()).setNowDate(txtFromDuration);
        ((MainActivity)getActivity()).setNowDate(txtToDuration);
        isNotDurationClick();
        radioNew.setChecked(true);
        editCount.setText(null);

        setSharedPreferences();

        sendBundle(0,30000000,0,true);
    }

    public void isDurationClick(){
        txtDuration.setTextColor(ContextCompat.getColor(getContext(),R.color.black));
        txtFromDuration.setVisibility(View.VISIBLE);
        txtToDuration.setVisibility(View.VISIBLE);
        isClick=false;
    }

    public void isNotDurationClick(){
        txtDuration.setTextColor(ContextCompat.getColor(getContext(),R.color.darkGray));
        txtFromDuration.setVisibility(View.GONE);
        txtToDuration.setVisibility(View.GONE);
        isClick=true;
    }

    public void sendBundle(int fromDate, int toDate, int count, boolean sort){
        Bundle bundle=new Bundle();
        bundle.putInt("fromDate",fromDate);
        bundle.putInt("toDate",toDate);
        bundle.putInt("count",count);
        bundle.putBoolean("sort",sort);

        getParentFragmentManager().setFragmentResult("memoRequestKey", bundle);
        getParentFragmentManager().setFragmentResult("memoRequestKey2", bundle);
    }

    public void setSharedPreferences(){
        SharedPreferences preferences= getActivity().getSharedPreferences("sortInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean("isClick",isClick);
        editor.putString("fromDate",txtFromDuration.getText().toString());
        editor.putString("toDate",txtToDuration.getText().toString());
        editor.putBoolean("sort",radioNew.isChecked());
        editor.putString("count",editCount.getText().toString());
        editor.apply();
    }

    public void hideKeyboard(){
        InputMethodManager imm=(InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editCount.getWindowToken(),0);
    }

}