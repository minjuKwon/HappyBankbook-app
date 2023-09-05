package com.example.happybankbook.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.happybankbook.R;

public class SettingFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener{

    private TextView txtManual, txtEllipsis, txtPdf, txtExcel, txtTxt, txtOpenSource;
    private RadioGroup radioGroupLine, radioGroupFont;
    private RadioButton radioSingle, radioMull, radioOne, radioTwo, radioThree;

    private boolean isEllipsize=false;
    private int checkLine, checkFontSize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_setting, container, false);
       init(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences= getActivity().getSharedPreferences("settingInfo",Context.MODE_PRIVATE);
        isEllipsize=preferences.getBoolean("isEllipsize",true);
        checkLine=preferences.getInt("checkLine",R.id.radioLineMul);
        checkFontSize=preferences.getInt("checkFontSize",R.id.radioFontOne);

        setEllipsize();

        if(checkLine==R.id.radioLineSingle){
            radioLine(true, false, R.color.black, R.color.gray);
        }else if(checkLine==R.id.radioLineMul){
            radioLine(false, true, R.color.darkGray, R.color.black);
        }

        if(checkFontSize==R.id.radioFontOne){
            radioFont(true, false, false, R.color.black, R.color.gray, R.color.gray);
        }else if(checkFontSize==R.id.radioFontTwo){
            radioFont(false, true, false, R.color.gray, R.color.black, R.color.gray);
        }else if(checkFontSize==R.id.radioFontThree){
            radioFont(false, false, true, R.color.gray, R.color.gray, R.color.black);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        resetRadioButton();
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

        txtEllipsis.setOnClickListener(this);
        txtPdf.setOnClickListener(this);
        txtExcel.setOnClickListener(this);
        txtTxt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ellipsis){
            setEllipsize();
            boolean check=!isEllipsize;
            changeEllipsize(check,"textEllipsize1");
            changeEllipsize(check,"textEllipsize2");
        }else if(v.getId()==R.id.pdf){
            makeExportDialog("pdf");
        }else if(v.getId()==R.id.excel){
            makeExportDialog("excel");
        }else if(v.getId()==R.id.txt){
            makeExportDialog("txt");
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if(group.getId()==R.id.radioLineDisplay){
            if(checkedId==R.id.radioLineSingle){
                radioLine(true, false, R.color.black, R.color.gray);
                changeTextLine(1,"textLine1");
                changeTextLine(1,"textLine2");
                checkLine=R.id.radioLineSingle;
            }else if(checkedId==R.id.radioLineMul){
                radioLine(false, true, R.color.darkGray, R.color.black);
                changeTextLine(2,"textLine1");
                changeTextLine(2,"textLine2");
                checkLine=R.id.radioLineMul;
            }
        }

        else if(group.getId()==R.id.radioFont){
            if(checkedId==R.id.radioFontOne){
                radioFont(true, false, false, R.color.black, R.color.gray, R.color.gray);
                changeFont(15,"fontSize1");
                changeFont(15,"fontSize2");
                changeFont(12,"fontSize3");
                changeFont(12,"fontSize4");
                checkFontSize=R.id.radioFontOne;
            }else if(checkedId==R.id.radioFontTwo){
                radioFont(false, true, false, R.color.gray, R.color.black, R.color.gray);
                changeFont(18,"fontSize1");
                changeFont(18,"fontSize2");
                changeFont(15,"fontSize3");
                changeFont(15,"fontSize4");
                checkFontSize=R.id.radioFontTwo;
            }else if(checkedId==R.id.radioFontThree){
                radioFont(false, false, true, R.color.gray, R.color.gray, R.color.black);
                changeFont(21,"fontSize1");
                changeFont(21,"fontSize2");
                changeFont(18,"fontSize3");
                changeFont(18,"fontSize4");
                checkFontSize=R.id.radioFontThree;
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

    public void changeFont(float size, String key){
        Bundle bundle=new Bundle();
        bundle.putFloat("fontSize",size);

        getParentFragmentManager().setFragmentResult(key, bundle);
    }

   public void changeTextLine(int line, String key){
       Bundle bundle=new Bundle();
       bundle.putInt("textLine", line);

       getParentFragmentManager().setFragmentResult(key, bundle);
   }

   public void setEllipsize(){
        if(isEllipsize){
            txtEllipsis.setTextColor(ContextCompat.getColor(getContext(),R.color.black));
            isEllipsize=false;
        }else{
            txtEllipsis.setTextColor(ContextCompat.getColor(getContext(),R.color.gray));
            isEllipsize=true;
        }
   }

    public void changeEllipsize(boolean check, String key){
        Bundle bundle=new Bundle();
        bundle.putBoolean("textEllipsize", check);

        getParentFragmentManager().setFragmentResult(key, bundle);
    }

    public void resetRadioButton(){
        SharedPreferences preferences= getActivity().getSharedPreferences("settingInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean("isEllipsize", !isEllipsize);
        editor.putInt("checkLine", checkLine);
        editor.putInt("checkFontSize", checkFontSize);

        editor.apply();
    }

    public void makeExportDialog(String file){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setMessage(getResources().getText(R.string.doExport));
        builder.setPositiveButton(getResources().getText(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(file=="pdf"){
                   exportPdf();
                }else if(file=="excel"){
                    exportExcel();
                }else if(file=="txt"){
                    exportTxt();
                }
            }
        });
        builder.setNegativeButton(getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public void exportPdf(){

    }

    public void exportExcel(){

    }

    public void exportTxt(){

    }

}