package com.example.happybankbook;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ConditionFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private ImageView imgClose;
    private TextView txtToDuration;
    private TextView txtFromDuration;
    private RadioGroup radioGroupSort;
    private RadioButton radioOld;
    private RadioButton radioNew;
    private Button submit;

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

        ((MainActivity)getActivity()).setNowDate(txtToDuration);
        ((MainActivity)getActivity()).setNowDate(txtFromDuration);
    }

    private void init(View view){
        imgClose=view.findViewById(R.id.close);
        txtToDuration=view.findViewById(R.id.toDuration);
        txtFromDuration=view.findViewById(R.id.fromDuration);
        radioOld=view.findViewById(R.id.radioOldest);
        radioGroupSort=view.findViewById(R.id.radioGroupSort);
        radioNew=view.findViewById(R.id.radioNewest);
        submit=view.findViewById(R.id.buttonSubmit);

        imgClose.setOnClickListener(this);
        txtToDuration.setOnClickListener(this);
        txtFromDuration.setOnClickListener(this);

       radioNew.setChecked(true);
       radioGroupSort.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.close){
            ((MainActivity)getActivity()).removeFragment(this);
        }else if(v.getId()==R.id.toDuration){
            ((MainActivity)getActivity()).setDate(txtToDuration,getContext());
        }else if(v.getId()==R.id.fromDuration){
            ((MainActivity)getActivity()).setDate(txtFromDuration,getContext());
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

}