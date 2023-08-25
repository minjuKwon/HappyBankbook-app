package com.example.happybankbook.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.happybankbook.R;
import com.example.happybankbook.db.BaseItem;
import com.example.happybankbook.db.MemoData;

public class ViewPagerViewHolder extends BaseItemView {

    private TextView txtDetailDate, txtDetailContent, txtDetailPrice;
    private ImageView imgDetail;

    private MemoData data;

    public ViewPagerViewHolder(@NonNull View view){
        super(view);

        txtDetailDate=view.findViewById(R.id.memoDetailDate);
        txtDetailContent=view.findViewById(R.id.memoDetailContent);
        txtDetailPrice=view.findViewById(R.id.memoDetailPriceTxt);
        imgDetail=view.findViewById(R.id.memoDetailImg);
    }

    public void onBind(BaseItem data, Context context){

        this.data=(MemoData)data;

        String date=Integer.toString(this.data.getDate());
        String year=date.substring(0,4);
        String month=date.substring(4,6);
        String day=date.substring(6);
        txtDetailDate.setText(String.format("%s.%s.%s",year,month,day));

        txtDetailContent.setText(this.data.getContent());

        txtDetailPrice.setText(Integer.toString(this.data.getPrice()));

        if(this.data.getImage()!=null){
            Drawable img=new BitmapDrawable(context.getResources(),this.data.getImage());
            imgDetail.setImageDrawable(img);
        }

    }

}
