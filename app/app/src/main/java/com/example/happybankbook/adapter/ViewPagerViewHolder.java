package com.example.happybankbook.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.happybankbook.R;
import com.example.happybankbook.db.BaseItem;
import com.example.happybankbook.db.MemoData;

public class ViewPagerViewHolder extends BaseItemView {

    private TextView dateTextView, contentTextView, priceTextView;
    private ImageView contentImg;

    public ViewPagerViewHolder(@NonNull View view){
        super(view);

        dateTextView=view.findViewById(R.id.memoDetailDate);
        contentTextView=view.findViewById(R.id.memoDetailContent);
        priceTextView=view.findViewById(R.id.memoDetailPriceTxt);
        contentImg=view.findViewById(R.id.memoDetailImg);
    }

    public void onBind(BaseItem data, Context context, float fontSize){
        MemoData memoData=(MemoData)data;

        contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

        String date=Integer.toString(memoData.getDate());
        String year=date.substring(0,4);
        String month=date.substring(4,6);
        String day=date.substring(6);
        dateTextView.setText(String.format("%s.%s.%s",year,month,day));

        contentTextView.setText(memoData.getContent());

        priceTextView.setText(Integer.toString(memoData.getPrice()));

        if(memoData.getImage()!=null){
            Drawable img=new BitmapDrawable(context.getResources(),memoData.getImage());
            contentImg.setImageDrawable(img);
            contentImg.setVisibility(View.VISIBLE);
        }

    }

}
