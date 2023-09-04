package com.example.happybankbook.view;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.happybankbook.R;

public class SettingFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener{

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

        radioSingle.setChecked(false);
        radioMull.setChecked(true);

        radioOne.setChecked(true);
        radioTwo.setChecked(false);
        radioThree.setChecked(false);

        radioGroupLine.setOnCheckedChangeListener(this);
        radioGroupFont.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if(group.getId()==R.id.radioLineDisplay){
            if(checkedId==R.id.radioLineSingle){
                radioLine(true, false, R.color.black, R.color.darkGray);
            }else if(checkedId==R.id.radioLineMul){
                radioLine(false, true, R.color.darkGray, R.color.black);
            }
        }

        else if(group.getId()==R.id.radioFont){
            if(checkedId==R.id.radioFontOne){
                radioFont(true, false, false, R.color.black, R.color.darkGray, R.color.darkGray);
                changeFont(15,"fontSize1");
                changeFont(15,"fontSize2");
                changeFont(12,"fontSize3");
                changeFont(12,"fontSize4");
            }else if(checkedId==R.id.radioFontTwo){
                radioFont(false, true, false, R.color.darkGray, R.color.black, R.color.darkGray);
                changeFont(18,"fontSize1");
                changeFont(18,"fontSize2");
                changeFont(15,"fontSize3");
                changeFont(15,"fontSize4");
            }else if(checkedId==R.id.radioFontThree){
                radioFont(false, false, true, R.color.darkGray, R.color.darkGray, R.color.black);
                changeFont(21,"fontSize1");
                changeFont(21,"fontSize2");
                changeFont(18,"fontSize3");
                changeFont(18,"fontSize4");
            }
        }

    }

    public void radioLine(boolean b1, boolean b2, int c1, int c2){
        radioSingle.setChecked(b1);
        radioMull.setChecked(b2);
        radioSingle.setTextColor(ContextCompat.getColor(getContext(),c1));
        radioMull.setTextColor(ContextCompat.getColor(getContext(),c2));
    }

    public void radioFont(boolean b1, boolean b2, boolean b3, int c1, int c2, int c3){
        radioOne.setChecked(b1);
        radioTwo.setChecked(b2);
        radioThree.setChecked(b3);
        radioOne.setTextColor(ContextCompat.getColor(getContext(),c1));
        radioTwo.setTextColor(ContextCompat.getColor(getContext(),c2));
        radioThree.setTextColor(ContextCompat.getColor(getContext(),c3));
    }

    public void changeFont(int size, String key){
        Bundle bundle=new Bundle();
        bundle.putFloat("fontSize",size);

        getParentFragmentManager().setFragmentResult(key, bundle);
    }

}