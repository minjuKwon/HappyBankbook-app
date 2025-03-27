package com.example.happybankbook;

import static com.example.happybankbook.Utils.showToastOnUi;

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

    private static final int CANVAS_WIDTH=1080;
    private static final int CANVAS_HEIGHT=1920;
    private static final int BACKGROUND_HEIGHT=CANVAS_HEIGHT/15;
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
        showToastOnUi(mContext, R.string.savePermissionYes);

        for(int i=0;i<dataList.size();i++){
            drawPage(pdfDocument, dataList.get(i));
        }

        SettingFragment fragment=new SettingFragment();

        if(branch==1){
            FileOutputStream fileOutputStream=fragment.getDirectory(uri,mContext);
            try {
                pdfDocument.writeTo(fileOutputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if(branch==2){
            File file=fragment.getDirectory(extension);
            try {
                pdfDocument.writeTo(new FileOutputStream(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        pdfDocument.close();

        ((MainActivity)mContext).runOnUiThread( ()->
                Toast.makeText(
                        mContext,
                        mContext.getResources().getText(R.string.completeSaving),
                        Toast.LENGTH_SHORT
                ).show());
    }

    private void drawPage(PdfDocument pdfDocument, MemoData data){
        PdfDocument.PageInfo pageInfo;
        PdfDocument.Page page;
        Canvas canvas;

        pageInfo=new PdfDocument.PageInfo.Builder(CANVAS_WIDTH, CANVAS_HEIGHT, 1).create();
        page=pdfDocument.startPage(pageInfo);

        //canvas 배경 색 지정
        canvas=page.getCanvas();
        int canvasColor= ContextCompat.getColor(mContext,R.color.cream);
        canvas.drawColor(canvasColor);

        //canvas 배경 drawable 지정
        Drawable backgroundColor=ContextCompat.getDrawable(mContext,R.drawable.memo_writing);
        if(backgroundColor!=null){
            backgroundColor.setBounds(
                    0,
                    BACKGROUND_HEIGHT,
                    CANVAS_WIDTH,
                    CANVAS_HEIGHT-BACKGROUND_HEIGHT
            );
            backgroundColor.draw(canvas);
        }

        drawDate(canvas, data);
        drawContentImage(canvas, data);
        drawContent(canvas, data);
        drawPrice(canvas, data);
        drawIcon(canvas);

        pdfDocument.finishPage(page);
    }

    private void drawDate(Canvas canvas, MemoData data){
        int date=data.getDate();
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
        canvas.drawText(formattedDate,(float)(CANVAS_WIDTH/2), BACKGROUND_HEIGHT+180, datePaint);
    }

    private void drawContentImage(Canvas canvas, MemoData data){
        if(data.getImage()!=null){
            Bitmap img=resizeBitmap(data.getImage(), 560, 420);
            Paint paintImg=new Paint();
            paintImg.setAntiAlias(true);
            canvas.drawBitmap(
                    img,
                    (float)(CANVAS_WIDTH-img.getWidth())/2,
                    BACKGROUND_HEIGHT+240,
                    paintImg
            );

        }
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

    private void drawContent(Canvas canvas, MemoData data){
        String content=data.getContent();
        TextPaint contentPaint=new TextPaint();
        contentPaint.setTextSize(48);
        contentPaint.setTextAlign(Paint.Align.CENTER);
        StaticLayout.Builder builder=StaticLayout.Builder.obtain(
                content, 0, content.length(), contentPaint, CANVAS_WIDTH-400);
        StaticLayout staticLayout=builder.build();
        canvas.save();
        canvas.translate((float)(CANVAS_WIDTH/2), 928);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    private void drawPrice(Canvas canvas, MemoData data){
        int price=data.getPrice();
        TextPaint pricePaint=new TextPaint();
        pricePaint.setTextSize(54);
        pricePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        pricePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(
                Integer.toString(price),
                (float)(CANVAS_WIDTH/2),
                CANVAS_HEIGHT-BACKGROUND_HEIGHT+87,
                pricePaint
        );
    }

    private void drawIcon(Canvas canvas){
        Drawable cloverImg=ContextCompat.getDrawable(mContext,R.drawable.clover_30);
        if(cloverImg!=null){
            cloverImg.setBounds(
                    CANVAS_WIDTH/2-220,
                    CANVAS_HEIGHT-BACKGROUND_HEIGHT+40,
                    CANVAS_WIDTH/2-160,
                    CANVAS_HEIGHT-BACKGROUND_HEIGHT+100
            );
            cloverImg.draw(canvas);
        }
    }

}