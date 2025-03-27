package com.example.happybankbook.view;

import static android.app.Activity.RESULT_OK;

import static com.example.happybankbook.Utils.logDebugData;
import static com.example.happybankbook.Utils.showToastOnUi;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_TEXT_LINE;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_TEXT_SIZE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_MEMO_TEXT_SIZE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_RECYCLERVIEW_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_RECYCLERVIEW_TEXT_LINE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_RECYCLERVIEW_TEXT_SIZE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_SEARCH_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_SEARCH_TEXT_LINE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_SEARCH_TEXT_SIZE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_VIEWPAGER_TEXT_SIZE;
import static com.example.happybankbook.constants.PreferencesDefaults.PREF_DEFAULT_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_HAS_ELLIPSIZE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_LINE_TEXT_ID;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_SIZE_TEXT_ID;
import static com.example.happybankbook.constants.PreferencesNames.PREF_NAME_SET_STYLE;
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
import android.os.Environment;
import android.os.ParcelFileDescriptor;
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

import com.example.happybankbook.PdfRunnable;
import com.example.happybankbook.R;
import com.example.happybankbook.db.RoomDB;
import com.example.happybankbook.presenter.OutputPresenter;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SettingFragment extends Fragment
        implements View.OnClickListener, RadioGroup.OnCheckedChangeListener
{

    private static final String LOG_TAG="FILE";
    private final String PERMISSION= Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private Context mContext;
    private Activity mActivity;
    private OutputPresenter presenter;
    private ActivityResultLauncher<String> requestPermissionLauncher ;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private FileRunnable fileRunnable;
    private Thread fileThread;
    private ParcelFileDescriptor pfd;

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

    public <T> void exportPdf(T path){
        presenter.getConvertedPdf(RoomDB.getInstance(mContext).memoDao());
        presenter.setMemoDataListCallback(list -> {
            PdfRunnable runnable=null;
            if(path instanceof Uri){
                runnable=new PdfRunnable(list, mContext, Uri.parse(String.valueOf(path)));
            }else if(path instanceof String){
                runnable=new PdfRunnable(list, mContext, String.valueOf(path));
            }
            Thread thread=new Thread(runnable);
            thread.start();
        });
    }

    public <T> void exportTxtFile(char split,T path){
        presenter.getConvertedFile(RoomDB.getInstance(mContext).memoDao(),split);

        presenter.setStringBufferResultCallback(stringBuffer -> {
            buffer=stringBuffer;
            if(path instanceof Uri){
                fileRunnable=new FileRunnable(buffer, Uri.parse(String.valueOf(path)));
            }else if(path instanceof String){
                fileRunnable=new FileRunnable(buffer, String.valueOf(path));
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

        presenter=new OutputPresenter();
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

    public void resetRadioButton(){
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
            changeEllipsize(isCheckEllipsize,REQUEST_KEY_RECYCLERVIEW_TEXT_ELLIPSIZE);
            changeEllipsize(isCheckEllipsize,REQUEST_KEY_SEARCH_TEXT_ELLIPSIZE);
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

    public void setEllipsize(){
        if(hasEllipsize){
            ellipsisTextView.setTextColor(ContextCompat.getColor(mContext,R.color.black));
            hasEllipsize=false;
        }else{
            ellipsisTextView.setTextColor(ContextCompat.getColor(mContext,R.color.gray));
            hasEllipsize=true;
        }
    }

    public void changeEllipsize(boolean check, String key){
        Bundle bundle=new Bundle();
        bundle.putBoolean(BUNDLE_KEY_TEXT_ELLIPSIZE, check);

        getParentFragmentManager().setFragmentResult(key, bundle);
    }

    public void makeExportDialog(int androidVersion, String type){
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
            presenter.getDataCount(RoomDB.getInstance(mContext).memoDao());
        });
        builder.setNegativeButton(
                getResources().getText(R.string.cancel),
                (dialog, which)-> dialog.dismiss()
        );
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public void showManual(){
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
                changeTextLine(TEXT_LINE_SINGLE, REQUEST_KEY_RECYCLERVIEW_TEXT_LINE);
                changeTextLine(TEXT_LINE_SINGLE,REQUEST_KEY_SEARCH_TEXT_LINE);
                currentTextLineId=R.id.radioLineSingle;
            }else if(checkedId==R.id.radioLineMul){
                setTextLine(false, true, R.color.gray, R.color.black);
                changeTextLine(TEXT_LINE_DEFAULT, REQUEST_KEY_RECYCLERVIEW_TEXT_LINE);
                changeTextLine(TEXT_LINE_DEFAULT, REQUEST_KEY_SEARCH_TEXT_LINE);
                currentTextLineId=R.id.radioLineMul;
            }
        }

        else if(group.getId()==R.id.radioFont){
            if(checkedId==R.id.radioFontOne){
                setTextSize(true, false, false, R.color.black, R.color.gray, R.color.gray);
                changeTextSize(TEXT_SIZE_DEFAULT_SMALL, REQUEST_KEY_VIEWPAGER_TEXT_SIZE);
                changeTextSize(TEXT_SIZE_DEFAULT_SMALL, REQUEST_KEY_MEMO_TEXT_SIZE);
                changeTextSize(TEXT_SIZE_DEFAULT_LARGE, REQUEST_KEY_RECYCLERVIEW_TEXT_SIZE);
                changeTextSize(TEXT_SIZE_DEFAULT_LARGE, REQUEST_KEY_SEARCH_TEXT_SIZE);
                currentTextSizeId=R.id.radioFontOne;
            }else if(checkedId==R.id.radioFontTwo){
                setTextSize(false, true, false, R.color.gray, R.color.black, R.color.gray);
                changeTextSize(TEXT_SIZE_DEFAULT_LARGE, REQUEST_KEY_VIEWPAGER_TEXT_SIZE);
                changeTextSize(TEXT_SIZE_DEFAULT_LARGE, REQUEST_KEY_MEMO_TEXT_SIZE);
                changeTextSize(TEXT_SIZE_MEDIUM, REQUEST_KEY_RECYCLERVIEW_TEXT_SIZE);
                changeTextSize(TEXT_SIZE_MEDIUM, REQUEST_KEY_SEARCH_TEXT_SIZE);
                currentTextSizeId=R.id.radioFontTwo;
            }else if(checkedId==R.id.radioFontThree){
                setTextSize(false, false, true, R.color.gray, R.color.gray, R.color.black);
                changeTextSize(TEXT_SIZE_MEDIUM, REQUEST_KEY_VIEWPAGER_TEXT_SIZE);
                changeTextSize(TEXT_SIZE_MEDIUM, REQUEST_KEY_MEMO_TEXT_SIZE);
                changeTextSize(TEXT_SIZE_LARGE, REQUEST_KEY_RECYCLERVIEW_TEXT_SIZE);
                changeTextSize(TEXT_SIZE_LARGE, REQUEST_KEY_SEARCH_TEXT_SIZE);
                currentTextSizeId=R.id.radioFontThree;
            }
        }

    }

    public void setTextLine(boolean b1, boolean b2, int c1, int c2){
        singleLineRadioButton.setChecked(b1);
        multiLineRadioButton.setChecked(b2);
        singleLineRadioButton.setTextColor(ContextCompat.getColor(mContext,c1));
        multiLineRadioButton.setTextColor(ContextCompat.getColor(mContext,c2));
    }

    public void setTextSize(boolean b1, boolean b2, boolean b3, int c1, int c2, int c3){
        textSizeOneRadioButton.setChecked(b1);
        textSizeTwoRadioButton.setChecked(b2);
        textSizeThreeRadioButton.setChecked(b3);
        textSizeOneRadioButton.setTextColor(ContextCompat.getColor(mContext,c1));
        textSizeTwoRadioButton.setTextColor(ContextCompat.getColor(mContext,c2));
        textSizeThreeRadioButton.setTextColor(ContextCompat.getColor(mContext,c3));
    }

    public void changeTextSize(float size, String key){
        Bundle bundle=new Bundle();
        bundle.putFloat(BUNDLE_KEY_TEXT_SIZE,size);

        getParentFragmentManager().setFragmentResult(key, bundle);
    }

   public void changeTextLine(int line, String key){
       Bundle bundle=new Bundle();
       bundle.putInt(BUNDLE_KEY_TEXT_LINE, line);

       getParentFragmentManager().setFragmentResult(key, bundle);
   }

    private class FileRunnable implements Runnable{

        private final StringBuffer content;

        private String extension;
        private Uri uri;
        private final int branch;
        public FileRunnable(StringBuffer content, String extension){
            this.content=content;
            this.extension=extension;
            branch=1;
        }
        public FileRunnable(StringBuffer content, Uri uri){
            this.uri=uri;
            this.content=content;
            branch=2;
        }

        @Override
        public void run() {
            if(branch==1){
                makeFile(content, extension);
            }else if(branch==2){
                makeFile(uri, content);
            }
        }

    }

    public void makeFile(StringBuffer content, String extension){
        File file=getDirectory(extension);

        FileWriter fw=null;
        BufferedWriter writer=null;

        try{
            boolean hasFile=file.createNewFile();
            if(hasFile){
                showToastOnUi(mContext, R.string.savePermissionYes);
                fw = new FileWriter(file);
                writer = new BufferedWriter(fw);
                String contentStr = String.valueOf(content);
                writer.write(contentStr);
            }
        }catch(IOException e){
            logDebugData(LOG_TAG,"파일 생성 실패: "+e);
        }finally{
            try {
                if(writer!=null){writer.flush();writer.close();}
                if(fw!=null){fw.flush();fw.close();}
            }catch (IOException e2){
                logDebugData(LOG_TAG,"파일 리소스 닫기: "+e2);
            }
        }

        showToastOnUi(mContext, R.string.completeSaving);
    }

    public void makeFile(Uri uri, StringBuffer content){
        pfd=null;
        FileOutputStream fileOutputStream=null;
        BufferedWriter bufferedWriter=null;

        try{
            showToastOnUi(mContext, R.string.savePermissionYes);
            fileOutputStream=getDirectory(uri, mContext);
            bufferedWriter=new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            String contentStr = String.valueOf(content);
            bufferedWriter.write(contentStr);
        }catch(IOException e){
            logDebugData(LOG_TAG,"uri로 파일 생성 실패: "+e);
        }finally{
            try {
                if(bufferedWriter!=null){bufferedWriter.flush();bufferedWriter.close();}
                if(fileOutputStream!=null){fileOutputStream.flush();fileOutputStream.close();}
                if(pfd!=null){pfd.close();}
            }catch (IOException e2){
                logDebugData(LOG_TAG,"uri 생성 파일 리소스 닫기: "+e2);
            }
        }

        showToastOnUi(mContext, R.string.completeSaving);
    }

    public File getDirectory(String extension){
        final String directoryName="/HappyBank";
        final String pathName=Environment.getExternalStorageDirectory().getAbsolutePath();
        File directory = new File(pathName+directoryName);
        int count=0;

        if (!directory.exists()) {
            boolean isSuccess=directory.mkdirs();
            if(!isSuccess){
                logDebugData(LOG_TAG,"directory 생성 실패");
            }
        }

        File[] files = directory.listFiles();
        if(files!=null){
            count=files.length;
        }

        final String fileName="happy bank memo";
        return new File(directory, fileName+"_"+(count+1) + extension);
    }

    public FileOutputStream getDirectory(Uri uri, Context context) {
        FileOutputStream fileOutputStream=null;
        try {
            pfd = context.getContentResolver().openFileDescriptor(uri, "w");
            if(pfd!=null) fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
        } catch (IOException e) {
            logDebugData(LOG_TAG,"uri로 directory 얻기: "+e);
        }
        return fileOutputStream;
    }

}