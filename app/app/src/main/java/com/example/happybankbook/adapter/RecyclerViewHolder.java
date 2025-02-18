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

    private TextView idxTextView,dateTextView,contentTextView,priceTextView;
    private OnItemClickListener onItemClickListener;

    public RecyclerViewHolder(@NonNull View view){
        super(view);

        idxTextView=view.findViewById(R.id.txtNumber);
        dateTextView=view.findViewById(R.id.inputTxtDate);
        contentTextView=view.findViewById(R.id.inputTxtContent);
        priceTextView=view.findViewById(R.id.inputTxtDeposit);
        ConstraintLayout recyclerContainer=view.findViewById(R.id.recyclerContainer);

        recyclerContainer.setOnClickListener(v -> onItemClickListener.onItemClick());
    }

    public void onBind(BaseItem data, Context context, int position, float fontSize, int textLine, boolean hasTextEllipsize){
        MemoData memoData=(MemoData)data;

        idxTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        dateTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        priceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);

        idxTextView.setMaxLines(textLine);
        contentTextView.setMaxLines(textLine);
        priceTextView.setMaxLines(textLine);

        if(hasTextEllipsize){
            idxTextView.setEllipsize(TextUtils.TruncateAt.END);
            contentTextView.setEllipsize(TextUtils.TruncateAt.END);
            priceTextView.setEllipsize(TextUtils.TruncateAt.END);
        }else{
            idxTextView.setEllipsize(null);
            contentTextView.setEllipsize(null);
            priceTextView.setEllipsize(null);
        }

        idxTextView.setText(Integer.toString(memoData.getNum()));

        dateTextView.setText(Long.toString(memoData.getDate()).substring(2));

        contentTextView.setText(memoData.getContent());

        if(memoData.getImage()!=null){
            Drawable img=new BitmapDrawable(context.getResources(),memoData.getImage());
            img.setBounds(0,0,100,100);
            contentTextView.setCompoundDrawables(img,null,null,null);
        }else{
            contentTextView.setCompoundDrawables(null,null,null,null);
        }

        priceTextView.setText(Integer.toString(memoData.getPrice()));

        if(position%2==0){
            idxTextView.setBackgroundResource(R.drawable.memo_list_content_background_cream);
            dateTextView.setBackgroundResource(R.drawable.memo_list_content_background_cream);
            contentTextView.setBackgroundResource(R.drawable.memo_list_content_background_cream);
            priceTextView.setBackgroundResource(R.color.cream);
        }else{
            idxTextView.setBackgroundResource(R.drawable.memo_list_content_background_green);
            dateTextView.setBackgroundResource(R.drawable.memo_list_content_background_green);
            contentTextView.setBackgroundResource(R.drawable.memo_list_content_background_green);
            priceTextView.setBackgroundResource(R.color.green);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener=listener;
    }

}
