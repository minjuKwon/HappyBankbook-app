package com.example.happybankbook;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MemoRecyclerAdapter extends RecyclerView.Adapter<MemoRecyclerAdapter.ViewHolder> {

    private List<MemoData> dataList=new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtIdx,txtDate,txtContent,txtPrice;
        public ViewHolder(View view){
            super(view);
            txtIdx=view.findViewById(R.id.txtNumber);
            txtDate=view.findViewById(R.id.inputTxtDate);
            txtContent=view.findViewById(R.id.inputTxtContent);
            txtPrice=view.findViewById(R.id.inputTxtDeposit);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MemoData data=dataList.get(holder.getAdapterPosition());

        holder.txtIdx.setText(Integer.toString(data.getIdx()));
        holder.txtDate.setText(Long.toString(data.getDate()).substring(2));
        holder.txtContent.setText(data.getContent());
        holder.txtPrice.setText(Integer.toString(data.getPrice()));

        if(position%2==0){
            holder.txtIdx.setBackgroundResource(R.drawable.memo_list_content_background_cream);
            holder.txtDate.setBackgroundResource(R.drawable.memo_list_content_background_cream);
            holder.txtContent.setBackgroundResource(R.drawable.memo_list_content_background_cream);
            holder.txtPrice.setBackgroundResource(R.color.cream);
        }else{
            holder.txtIdx.setBackgroundResource(R.drawable.memo_list_content_background_green);
            holder.txtDate.setBackgroundResource(R.drawable.memo_list_content_background_green);
            holder.txtContent.setBackgroundResource(R.drawable.memo_list_content_background_green);
            holder.txtPrice.setBackgroundResource(R.color.green);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setItems(ArrayList<MemoData>data){
        this.dataList=data;
        notifyDataSetChanged();
    }

}
