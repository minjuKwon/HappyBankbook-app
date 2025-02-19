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

public class MemoAdapter extends RecyclerView.Adapter<BaseItemView> {

    private final Context mContext;
    private final MemoType memoType;
    static private List<MemoData> dataList=new ArrayList<>();

    private static int recyclerviewPosition;
    private float fontSize;
    private int textLine;
    private boolean isFirstInteraction=true;
    private boolean isRecyclable=true;
    private boolean textEllipsize;
    private boolean hasReceivedCondition=true;
    private boolean hasVisitedViewPager ;

    public MemoAdapter(Context context, MemoType memoType, float fontSize){
        this.mContext=context;
        this.memoType=memoType;
        this.fontSize=fontSize;
    }

    public MemoAdapter(Context context, MemoType memoType, float fontSize, int textLine, boolean textEllipsize){
        this.mContext=context;
        this.memoType=memoType;
        this.fontSize=fontSize;
        this.textLine=textLine;
        this.textEllipsize=textEllipsize;
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
            //viewPager 후 recyclerView로 돌아 왔을 때 condition 값을 유지 하기 위한 변수
            hasVisitedViewPager =true;
            return new ViewPagerViewHolder(view);
        }

        throw new AssertionError("impossible viewType : "+viewType);

    }

    @Override
    public void onBindViewHolder(@NonNull BaseItemView holder, int position) {
        MemoData data;

        if(holder instanceof RecyclerViewHolder){
            RecyclerViewHolder recyclerViewHolder=(RecyclerViewHolder)holder;
            data=dataList.get(recyclerViewHolder.getAdapterPosition());
            recyclerViewHolder.onBind(data, mContext, position, fontSize, textLine, textEllipsize);
            //recyclerview position 얻기 위한 클릭 이벤트
            recyclerViewHolder.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick() {
                    ((MainActivity)mContext).addFragment(new MemoDetailFragment());
                    recyclerviewPosition= recyclerViewHolder.getAdapterPosition();
                }
            });

        }else if(holder instanceof ViewPagerViewHolder){
            ViewPagerViewHolder viewPagerViewHolder=(ViewPagerViewHolder) holder;
            data=dataList.get(viewPagerViewHolder.getAdapterPosition());
            if(hasReceivedCondition){//메모 정렬 후 onBind 호출하기 위한 변수.
                //recyclerview position, viewpager position 더하여 클릭한 메모를 시작점으로 viewpager 화면 넘기게 하기 위한 초기 값
                if(isFirstInteraction){
                    data=dataList.get(viewPagerViewHolder.getAdapterPosition()+recyclerviewPosition);
                }
                //viewpager에서 제일 첫번째 위치로 이동하면 처음 클릭한 데이터(0번째)로 재활용 방지
                if(viewPagerViewHolder.getAdapterPosition()==0){
                    isRecyclable=false;
                }
                viewPagerViewHolder.setIsRecyclable(isRecyclable);
                viewPagerViewHolder.onBind(data, mContext, fontSize);
                isFirstInteraction=false;
                isRecyclable=true;
            }
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
        dataList=data;
        notifyDataSetChanged();
    }

    public void clearItems(){
        dataList.clear();
    }

    public int getRecyclerviewPosition(){
        return recyclerviewPosition;
    }

    public void setFont(float size){
        fontSize=size;
    }

    public void setTextLine(int line){
        this.textLine=line;
    }

    public void setTextEllipsize(boolean check){
        this.textEllipsize=check;
    }

    public void setCondition(boolean condition){
        this.hasReceivedCondition=condition;
    }

    public boolean hasVisitedViewpager(){
        return hasVisitedViewPager ;
    }

}
