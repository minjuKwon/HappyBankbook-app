package com.example.happybankbook.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happybankbook.R;
import com.example.happybankbook.db.UiMemoData;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder>{

    public interface OnItemClickListener {
        void onClick(UiMemoData memoData);
    }

    private final OnItemClickListener listener;
    private TextView idxTextView,dateTextView,contentTextView,priceTextView;
    private List<UiMemoData> dataList=new ArrayList<>();
    private int textLine;
    private float textSize;
    private boolean textEllipsize;

    public ListAdapter(
            float textSize,
            int textLine,
            boolean textEllipsize,
            OnItemClickListener listener
    ){
        this.textSize=textSize;
        this.textLine=textLine;
        this.textEllipsize=textEllipsize;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        UiMemoData data=dataList.get(position);

        holder.bind(data, position, textSize, textLine, textEllipsize);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    //데이터 꼬임 현상 막음
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setItems(ArrayList<UiMemoData> data){
        DiffUtil.DiffResult diffResult=
                DiffUtil.calculateDiff(new MemoDiffUtilCallback(dataList,data));
        dataList=data;
        diffResult.dispatchUpdatesTo(this);
    }

    public void setTextSize(float size){
        textSize=size;
    }

    public void setTextLine(int line){
        this.textLine=line;
    }

    public void setTextEllipsize(boolean check){
        this.textEllipsize=check;
    }

    public void clearItems(){
        dataList.clear();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            idxTextView=itemView.findViewById(R.id.txtNumber);
            dateTextView=itemView.findViewById(R.id.inputTxtDate);
            contentTextView=itemView.findViewById(R.id.inputTxtContent);
            priceTextView=itemView.findViewById(R.id.inputTxtDeposit);
        }

        public void bind(
                UiMemoData memoData,
                int position,
                float textSize,
                int textLine,
                boolean hasTextEllipsize
        ){
            itemView.setOnClickListener(v ->
                    listener.onClick(memoData)
            );

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
                Drawable img=new BitmapDrawable(contentTextView.getResources(),memoData.getImage());
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

}