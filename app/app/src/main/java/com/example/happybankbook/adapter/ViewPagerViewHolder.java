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

    private final TextView txtDetailDate, txtDetailContent, txtDetailPrice;
    private final ImageView imgDetail;

    public ViewPagerViewHolder(@NonNull View view){
        super(view);

        txtDetailDate=view.findViewById(R.id.memoDetailDate);
        txtDetailContent=view.findViewById(R.id.memoDetailContent);
        txtDetailPrice=view.findViewById(R.id.memoDetailPriceTxt);
        imgDetail=view.findViewById(R.id.memoDetailImg);
    }

    public void onBind(BaseItem data, Context context, float fontSize){
        MemoData memoData=(MemoData)data;

        txtDetailContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

        String date=Integer.toString(memoData.getDate());
        String year=date.substring(0,4);
        String month=date.substring(4,6);
        String day=date.substring(6);
        txtDetailDate.setText(String.format("%s.%s.%s",year,month,day));

        txtDetailContent.setText(memoData.getContent());

        txtDetailPrice.setText(Integer.toString(memoData.getPrice()));

        if(memoData.getImage()!=null){
            Drawable img=new BitmapDrawable(context.getResources(),memoData.getImage());
            imgDetail.setImageDrawable(img);
        }

    }

}
