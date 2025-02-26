package com.example.happybankbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.view.SettingFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PdfRunnable implements Runnable{

    private final Context mContext;
    private final int branch;
    private final ArrayList<MemoData> dataList;
    private Uri uri;
    private String extension;

    public PdfRunnable(ArrayList<MemoData> dataList, Context context, Uri uri){
        this.dataList=dataList;
        this.mContext=context;
        this.uri=uri;
        branch=1;
    }

    public PdfRunnable(ArrayList<MemoData> dataList, Context context, String extension){
        this.dataList=dataList;
        this.mContext=context;
        this.extension=extension;
        branch=2;
    }

    @Override
    public void run() {

        PdfDocument pdfDocument=new PdfDocument();
        PdfDocument.PageInfo pageInfo;
        PdfDocument.Page page;
        Canvas canvas;

        int width=1080;
        int height=1920;

        for(int i=0;i<dataList.size();i++){
            pageInfo=new PdfDocument.PageInfo.Builder(width, height, 1).create();
            page=pdfDocument.startPage(pageInfo);

            //canvas 배경 색 지정
            canvas=page.getCanvas();
            int canvasColor= ContextCompat.getColor(mContext,R.color.cream);
            canvas.drawColor(canvasColor);

            //canvas 배경 drawable 지정
            int dy=height/15;
            Drawable backgroundColor=ContextCompat.getDrawable(mContext,R.drawable.memo_writing);
            if(backgroundColor!=null){
                backgroundColor.setBounds(0, dy, width, height-dy);
                backgroundColor.draw(canvas);
            }

            //date
            int date=dataList.get(i).getDate();
            int year=date/10000;
            date-=year*10000;
            int month=date/100;
            int day=date%100;

            TextPaint datePaint=new TextPaint();
            datePaint.setTextSize(64);
            datePaint.setTextAlign(Paint.Align.CENTER);
            datePaint.setUnderlineText(true);

            String formattedDate= String.format( java.util.Locale.getDefault(),
                                          "%d.%02d.%02d", year, month, day );
            canvas.drawText(formattedDate,(float)(width/2), dy+180, datePaint);

            //image
            if(dataList.get(i).getImage()!=null){
                Bitmap img=resizeBitmap(dataList.get(i).getImage(), 560, 420);
                Paint paintImg=new Paint();
                paintImg.setAntiAlias(true);
                canvas.drawBitmap(img,(float)(width-img.getWidth())/2,dy+240, paintImg);

            }

            //content
            String content=dataList.get(i).getContent();
            TextPaint contentPaint=new TextPaint();
            contentPaint.setTextSize(48);
            contentPaint.setTextAlign(Paint.Align.CENTER);
            StaticLayout.Builder builder=StaticLayout.Builder.obtain(
                    content, 0, content.length(), contentPaint, width-400);
            StaticLayout staticLayout=builder.build();
            canvas.save();
            canvas.translate((float)(width/2), 928);
            staticLayout.draw(canvas);
            canvas.restore();

            //price
            int price=dataList.get(i).getPrice();
            TextPaint pricePaint=new TextPaint();
            pricePaint.setTextSize(54);
            pricePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            pricePaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(Integer.toString(price),(float)(width/2), height-dy+87, pricePaint);

            //clover
            Drawable cloverImg=ContextCompat.getDrawable(mContext,R.drawable.clover_30);
            if(cloverImg!=null){
                cloverImg.setBounds(width/2-220, height-dy+40, width/2-160, height-dy+100);
                cloverImg.draw(canvas);
            }

            pdfDocument.finishPage(page);
        }

        SettingFragment fragment=new SettingFragment();

        if(branch==1){

            FileOutputStream fileOutputStream=fragment.getDirectory(uri,mContext);
            try {
                pdfDocument.writeTo(fileOutputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        else if(branch==2){

            File file=fragment.getDirectory(extension);
            try {
                pdfDocument.writeTo(new FileOutputStream(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        pdfDocument.close();

        ((MainActivity)mContext).runOnUiThread(()-> Toast.makeText(mContext,mContext.getResources().getText(R.string.completeSaving),Toast.LENGTH_SHORT).show());

    }

    public Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight){
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        while(width>=newWidth||height>=newHeight){
            width*=0.9;
            height*=0.9;
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

}