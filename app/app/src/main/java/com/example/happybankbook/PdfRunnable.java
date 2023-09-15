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

    private ArrayList<MemoData> dataList;
    private Context context;
    private Uri uri;
    private String extension;
    private int branch;
    private int width=1080;
    private int height=1920;

    public PdfRunnable(ArrayList<MemoData> dataList, Context context, Uri uri){
        this.dataList=dataList;
        this.context=context;
        this.uri=uri;
        branch=1;
    }

    public PdfRunnable(ArrayList<MemoData> dataList, Context context, String extension){
        this.dataList=dataList;
        this.context=context;
        this.extension=extension;
        branch=2;
    }

    @Override
    public void run() {

        PdfDocument pdfDocument=new PdfDocument();
        PdfDocument.PageInfo pageInfo;
        PdfDocument.Page page;
        Canvas canvas;

        for(int i=0;i<dataList.size();i++){
            pageInfo=new PdfDocument.PageInfo.Builder(width, height, 1).create();
            page=pdfDocument.startPage(pageInfo);

            //canvas 배경 색 지정
            canvas=page.getCanvas();
            canvas.drawColor(ContextCompat.getColor(context,R.color.cream));

            //canvas 배경 drawable 지정
            int dy=height/15;
            Drawable backgroundColor=ContextCompat.getDrawable(context,R.drawable.memo_writing);
            backgroundColor.setBounds(0, dy, width, height-dy);
            backgroundColor.draw(canvas);

            //date
            int date=dataList.get(i).getDate();
            int year=date/10000;
            date-=year*10000;
            int month=date/100;
            int day=date%100;

            TextPaint paintDate=new TextPaint();
            paintDate.setTextSize(64);
            paintDate.setTextAlign(Paint.Align.CENTER);
            paintDate.setUnderlineText(true);
            canvas.drawText(String.format("%d.%02d.%02d",year,month,day),width/2, dy+180, paintDate);

            //image
            if(dataList.get(i).getImage()!=null){
                Bitmap img=resizeBitmap(dataList.get(i).getImage(), 560, 420);
                Paint paintImg=new Paint();
                paintImg.setAntiAlias(true);
                canvas.drawBitmap(img,(width-img.getWidth())/2,dy+240, paintImg);

            }

            //content
            String content=dataList.get(i).getContent();
            TextPaint paintContent=new TextPaint();
            paintContent.setTextSize(48);
            paintContent.setTextAlign(Paint.Align.CENTER);
            StaticLayout.Builder builder=StaticLayout.Builder.obtain(
                    content, 0, content.length(), paintContent, width-400);
            StaticLayout staticLayout=builder.build();
            canvas.save();
            canvas.translate(width/2, 928);
            staticLayout.draw(canvas);
            canvas.restore();

            //price
            int price=dataList.get(i).getPrice();
            TextPaint paintPrice=new TextPaint();
            paintPrice.setTextSize(54);
            paintPrice.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            paintPrice.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(String.valueOf(price),width/2, height-dy+87, paintPrice);

            //clover
            Drawable clover=ContextCompat.getDrawable(context,R.drawable.clover_30);
            clover.setBounds(width/2-220, height-dy+40, width/2-160, height-dy+100);
            clover.draw(canvas);

            pdfDocument.finishPage(page);
        }

        SettingFragment fragment=new SettingFragment();

        if(branch==1){

            FileOutputStream fileOutputStream=fragment.getDirectory(uri,context);
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

        ((MainActivity)context).runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(context,context.getResources().getText(R.string.completeSaving),Toast.LENGTH_SHORT).show();
            }
        });

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
