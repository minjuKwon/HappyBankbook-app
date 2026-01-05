package com.example.happybankbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happybankbook.db.UiMemoData;
import com.example.happybankbook.view.MainActivity;
import com.example.happybankbook.R;
import com.example.happybankbook.view.MemoDetailFragment;

import java.util.ArrayList;
import java.util.List;

public class MemoAdapter extends RecyclerView.Adapter<BaseItemView> {

    private static List<UiMemoData> dataList=new ArrayList<>();
    private static int recyclerviewPosition;
    private final Context mContext;
    private final MemoType memoType;

    private int textLine;
    private float textSize;
    private boolean isFirstInteraction=true, isRecyclable=true, hasReceivedCondition=true;
    private boolean textEllipsize, hasVisitedViewPager;


    public MemoAdapter(Context context, MemoType memoType, float textSize){
        this.mContext=context;
        this.memoType=memoType;
        this.textSize=textSize;
    }

    public MemoAdapter(
            Context context,
            MemoType memoType,
            float textSize,
            int textLine,
            boolean textEllipsize
    ){
        this.mContext=context;
        this.memoType=memoType;
        this.textSize=textSize;
        this.textLine=textLine;
        this.textEllipsize=textEllipsize;
    }

    @NonNull
    @Override
    public BaseItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if(memoType==MemoType.RECYCLER){
            view= LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.recyclerview_item, parent, false);
            return new RecyclerViewHolder(view);
        }else if(memoType==MemoType.VIEWPAGER){
            view= LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.fragment_memo_detail_item, parent, false);
            //viewPager 후 recyclerView로 돌아 왔을 때 condition 값을 유지 하기 위한 변수
            hasVisitedViewPager =true;
            return new ViewPagerViewHolder(view);
        }

        throw new AssertionError("impossible viewType : "+viewType);

    }

    @Override
    public void onBindViewHolder(@NonNull BaseItemView holder, int position) {
        UiMemoData data;

        if(holder instanceof RecyclerViewHolder){
            RecyclerViewHolder recyclerViewHolder=(RecyclerViewHolder)holder;
            data=dataList.get(recyclerViewHolder.getBindingAdapterPosition());

            recyclerViewHolder.onBind(data, mContext, position, textSize, textLine, textEllipsize);

            //recyclerview position 얻기 위한 클릭 이벤트
            recyclerViewHolder.setOnItemClickListener(() -> {
                ((MainActivity)mContext).addFragment(new MemoDetailFragment());
                recyclerviewPosition= recyclerViewHolder.getBindingAdapterPosition();
            });

        }else if(holder instanceof ViewPagerViewHolder){
            ViewPagerViewHolder viewPagerViewHolder=(ViewPagerViewHolder) holder;
            data=dataList.get(viewPagerViewHolder.getBindingAdapterPosition());
            if(hasReceivedCondition){//메모 정렬 후 onBind 호출하기 위한 변수.
                //recyclerview position, viewpager position 더하여
                // 클릭한 메모를 시작점으로 viewpager 화면 넘기게 하기 위한 초기 값
                if(isFirstInteraction){
                    data=dataList.get(viewPagerViewHolder.getBindingAdapterPosition()+recyclerviewPosition);
                }
                //viewpager에서 제일 첫번째 위치로 이동하면 처음 클릭한 데이터(0번째)로 재활용 방지
                if(viewPagerViewHolder.getBindingAdapterPosition()==0){
                    isRecyclable=false;
                }
                viewPagerViewHolder.setIsRecyclable(isRecyclable);
                viewPagerViewHolder.onBind(data, mContext, textSize);
                isRecyclable=true;
                isFirstInteraction=false;
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

    public void setItems(ArrayList<UiMemoData>data){
        DiffUtil.DiffResult diffResult=
                DiffUtil.calculateDiff(new MemoDiffUtilCallback(dataList,data));
        dataList=data;
        diffResult.dispatchUpdatesTo(this);
    }

    public void setItems(ArrayList<UiMemoData>data, Runnable onCommitted){
        setItems(data);
        onCommitted.run();
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

    public void setCondition(boolean condition){
        this.hasReceivedCondition=condition;
    }

    public int getRecyclerviewPosition(){
        return recyclerviewPosition;
    }

    public boolean hasVisitedViewpager(){
        return hasVisitedViewPager ;
    }

    public void clearItems(){
        dataList.clear();
    }

}