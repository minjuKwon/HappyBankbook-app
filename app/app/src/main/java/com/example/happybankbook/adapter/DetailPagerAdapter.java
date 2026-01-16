package com.example.happybankbook.adapter;

import static com.example.happybankbook.Utils.formatDateToString;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happybankbook.R;
import com.example.happybankbook.db.UiMemoData;

import java.util.ArrayList;
import java.util.List;

public class DetailPagerAdapter extends RecyclerView.Adapter<DetailPagerAdapter.ViewPagerHolder>{

    private TextView dateTextView, contentTextView, priceTextView;
    private ImageView contentImg;
    private List<UiMemoData> dataList=new ArrayList<>();
    private float textSize;

    public DetailPagerAdapter(float textSize){
        this.textSize=textSize;
    }

    @NonNull
    @Override
    public ViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_memo_detail_item, parent, false);
        return new DetailPagerAdapter.ViewPagerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerHolder holder, int position) {
        UiMemoData data=dataList.get(position);
        boolean isRecyclable= position != 0 && position != dataList.size() - 1;
        holder.setIsRecyclable(isRecyclable);
        holder.bind(data, textSize);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setItems(ArrayList<UiMemoData> data){
        DiffUtil.DiffResult diffResult=
                DiffUtil.calculateDiff(new MemoDiffUtilCallback(dataList,data));
        dataList=data;
        diffResult.dispatchUpdatesTo(this);
    }

    public int getPositionById(int itemId) {
        for(int i=0;i<dataList.size();i++){
            if(dataList.get(i).getIdx()==itemId){
                return i;
            }
        }
        return -1;
    }

    public void setTextSize(float size){
        textSize=size;
    }

    public class ViewPagerHolder extends RecyclerView.ViewHolder{
        public ViewPagerHolder(@NonNull View itemView) {
            super(itemView);

            dateTextView=itemView.findViewById(R.id.memoDetailDate);
            contentTextView=itemView.findViewById(R.id.memoDetailContent);
            priceTextView=itemView.findViewById(R.id.memoDetailPriceTxt);
            contentImg=itemView.findViewById(R.id.memoDetailImg);
        }

        public void bind(UiMemoData memoData, float textSize){
            dateTextView.setText(formatDateToString(memoData.getDate()));

            String formattedPrice=
                    String.format(java.util.Locale.getDefault(), "%,d", memoData.getPrice());
            priceTextView.setText(formattedPrice);

            contentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            contentTextView.setText(memoData.getContent());

            if(memoData.getImage()!=null){
                Drawable img=new BitmapDrawable(contentImg.getResources(),memoData.getImage());
                contentImg.setImageDrawable(img);
                contentImg.setVisibility(View.VISIBLE);
            }
        }

    }

}