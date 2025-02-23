package com.example.happybankbook.view;

import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_RETAIN_SORT;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_VIEWPAGER_SORT;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_VIEWPAGER_TEXT_SIZE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.happybankbook.MainActivity;
import com.example.happybankbook.R;
import com.example.happybankbook.adapter.MemoAdapter;
import com.example.happybankbook.adapter.MemoType;
import com.example.happybankbook.contract.ListContract;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.db.RoomDB;
import com.example.happybankbook.presenter.ListPresenter;
import com.example.happybankbook.presenterReturnInterface.IntResultCallback;

import java.util.ArrayList;

public class MemoDetailFragment extends Fragment implements ListContract.View,View.OnClickListener{

    //일정 시간 후 화살표 이미지 투명화 위한 Runnable
    private final Runnable changeImgAlphaRunnable =new Runnable(){
        @Override
        public void run() {
            forwardImageView.setImageAlpha(0);
            backImageView.setImageAlpha(0);
        }
    };

    private ViewPager2 viewPager;
    private ImageView forwardImageView;
    private ImageView backImageView;

    private ListPresenter presenter;
    private MemoAdapter adapter;

    private Handler handler;

    private int itemCount;
    private int fromDate;
    private int toDate;
    private int rowCount;
    private int currentPosition;
    private int adapterPosition;
    private boolean isFirstInteraction=true;
    private float fontSize=12;

    private Context mContext;
    private Activity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
        if(context instanceof Activity){
            mActivity=(Activity)context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler=new Handler();

        //ConditionFragment 정렬 값 받기
        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY_VIEWPAGER_SORT, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                adapter.setCondition(false);

                fromDate=result.getInt(getResources().getString(R.string.from_date));
                toDate=result.getInt(getResources().getString(R.string.to_date));
                itemCount=result.getInt(getResources().getString(R.string.item_count));
                boolean isNewestSort=result.getBoolean(getResources().getString(R.string.is_newest_sort));

                if(fromDate>toDate){
                    int temp=fromDate;
                    fromDate=toDate;
                    toDate=temp;
                }

                if(itemCount==0){
                    presenter.setIntResultCallback(new IntResultCallback() {
                        @Override
                        public void onIntResult(int value) {
                            if(isNewestSort){
                                presenter.getDataDesc(RoomDB.getInstance(getContext()).memoDao(),fromDate,toDate,value);
                            }else{
                                presenter.getDataAsc(RoomDB.getInstance(getContext()).memoDao(),fromDate,toDate,value);
                            }
                        }
                    });
                    presenter.getDataCount(RoomDB.getInstance(getContext()).memoDao());
                }else{
                    if(isNewestSort){
                        presenter.getDataDesc(RoomDB.getInstance(getContext()).memoDao(),fromDate,toDate,itemCount);
                    }else{
                        presenter.getDataAsc(RoomDB.getInstance(getContext()).memoDao(),fromDate,toDate,itemCount);
                    }
                }

            }
        });
        //변경 font size 값
        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY_VIEWPAGER_TEXT_SIZE, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                fontSize=result.getFloat(getResources().getString(R.string.text_Size));
                adapter.setFont(fontSize);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_memo_detail, container, false);
        init(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences= mActivity.getSharedPreferences(getResources().getString(R.string.pref_viewpager_text_style),Context.MODE_PRIVATE);
        fontSize=preferences.getFloat(getResources().getString(R.string.text_Size),12);
        adapter.setFont(fontSize);
        
        //SearchFragment에서 검색 후 키보드 내리지 않고 바로 viewpager 이동 하면,
        //계속 키보드 올려지는 경우 방지
        InputMethodManager inputMethodManager=(InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onStop() {
        super.onStop();

        Bundle bundle=new Bundle();
        bundle.putBoolean(getResources().getString(R.string.is_newest_sort),true);
        getParentFragmentManager().setFragmentResult(REQUEST_KEY_RETAIN_SORT, bundle);

        resetTextSetting();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.releaseView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext=null;
        mActivity=null;
    }

    public void init(View view){
        viewPager=view.findViewById(R.id.viewPager2);
        forwardImageView=view.findViewById(R.id.imgForward);
        backImageView=view.findViewById(R.id.imgBack);
        TextView previousTextView=view.findViewById(R.id.memoDetailPrevious);

        forwardImageView.setOnClickListener(this);
        backImageView.setOnClickListener(this);
        previousTextView.setOnClickListener(this);

        presenter=new ListPresenter();
        presenter.setView(this);

        adapter=new MemoAdapter(getContext(), MemoType.VIEWPAGER, fontSize);
        viewPager.setAdapter(adapter);

        adapterPosition= adapter.getRecyclerviewPosition();

        presenter.setIntResultCallback(new IntResultCallback() {
            @Override
            public void onIntResult(int value) {
                rowCount=value;
            }
        });
        presenter.getDataCount(RoomDB.getInstance(getContext()).memoDao());

        changePage();
    }

    @Override
    public void onClick(View v) {
        final int delayTime=3000;
        if(v.getId()==R.id.imgForward){
            forwardImageView.setImageAlpha(255);
            //처음 1번째 아이템 클릭하여 이동한 viewpager 에서 이전 데이터로 이동하지 않은 오류 해결
            if(!isFirstInteraction&&adapterPosition==1&&currentPosition==1){
                //notifyItemChanged 호출하면 화면 버벅거림
                adapter.notifyDataSetChanged();
                //1번째 아이템이라도 viewpager 입장에서는 0번째라서 이전 버튼 누르면
                //페이지 변화가 없기 때문에 임의로 변경.
                currentPosition=0;
                viewPager.setCurrentItem(currentPosition);
                forwardImageView.setVisibility(View.INVISIBLE);
            }else{
                viewPager.setCurrentItem(currentPosition-1,false);
            }
            handler.postDelayed(changeImgAlphaRunnable,delayTime);
        }else if(v.getId()==R.id.imgBack){
            backImageView.setImageAlpha(255);
            viewPager.setCurrentItem(currentPosition+1,false);
            handler.postDelayed(changeImgAlphaRunnable,delayTime);
        }else if(v.getId()==R.id.memoDetailPrevious){
            ((MainActivity)mActivity).removeFragment(this);
        }
    }

    @Override
    public void setItems(ArrayList<MemoData> items) {
        adapter.setItems(items);
        adapter.setCondition(true);
    }

    public void changePage(){
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //recyclerview position, viewpager position 더하여 클릭된 메모 데이터에서 슬라이드 하였을 때 다음 데이터 로딩 하기 위한 초기 값
                if(isFirstInteraction){
                    currentPosition=position+adapterPosition;
                }else{
                    currentPosition=position;
                }
                isFirstInteraction=false;
                
                if(currentPosition==0){
                    forwardImageView.setVisibility(View.INVISIBLE);
                    backImageView.setVisibility(View.VISIBLE);
                }else if(currentPosition==(rowCount-1)){
                    forwardImageView.setVisibility(View.VISIBLE);
                    backImageView.setVisibility(View.INVISIBLE);
                }else{
                    forwardImageView.setVisibility(View.VISIBLE);
                    backImageView.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void resetTextSetting(){
        SharedPreferences preferences= mActivity.getSharedPreferences(getResources().getString(R.string.pref_viewpager_text_style), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putFloat(getResources().getString(R.string.text_Size), fontSize);

        editor.apply();
    }

}

