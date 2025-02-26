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

        String formattedIdx=String.format( java.util.Locale.getDefault(),
                                     "%,d", memoData.getNum() );
        String formattedDate=String.format( java.util.Locale.getDefault(),
                                     "%,d", memoData.getDate() );
        String formattedPrice=String.format( java.util.Locale.getDefault(),
                                      "%,d", memoData.getPrice() );

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

}