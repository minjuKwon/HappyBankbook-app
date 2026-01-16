package com.example.happybankbook.view;

import static android.app.Activity.RESULT_OK;

import static com.example.happybankbook.Utils.showToastOnUi;
import static com.example.happybankbook.constants.PreferencesDefaults.PREF_DEFAULT_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_HAS_ELLIPSIZE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_LINE_TEXT_ID;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_SIZE_TEXT_ID;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_LINE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_SIZE;
import static com.example.happybankbook.constants.PreferencesNames.PREF_NAME_LIST_TEXT_STYLE;
import static com.example.happybankbook.constants.PreferencesNames.PREF_NAME_MEMO_TEXT_STYLE;
import static com.example.happybankbook.constants.PreferencesNames.PREF_NAME_SEARCH_TEXT_STYLE;
import static com.example.happybankbook.constants.PreferencesNames.PREF_NAME_SET_STYLE;
import static com.example.happybankbook.constants.PreferencesNames.PREF_NAME_VIEWPAGER_TEXT_STYLE;
import static com.example.happybankbook.constants.TextStyles.TEXT_LINE_DEFAULT;
import static com.example.happybankbook.constants.TextStyles.TEXT_LINE_SINGLE;
import static com.example.happybankbook.constants.TextStyles.TEXT_SIZE_DEFAULT_LARGE;
import static com.example.happybankbook.constants.TextStyles.TEXT_SIZE_DEFAULT_SMALL;
import static com.example.happybankbook.constants.TextStyles.TEXT_SIZE_LARGE;
import static com.example.happybankbook.constants.TextStyles.TEXT_SIZE_MEDIUM;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.happybankbook.runnable.FileRunnable;
import com.example.happybankbook.runnable.PdfRunnable;
import com.example.happybankbook.R;
import com.example.happybankbook.presenter.OutputPresenter;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingFragment extends Fragment
        implements View.OnClickListener, RadioGroup.OnCheckedChangeListener
{
    @Inject
    OutputPresenter presenter;

    private final String PERMISSION= Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private Context mContext;
    private Activity mActivity;
    private ActivityResultLauncher<String> requestPermissionLauncher ;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private FileRunnable fileRunnable;
    private Thread fileThread;

    private TextView ellipsisTextView, manualTextView, pdfTextView, excelTextView, txtTextView,
            openSourceTextView;
    private RadioGroup radioGroupLine, radioGroupTextSize;
    private RadioButton singleLineRadioButton, multiLineRadioButton, textSizeOneRadioButton,
            textSizeTwoRadioButton, textSizeThreeRadioButton;

    private boolean hasEllipsize=false;
    private int currentTextLineId, currentTextSizeId;
    private String fileExtension;
    private StringBuffer buffer;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
        if(context instanceof Activity){
            mActivity=(Activity)context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(), check -> {
                    if(check){
                        if("pdf".equals(fileExtension)){
                            exportPdf(".pdf");
                        }else if("excel".equals(fileExtension)){
                            exportTxtFile(',',".csv");
                        }else if("txt".equals(fileExtension)){
                            exportTxtFile(' ',".txt");
                        }
                    }else{
                        Toast.makeText(
                                mContext,
                                getResources().getText(R.string.savePermissionNo),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

        activityResultLauncher
                =registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(), result->{
            if(result.getResultCode()==RESULT_OK&&result.getData()!=null){
                Uri uri=result.getData().getData();
                if("pdf".equals(fileExtension)){
                    exportPdf(uri);
                }else if("excel".equals(fileExtension)){
                    exportTxtFile(',', uri);
                }else if("txt".equals(fileExtension)){
                    exportTxtFile(' ', uri);
                }
            }else{
                Toast.makeText(
                        mContext,
                        getResources().getText(R.string.savePermissionNo),
                        Toast.LENGTH_LONG
                ).show();
            }
        });

    }

    private <T> void exportPdf(T path){
        presenter.getConvertedPdf();
        presenter.setMemoDataListCallback(list -> {
            PdfRunnable runnable=null;
            if(path instanceof Uri){
                runnable=new PdfRunnable(mContext, list, Uri.parse(String.valueOf(path)), new FileRunnable());
            }else if(path instanceof String){
                runnable=new PdfRunnable(mContext, list, String.valueOf(path), new FileRunnable());
            }
            Thread thread=new Thread(runnable);
            thread.start();
        });
    }

    private <T> void exportTxtFile(char split,T path){
        presenter.getConvertedFile(split);

        presenter.setStringBufferResultCallback(stringBuffer -> {
            buffer=stringBuffer;
            if(path instanceof Uri){
                fileRunnable=new FileRunnable(mContext, buffer, Uri.parse(String.valueOf(path)));
            }else if(path instanceof String){
                fileRunnable=new FileRunnable(mContext, buffer, String.valueOf(path));
            }
            fileThread=new Thread(fileRunnable);
            fileThread.start();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_setting, container, false);
        initViews(view);

        setCheckedRadioButton();
        setListeners();

        return view;
    }

    private void initViews(View view){
        manualTextView=view.findViewById(R.id.manual);
        ellipsisTextView=view.findViewById(R.id.ellipsis);
        pdfTextView=view.findViewById(R.id.pdf);
        excelTextView=view.findViewById(R.id.excel);
        txtTextView=view.findViewById(R.id.txt);
        openSourceTextView=view.findViewById(R.id.openSource);
        radioGroupLine=view.findViewById(R.id.radioLineDisplay);
        singleLineRadioButton=view.findViewById(R.id.radioLineSingle);
        multiLineRadioButton=view.findViewById(R.id.radioLineMul);
        radioGroupTextSize=view.findViewById(R.id.radioFont);
        textSizeOneRadioButton=view.findViewById(R.id.radioFontOne);
        textSizeTwoRadioButton=view.findViewById(R.id.radioFontTwo);
        textSizeThreeRadioButton=view.findViewById(R.id.radioFontThree);
    }

    private void setCheckedRadioButton(){
        singleLineRadioButton.setChecked(false);
        multiLineRadioButton.setChecked(true);

        textSizeOneRadioButton.setChecked(true);
        textSizeTwoRadioButton.setChecked(false);
        textSizeThreeRadioButton.setChecked(false);
    }

    private void setListeners(){
        radioGroupLine.setOnCheckedChangeListener(this);
        radioGroupTextSize.setOnCheckedChangeListener(this);

        manualTextView.setOnClickListener(this);
        ellipsisTextView.setOnClickListener(this);
        pdfTextView.setOnClickListener(this);
        excelTextView.setOnClickListener(this);
        txtTextView.setOnClickListener(this);
        openSourceTextView.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTextStyle();
    }

    private void setTextStyle(){
        SharedPreferences preferences=
                mActivity.getSharedPreferences(PREF_NAME_SET_STYLE,Context.MODE_PRIVATE);
        hasEllipsize=preferences.getBoolean(PREF_KEY_HAS_ELLIPSIZE, PREF_DEFAULT_TEXT_ELLIPSIZE);
        currentTextLineId=preferences.getInt(PREF_KEY_LINE_TEXT_ID, R.id.radioLineMul);
        currentTextSizeId=preferences.getInt(PREF_KEY_SIZE_TEXT_ID, R.id.radioFontOne);

        setEllipsize();

        if(currentTextLineId==R.id.radioLineSingle){
            setTextLine(true, false, R.color.black, R.color.gray);
        }else if(currentTextLineId==R.id.radioLineMul){
            setTextLine(false, true, R.color.gray, R.color.black);
        }

        if(currentTextSizeId==R.id.radioFontOne){
            setTextSize(true, false, false, R.color.black, R.color.gray, R.color.gray);
        }else if(currentTextSizeId==R.id.radioFontTwo){
            setTextSize(false, true, false, R.color.gray, R.color.black, R.color.gray);
        }else if(currentTextSizeId==R.id.radioFontThree){
            setTextSize(false, false, true, R.color.gray, R.color.gray, R.color.black);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        resetRadioButton();
        presenter.releaseView();
    }

    private void resetRadioButton(){
        SharedPreferences preferences=
                mActivity.getSharedPreferences(PREF_NAME_SET_STYLE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(PREF_KEY_HAS_ELLIPSIZE, !hasEllipsize);
        editor.putInt(PREF_KEY_LINE_TEXT_ID, currentTextLineId);
        editor.putInt(PREF_KEY_SIZE_TEXT_ID, currentTextSizeId);

        editor.apply();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext=null;
        mActivity=null;
    }

    @Override
    public void onClick(View v) {
        final String pdfType="application/pdf";
        final String csvType="text/comma-separated-values";
        final String txtType="text/plain";

        if(v.getId()==R.id.ellipsis){
            setEllipsize();
            boolean isCheckEllipsize=!hasEllipsize;
            changeEllipsize(isCheckEllipsize,PREF_NAME_LIST_TEXT_STYLE);
            changeEllipsize(isCheckEllipsize,PREF_NAME_SEARCH_TEXT_STYLE);
        }else if(v.getId()==R.id.pdf){
            fileExtension="pdf";
            makeExportDialog(Build.VERSION.SDK_INT, pdfType);
        }else if(v.getId()==R.id.excel){
            fileExtension="excel";
            makeExportDialog(Build.VERSION.SDK_INT, csvType);
        }else if(v.getId()==R.id.txt){
            fileExtension="txt";
            makeExportDialog(Build.VERSION.SDK_INT, txtType);
        }else if(v.getId()==R.id.manual){
            showManual();
        }else if(v.getId()==R.id.openSource){
            startActivity(new Intent(mContext, OssLicensesMenuActivity.class));
        }
    }

    private void setEllipsize(){
        if(hasEllipsize){
            ellipsisTextView.setTextColor(ContextCompat.getColor(mContext,R.color.black));
            hasEllipsize=false;
        }else{
            ellipsisTextView.setTextColor(ContextCompat.getColor(mContext,R.color.gray));
            hasEllipsize=true;
        }
    }

    private void changeEllipsize(boolean check, String key){
        SharedPreferences preferences=
                mActivity.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(PREF_KEY_TEXT_ELLIPSIZE, check);
        editor.apply();
    }

    private void makeExportDialog(int androidVersion, String type){
        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        final String message=fileExtension+" "+getResources().getText(R.string.doExport);
        builder.setMessage(message);
        builder.setPositiveButton(getResources().getText(R.string.OK), (dialog, which) -> {
            presenter.setIntResultCallback(value -> {
                if(value==0){
                    showToastOnUi(mContext, R.string.noMemo);
                }else{
                    if(androidVersion>=Build.VERSION_CODES.Q){
                        final String fileTitle="happy bank memo";
                        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType(type);
                        intent.putExtra(Intent.EXTRA_TITLE, fileTitle);
                        activityResultLauncher.launch(intent);
                    }else{
                        requestPermissionLauncher.launch(PERMISSION);
                    }
                }
            });
            presenter.getDataCount();
        });
        builder.setNegativeButton(
                getResources().getText(R.string.cancel),
                (dialog, which)-> dialog.dismiss()
        );
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private void showManual(){
        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setMessage(getResources().getText(R.string.manualDialog));
        builder.setNeutralButton(
                getResources().getText(R.string.close),
                (dialog, which)-> dialog.dismiss()
        );
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if(group.getId()==R.id.radioLineDisplay){
            if(checkedId==R.id.radioLineSingle){
                setTextLine(true, false, R.color.black, R.color.gray);
                changeTextLine(TEXT_LINE_SINGLE, PREF_NAME_LIST_TEXT_STYLE);
                changeTextLine(TEXT_LINE_SINGLE, PREF_NAME_SEARCH_TEXT_STYLE);
                currentTextLineId=R.id.radioLineSingle;
            }else if(checkedId==R.id.radioLineMul){
                setTextLine(false, true, R.color.gray, R.color.black);
                changeTextLine(TEXT_LINE_DEFAULT, PREF_NAME_LIST_TEXT_STYLE);
                changeTextLine(TEXT_LINE_DEFAULT, PREF_NAME_SEARCH_TEXT_STYLE);
                currentTextLineId=R.id.radioLineMul;
            }
        }

        else if(group.getId()==R.id.radioFont){
            if(checkedId==R.id.radioFontOne){
                setTextSize(true, false, false, R.color.black, R.color.gray, R.color.gray);
                changeTextSize(TEXT_SIZE_DEFAULT_SMALL, PREF_NAME_VIEWPAGER_TEXT_STYLE);
                changeTextSize(TEXT_SIZE_DEFAULT_SMALL, PREF_NAME_MEMO_TEXT_STYLE);
                changeTextSize(TEXT_SIZE_DEFAULT_LARGE, PREF_NAME_LIST_TEXT_STYLE);
                changeTextSize(TEXT_SIZE_DEFAULT_LARGE, PREF_NAME_SEARCH_TEXT_STYLE);
                currentTextSizeId=R.id.radioFontOne;
            }else if(checkedId==R.id.radioFontTwo){
                setTextSize(false, true, false, R.color.gray, R.color.black, R.color.gray);
                changeTextSize(TEXT_SIZE_DEFAULT_LARGE, PREF_NAME_VIEWPAGER_TEXT_STYLE);
                changeTextSize(TEXT_SIZE_DEFAULT_LARGE, PREF_NAME_MEMO_TEXT_STYLE);
                changeTextSize(TEXT_SIZE_MEDIUM, PREF_NAME_LIST_TEXT_STYLE);
                changeTextSize(TEXT_SIZE_MEDIUM, PREF_NAME_SEARCH_TEXT_STYLE);
                currentTextSizeId=R.id.radioFontTwo;
            }else if(checkedId==R.id.radioFontThree){
                setTextSize(false, false, true, R.color.gray, R.color.gray, R.color.black);
                changeTextSize(TEXT_SIZE_MEDIUM, PREF_NAME_VIEWPAGER_TEXT_STYLE);
                changeTextSize(TEXT_SIZE_MEDIUM, PREF_NAME_MEMO_TEXT_STYLE);
                changeTextSize(TEXT_SIZE_LARGE, PREF_NAME_LIST_TEXT_STYLE);
                changeTextSize(TEXT_SIZE_LARGE, PREF_NAME_SEARCH_TEXT_STYLE);
                currentTextSizeId=R.id.radioFontThree;
            }

        }
    }

    private void setTextLine(boolean b1, boolean b2, int c1, int c2){
        singleLineRadioButton.setChecked(b1);
        multiLineRadioButton.setChecked(b2);
        singleLineRadioButton.setTextColor(ContextCompat.getColor(mContext,c1));
        multiLineRadioButton.setTextColor(ContextCompat.getColor(mContext,c2));
    }

    private void setTextSize(boolean b1, boolean b2, boolean b3, int c1, int c2, int c3){
        textSizeOneRadioButton.setChecked(b1);
        textSizeTwoRadioButton.setChecked(b2);
        textSizeThreeRadioButton.setChecked(b3);
        textSizeOneRadioButton.setTextColor(ContextCompat.getColor(mContext,c1));
        textSizeTwoRadioButton.setTextColor(ContextCompat.getColor(mContext,c2));
        textSizeThreeRadioButton.setTextColor(ContextCompat.getColor(mContext,c3));
    }

    private void changeTextSize(float size, String key){
        SharedPreferences preferences=
                mActivity.getSharedPreferences(key,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putFloat(PREF_KEY_TEXT_SIZE, size);
        editor.apply();
    }

    private void changeTextLine(int line, String key){
        SharedPreferences preferences=
                mActivity.getSharedPreferences(key,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putInt(PREF_KEY_TEXT_LINE, line);
        editor.apply();
   }

}