package com.example.happybankbook.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.happybankbook.R;

public class SettingFragment extends Fragment{

    private TextView txtManual, txtEllipsis, txtPdf, txtExcel, txtTxt, txtOpenSource;
    private RadioGroup radioGroupLine, radioGroupFont;
    private RadioButton radioSingle, radioMull, radioOne, radioTwo, radioThree;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_setting, container, false);
       init(view);
        return view;
    }

    private void init(View view){
        txtManual=view.findViewById(R.id.manual);
        txtEllipsis=view.findViewById(R.id.ellipsis);
        txtPdf=view.findViewById(R.id.pdf);
        txtExcel=view.findViewById(R.id.excel);
        txtTxt=view.findViewById(R.id.txt);
        txtOpenSource=view.findViewById(R.id.openSource);
        radioGroupLine=view.findViewById(R.id.radioLineDisplay);
        radioSingle=view.findViewById(R.id.radioLineSingle);
        radioMull=view.findViewById(R.id.radioLineMul);
        radioGroupFont=view.findViewById(R.id.radioFont);
        radioOne=view.findViewById(R.id.radioFontOne);
        radioTwo=view.findViewById(R.id.radioFontTwo);
        radioThree=view.findViewById(R.id.radioFontThree);
    }

}