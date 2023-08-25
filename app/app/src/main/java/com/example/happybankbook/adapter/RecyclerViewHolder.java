package com.example.happybankbook.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.happybankbook.R;
import com.example.happybankbook.db.BaseItem;
import com.example.happybankbook.db.MemoData;

public class RecyclerViewHolder extends BaseItemView{

    private TextView txtIdx,txtDate,txtContent,txtPrice;
    private ConstraintLayout recyclerContainer;

    private MemoData data;

    private OnItemClickListener onItemClickListener;

    public RecyclerViewHolder(@NonNull View view){
        super(view);

        txtIdx=view.findViewById(R.id.txtNumber);
        txtDate=view.findViewById(R.id.inputTxtDate);
        txtContent=view.findViewById(R.id.inputTxtContent);
        txtPrice=view.findViewById(R.id.inputTxtDeposit);
        recyclerContainer=view.findViewById(R.id.recyclerContainer);

        recyclerContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick();
            }
        });
    }

    public void onBind(BaseItem data, Context context, boolean isNewSort, int position, int size){

        this.data=(MemoData)data;

        //isNewSort이면 txtIdx가 역순, 아닐 시 순서대로
        if(isNewSort){
            txtIdx.setText(Integer.toString(size-position));
        }else{
            txtIdx.setText(Integer.toString(position+1));
        }

        txtDate.setText(Long.toString(this.data.getDate()).substring(2));

        txtContent.setText(this.data.getContent());

        if(this.data.getImage()!=null){
            Drawable img=new BitmapDrawable(context.getResources(),this.data.getImage());
            img.setBounds(0,0,100,100);
            txtContent.setCompoundDrawables(img,null,null,null);
        }

        txtPrice.setText(Integer.toString(this.data.getPrice()));

        if(position%2==0){
            txtIdx.setBackgroundResource(R.drawable.memo_list_content_background_cream);
            txtDate.setBackgroundResource(R.drawable.memo_list_content_background_cream);
            txtContent.setBackgroundResource(R.drawable.memo_list_content_background_cream);
            txtPrice.setBackgroundResource(R.color.cream);
        }else{
            txtIdx.setBackgroundResource(R.drawable.memo_list_content_background_green);
            txtDate.setBackgroundResource(R.drawable.memo_list_content_background_green);
            txtContent.setBackgroundResource(R.drawable.memo_list_content_background_green);
            txtPrice.setBackgroundResource(R.color.green);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener=listener;
    }

}
