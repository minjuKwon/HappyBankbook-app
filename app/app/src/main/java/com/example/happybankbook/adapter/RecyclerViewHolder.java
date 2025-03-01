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

    private OnItemClickListener onItemClickListener;
    private TextView idxTextView,dateTextView,contentTextView,priceTextView;


    public RecyclerViewHolder(@NonNull View view){
        super(view);

        ConstraintLayout recyclerContainer=view.findViewById(R.id.recyclerContainer);
        idxTextView=view.findViewById(R.id.txtNumber);
        dateTextView=view.findViewById(R.id.inputTxtDate);
        contentTextView=view.findViewById(R.id.inputTxtContent);
        priceTextView=view.findViewById(R.id.inputTxtDeposit);

        recyclerContainer.setOnClickListener(v -> onItemClickListener.onItemClick());
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener=listener;
    }

    public void onBind(BaseItem data, Context context,
                       int position, float textSize, int textLine, boolean hasTextEllipsize
    ){
        MemoData memoData=(MemoData)data;

        setTextStyle(textSize, textLine, hasTextEllipsize);

        String formattedIdx= getFormattedString("%,d", memoData.getNum());
        String formattedDate= getFormattedString("%d", memoData.getDate());
        String formattedPrice= getFormattedString("%,d", memoData.getPrice());

        idxTextView.setText(formattedIdx);
        dateTextView.setText(formattedDate.substring(2));
        priceTextView.setText(formattedPrice);
        contentTextView.setText(memoData.getContent());

        final int imageWidth=100;
        final int imageHeight=100;

        if(memoData.getImage()!=null){
            Drawable img=new BitmapDrawable(context.getResources(),memoData.getImage());
            img.setBounds(0,0,imageWidth,imageHeight);
            contentTextView.setCompoundDrawables(img,null,null,null);
        }else{
            contentTextView.setCompoundDrawables(null,null,null,null);
        }

        setBackground(position);

    }

    private void setTextStyle(float textSize, int textLine, boolean hasTextEllipsize){
        setTextSize(idxTextView, textSize);
        setTextSize(dateTextView, textSize);
        setTextSize(contentTextView, textSize);
        setTextSize(priceTextView, textSize);

        idxTextView.setMaxLines(textLine);
        contentTextView.setMaxLines(textLine);
        priceTextView.setMaxLines(textLine);

        if(hasTextEllipsize){
            setEllipsize(idxTextView);
            setEllipsize(contentTextView);
            setEllipsize(priceTextView);
        }else{
            idxTextView.setEllipsize(null);
            contentTextView.setEllipsize(null);
            priceTextView.setEllipsize(null);
        }
    }

    private void setTextSize(TextView view, float textSize){
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    private void setEllipsize(TextView view){
        view.setEllipsize(TextUtils.TruncateAt.END);
    }

    private String getFormattedString(String format, Object obj){
        return String.format(java.util.Locale.getDefault(), format, obj);
    }

    private void setBackground(int position){
        if(position%2==0){
            setBackgroundItemEven(idxTextView);
            setBackgroundItemEven(dateTextView);
            setBackgroundItemEven(contentTextView);
            priceTextView.setBackgroundResource(R.color.cream);
        }else{
            setBackgroundItemOdd(idxTextView);
            setBackgroundItemOdd(dateTextView);
            setBackgroundItemOdd(contentTextView);
            priceTextView.setBackgroundResource(R.color.green);
        }
    }

    private void setBackgroundItemEven(TextView view){
        view.setBackgroundResource(R.drawable.memo_list_content_background_cream);
    }

    private void setBackgroundItemOdd(TextView view){
        view.setBackgroundResource(R.drawable.memo_list_content_background_green);
    }

}