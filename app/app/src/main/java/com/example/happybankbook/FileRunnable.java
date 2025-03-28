package com.example.happybankbook;

import static com.example.happybankbook.Utils.logDebugData;
import static com.example.happybankbook.Utils.showToastOnUi;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileRunnable implements Runnable{

    private static final String LOG_TAG="FILE";

    private  Context mContext;
    private Uri uri;
    private ParcelFileDescriptor pfd;
    private StringBuffer content;
    private int branch;
    private String extension;


    public FileRunnable(){

    }

    public FileRunnable(Context context, StringBuffer content, String extension){
        this.mContext=context;
        this.content=content;
        this.extension=extension;
        branch=1;
    }

    public FileRunnable(Context context, StringBuffer content, Uri uri){
        this.mContext=context;
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

    private void makeFile(StringBuffer content, String extension){
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

    private void makeFile(Uri uri, StringBuffer content){
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
        final String pathName= Environment.getExternalStorageDirectory().getAbsolutePath();
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