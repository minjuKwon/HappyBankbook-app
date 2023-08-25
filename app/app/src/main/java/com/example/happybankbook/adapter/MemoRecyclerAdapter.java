package com.example.happybankbook.adapter;

import android.content.Context;
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

public class MemoRecyclerAdapter extends RecyclerView.Adapter<BaseItemView> {

    private Context context;
    private MemoType memoType;
    static private List<MemoData> dataList=new ArrayList<>();

    private boolean isNewSort=true;
    private static int location;

    public MemoRecyclerAdapter(Context context, MemoType memoType){
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
            recyclerViewHolder.onBind(data, context, isNewSort, position, dataList.size());
            recyclerViewHolder.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick() {
                    ((MainActivity)context).addFragment(new MemoDetailFragment());
                    location= holder.getAdapterPosition();
                }
            });

        }else if(holder instanceof ViewPagerViewHolder){
            ViewPagerViewHolder viewPagerViewHolder=(ViewPagerViewHolder) holder;
            viewPagerViewHolder.onBind(dataList.get(holder.getAdapterPosition()+location), context);
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

    //ConditionFragment 정렬 순서를 얻기 위한 메소드
    public void setSort(boolean sort){
        this.isNewSort=sort;
    }

}
