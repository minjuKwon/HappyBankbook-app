package com.example.happybankbook.view;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.happybankbook.presenterReturnInterface.GetReturnMemoDataList;
import com.example.happybankbook.presenterReturnInterface.GetReturnStringBuffer;
import com.example.happybankbook.MainActivity;
import com.example.happybankbook.PdfRunnable;
import com.example.happybankbook.R;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.db.RoomDB;
import com.example.happybankbook.presenter.OutputPresenter;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SettingFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener{

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
        public FileRunnable(Uri uri, StringBuffer content){
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

    private ActivityResultLauncher<String> requestPermissionLauncher ;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private TextView txtEllipsis;
    private RadioButton radioSingle, radioMull, radioOne, radioTwo, radioThree;

    private OutputPresenter presenter;
    private FileRunnable fileRunnable;
    private Thread fileThread;

    private final String PERMISSION= Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private String fileExtension;
    private boolean isEllipsize=false;
    private int checkLine, checkFontSize;

    private StringBuffer buffer;
    private ParcelFileDescriptor pfd;

    private Context mContext;
    private Activity mActivity;

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
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), check -> {
                    if(check){
                        Toast.makeText(getContext(),getResources().getText(R.string.savePermissionYes),Toast.LENGTH_SHORT).show();
                        if("pdf".equals(fileExtension)){
                            exportPdf(".pdf");
                        }else if("excel".equals(fileExtension)){
                            exportTxtFile(',',".csv");
                        }else if("txt".equals(fileExtension)){
                            exportTxtFile(' ',".txt");
                        }
                    }else{
                        Toast.makeText(getContext(),getResources().getText(R.string.savePermissionNo),Toast.LENGTH_LONG).show();
                    }
                });

        activityResultLauncher
                =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result->{
            if(result.getResultCode()==RESULT_OK){
                Uri uri=result.getData().getData();
                Toast.makeText(getContext(),getResources().getText(R.string.savePermissionYes),Toast.LENGTH_SHORT).show();
                if("pdf".equals(fileExtension)){
                    exportPdf(uri);
                }else if("excel".equals(fileExtension)){
                    exportTxtFile(uri,',');
                }else if("txt".equals(fileExtension)){
                    exportTxtFile(uri,' ');
                }
            }
        });

    }

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

        SharedPreferences preferences= mActivity.getSharedPreferences(getResources().getString(R.string.settingInfo),Context.MODE_PRIVATE);
        isEllipsize=preferences.getBoolean(getResources().getString(R.string.isEllipsize),true);
        checkLine=preferences.getInt(getResources().getString(R.string.checkLine),R.id.radioLineMul);
        checkFontSize=preferences.getInt(getResources().getString(R.string.checkFontSize),R.id.radioFontOne);

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
        presenter.releaseView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext=null;
        mActivity=null;
    }

    private void init(View view){
        TextView txtManual=view.findViewById(R.id.manual);
        txtEllipsis=view.findViewById(R.id.ellipsis);
        TextView txtPdf=view.findViewById(R.id.pdf);
        TextView txtExcel=view.findViewById(R.id.excel);
        TextView txtTxt=view.findViewById(R.id.txt);
        TextView txtOpenSource=view.findViewById(R.id.openSource);
        RadioGroup radioGroupLine=view.findViewById(R.id.radioLineDisplay);
        radioSingle=view.findViewById(R.id.radioLineSingle);
        radioMull=view.findViewById(R.id.radioLineMul);
        RadioGroup radioGroupFont=view.findViewById(R.id.radioFont);
        radioOne=view.findViewById(R.id.radioFontOne);
        radioTwo=view.findViewById(R.id.radioFontTwo);
        radioThree=view.findViewById(R.id.radioFontThree);

        presenter=new OutputPresenter();

        radioSingle.setChecked(false);
        radioMull.setChecked(true);

        radioOne.setChecked(true);
        radioTwo.setChecked(false);
        radioThree.setChecked(false);

        radioGroupLine.setOnCheckedChangeListener(this);
        radioGroupFont.setOnCheckedChangeListener(this);

        txtManual.setOnClickListener(this);
        txtEllipsis.setOnClickListener(this);
        txtPdf.setOnClickListener(this);
        txtExcel.setOnClickListener(this);
        txtTxt.setOnClickListener(this);
        txtOpenSource.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final String pdfType="application/pdf";
        final String csvType="text/comma-separated-values";
        final String txtType="text/plain";

        if(v.getId()==R.id.ellipsis){
            setEllipsize();
            boolean check=!isEllipsize;
            changeEllipsize(check,getResources().getString(R.string.textEllipsize1));
            changeEllipsize(check,getResources().getString(R.string.textEllipsize2));
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
            startActivity(new Intent(getContext(), OssLicensesMenuActivity.class));
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if(group.getId()==R.id.radioLineDisplay){
            if(checkedId==R.id.radioLineSingle){
                radioLine(true, false, R.color.black, R.color.gray);
                changeTextLine(1,getResources().getString(R.string.textLine1));
                changeTextLine(1,getResources().getString(R.string.textLine2));
                checkLine=R.id.radioLineSingle;
            }else if(checkedId==R.id.radioLineMul){
                radioLine(false, true, R.color.darkGray, R.color.black);
                changeTextLine(2,getResources().getString(R.string.textLine1));
                changeTextLine(2,getResources().getString(R.string.textLine2));
                checkLine=R.id.radioLineMul;
            }
        }

        else if(group.getId()==R.id.radioFont){
            if(checkedId==R.id.radioFontOne){
                radioFont(true, false, false, R.color.black, R.color.gray, R.color.gray);
                changeFont(15,getResources().getString(R.string.fontSize1));
                changeFont(15,getResources().getString(R.string.fontSize2));
                changeFont(12,getResources().getString(R.string.fontSize3));
                changeFont(12,getResources().getString(R.string.fontSize4));
                checkFontSize=R.id.radioFontOne;
            }else if(checkedId==R.id.radioFontTwo){
                radioFont(false, true, false, R.color.gray, R.color.black, R.color.gray);
                changeFont(18,getResources().getString(R.string.fontSize1));
                changeFont(18,getResources().getString(R.string.fontSize2));
                changeFont(15,getResources().getString(R.string.fontSize3));
                changeFont(15,getResources().getString(R.string.fontSize4));
                checkFontSize=R.id.radioFontTwo;
            }else if(checkedId==R.id.radioFontThree){
                radioFont(false, false, true, R.color.gray, R.color.gray, R.color.black);
                changeFont(21,getResources().getString(R.string.fontSize1));
                changeFont(21,getResources().getString(R.string.fontSize2));
                changeFont(18,getResources().getString(R.string.fontSize3));
                changeFont(18,getResources().getString(R.string.fontSize4));
                checkFontSize=R.id.radioFontThree;
            }
        }

    }

    public void showManual(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setMessage(getResources().getText(R.string.manualDialog));
        builder.setNeutralButton(getResources().getText(R.string.close), (dialog, which)-> dialog.dismiss());
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public void radioLine(boolean b1, boolean b2, int c1, int c2){
        radioSingle.setChecked(b1);
        radioMull.setChecked(b2);
        radioSingle.setTextColor(ContextCompat.getColor(mContext,c1));
        radioMull.setTextColor(ContextCompat.getColor(mContext,c2));
    }

    public void radioFont(boolean b1, boolean b2, boolean b3, int c1, int c2, int c3){
        radioOne.setChecked(b1);
        radioTwo.setChecked(b2);
        radioThree.setChecked(b3);
        radioOne.setTextColor(ContextCompat.getColor(mContext,c1));
        radioTwo.setTextColor(ContextCompat.getColor(mContext,c2));
        radioThree.setTextColor(ContextCompat.getColor(mContext,c3));
    }

    public void changeFont(float size, String key){
        Bundle bundle=new Bundle();
        bundle.putFloat(getResources().getString(R.string.fontSize),size);

        getParentFragmentManager().setFragmentResult(key, bundle);
    }

   public void changeTextLine(int line, String key){
       Bundle bundle=new Bundle();
       bundle.putInt(getResources().getString(R.string.textLine), line);

       getParentFragmentManager().setFragmentResult(key, bundle);
   }

   public void setEllipsize(){
        if(isEllipsize){
            txtEllipsis.setTextColor(ContextCompat.getColor(mContext,R.color.black));
            isEllipsize=false;
        }else{
            txtEllipsis.setTextColor(ContextCompat.getColor(mContext,R.color.gray));
            isEllipsize=true;
        }
   }

    public void changeEllipsize(boolean check, String key){
        Bundle bundle=new Bundle();
        bundle.putBoolean(getResources().getString(R.string.textEllipsize), check);

        getParentFragmentManager().setFragmentResult(key, bundle);
    }

    public void resetRadioButton(){
        SharedPreferences preferences= mActivity.getSharedPreferences(getResources().getString(R.string.settingInfo), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(getResources().getString(R.string.isEllipsize), !isEllipsize);
        editor.putInt(getResources().getString(R.string.checkLine), checkLine);
        editor.putInt(getResources().getString(R.string.checkFontSize), checkFontSize);

        editor.apply();
    }

    public void makeExportDialog(int androidVersion, String type){
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setMessage(fileExtension+" "+getResources().getText(R.string.doExport));
        builder.setPositiveButton(getResources().getText(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(androidVersion>=Build.VERSION_CODES.Q){
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType(type);
                    intent.putExtra(Intent.EXTRA_TITLE,"happy bank memo");
                    activityResultLauncher.launch(intent);
                }else{
                    requestPermissionLauncher.launch(PERMISSION);
                }
            }
        });
        builder.setNegativeButton(getResources().getText(R.string.cancel), (dialog, which)-> dialog.dismiss());
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public void exportPdf(String extension){
        presenter.getDataToPdf(RoomDB.getInstance(getContext()).memoDao());
        presenter.setReturnMemoDataList(new GetReturnMemoDataList() {
            @Override
            public void getMemoDataList(ArrayList<MemoData> list) {
                PdfRunnable runnable=new PdfRunnable(list, getContext(), extension);
                Thread thread=new Thread(runnable);
                thread.start();
            }
        });
    }

    public void exportPdf(Uri uri){
        presenter.getDataToPdf(RoomDB.getInstance(getContext()).memoDao());
        presenter.setReturnMemoDataList(new GetReturnMemoDataList() {
            @Override
            public void getMemoDataList(ArrayList<MemoData> list) {
                PdfRunnable runnable=new PdfRunnable(list, getContext(),uri);
                Thread thread=new Thread(runnable);
                thread.start();
            }
        });
    }

    public void exportTxtFile(char split, String extension){
        buffer=new StringBuffer();
        presenter.getDataToFile(RoomDB.getInstance(getContext()).memoDao(),split);

        presenter.setGetReturnValue(new GetReturnStringBuffer() {
            @Override
            public void getStringBuffer(StringBuffer stringBuffer) {
                buffer=stringBuffer;
                fileRunnable=new FileRunnable(buffer, extension);
                fileThread=new Thread(fileRunnable);
                fileThread.start();
            }
        });
    }

    public void exportTxtFile(Uri uri, char split){
        buffer=new StringBuffer();
        presenter.getDataToFile(RoomDB.getInstance(getContext()).memoDao(),split);

        presenter.setGetReturnValue(new GetReturnStringBuffer() {
            @Override
            public void getStringBuffer(StringBuffer stringBuffer) {
                buffer=stringBuffer;
                fileRunnable=new FileRunnable(uri, buffer);
                fileThread=new Thread(fileRunnable);
                fileThread.start();
            }
        });
    }

    public void makeFile(Uri uri, StringBuffer content){
        pfd=null;
        FileOutputStream fileOutputStream=null;

        try{
            String strContent = String.valueOf(content);
            fileOutputStream=getDirectory(uri, mContext);
            if("null".equals(strContent)||"".equals(strContent)){
                Toast.makeText(getContext(),getResources().getText(R.string.noMemo),Toast.LENGTH_LONG).show();
            }else{
                fileOutputStream.write(strContent.getBytes());
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try {
                if(fileOutputStream!=null){fileOutputStream.close();}
                if(pfd!=null){pfd.close();}
            }catch (IOException e2){
                e2.printStackTrace();
            }
        }

        ((MainActivity)mContext).runOnUiThread(()->Toast.makeText(getContext(),getResources().getText(R.string.completeSaving),Toast.LENGTH_SHORT).show());

    }

    public void makeFile(StringBuffer content, String extension){
        File file=getDirectory(extension);

        FileWriter fw=null;
        BufferedWriter writer=null;

        try{
            file.createNewFile();

            fw = new FileWriter(file);
            writer = new BufferedWriter(fw);

            String strContent = String.valueOf(content);
            if("null".equals(strContent)||"".equals(strContent)){
               Toast.makeText(getContext(),getResources().getText(R.string.noMemo),Toast.LENGTH_LONG).show();
            }else{
                writer.write(strContent);
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try {
                if(writer!=null){writer.close();}
                if(fw!=null){fw.close();}
            }catch (IOException e2){
                e2.printStackTrace();
            }
        }

        ((MainActivity)mContext).runOnUiThread(()-> Toast.makeText(getContext(),getResources().getText(R.string.completeSaving),Toast.LENGTH_SHORT).show());

    }

    public FileOutputStream getDirectory(Uri uri, Context context) {
        FileOutputStream fileOutputStream=null;
        try {
            pfd = context.getContentResolver().openFileDescriptor(uri, "w");
            fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileOutputStream;
    }

    public File getDirectory(String extension){
        final String directoryName="/HappyBank";
        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+directoryName);
        int count=0;

        if (!directory.exists()) {
            directory.mkdirs();
        }

        if(directory.listFiles()!=null){
            count=directory.listFiles().length;
        }

        final String fileName="happy bank memo";
        File file = new File(directory, fileName+"_"+(count+1) + extension);
        return file;
    }

}