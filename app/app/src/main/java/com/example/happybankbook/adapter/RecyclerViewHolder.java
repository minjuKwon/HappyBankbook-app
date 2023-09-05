package com.example.happybankbook.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
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

    public void onBind(BaseItem data, Context context, int position, float fontSize, int textLine, boolean textEllipsize){

        this.data=(MemoData)data;

        txtIdx.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        txtPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

        txtIdx.setMaxLines(textLine);
        txtContent.setMaxLines(textLine);
        txtPrice.setMaxLines(textLine);

        if(textEllipsize){
            txtIdx.setEllipsize(TextUtils.TruncateAt.END);
            txtContent.setEllipsize(TextUtils.TruncateAt.END);
            txtPrice.setEllipsize(TextUtils.TruncateAt.END);
        }else{
            txtIdx.setEllipsize(null);
            txtContent.setEllipsize(null);
            txtPrice.setEllipsize(null);
        }

        txtIdx.setText(Integer.toString(this.data.getNum()));

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
