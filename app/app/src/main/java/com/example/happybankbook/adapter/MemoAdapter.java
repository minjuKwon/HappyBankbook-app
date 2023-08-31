package com.example.happybankbook.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happybankbook.MainActivity;
import com.example.happybankbook.R;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.view.MemoDetailFragment;

import java.util.ArrayList;
import java.util.List;

public class MemoAdapter extends RecyclerView.Adapter<BaseItemView> {

    private Context context;
    private MemoType memoType;
    static private List<MemoData> dataList=new ArrayList<>();

    private static int location;
    private boolean isFirst=true;

    public MemoAdapter(Context context, MemoType memoType){
        this.context=context;
        this.memoType=memoType;
    }

    @NonNull
    @Override
    public BaseItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if(memoType==MemoType.RECYCLER){
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item,parent,false);
            return new RecyclerViewHolder(view);
        }else if(memoType==MemoType.VIEWPAGER){
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_memo_detail_item,parent,false);
            return new ViewPagerViewHolder(view);
        }

        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull BaseItemView holder, int position) {

        MemoData data=dataList.get(holder.getAdapterPosition());

        if(holder instanceof RecyclerViewHolder){
            RecyclerViewHolder recyclerViewHolder=(RecyclerViewHolder)holder;
            recyclerViewHolder.onBind(data, context, position);
            //recyclerview position 얻기 위한 클릭 이벤트
            recyclerViewHolder.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick() {
                    ((MainActivity)context).addFragment(new MemoDetailFragment());
                    location= holder.getAdapterPosition();
                }
            });

        }else if(holder instanceof ViewPagerViewHolder){
            ViewPagerViewHolder viewPagerViewHolder=(ViewPagerViewHolder) holder;
            //recyclerview position, viewpager position 더하여 클릭한 메모를 시작점으로 viewpager 화면 넘기게 하기 위한 초기 값
            if(isFirst){
                data=dataList.get(holder.getAdapterPosition()+location);
                isFirst=false;
            }
            viewPagerViewHolder.onBind(data, context);
        }
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

    public void setItems(ArrayList<MemoData>data){
        this.dataList=data;
        notifyDataSetChanged();
    }

    public void clear(){
        dataList.clear();
    }

    public int getLocation(){
        return location;
    }

}
