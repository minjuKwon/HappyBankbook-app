package com.example.happybankbook.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.happybankbook.MainActivity;
import com.example.happybankbook.MemoRecyclerAdapter;
import com.example.happybankbook.R;
import com.example.happybankbook.contract.ListContract;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.db.RoomDB;
import com.example.happybankbook.presenter.ListPresenter;

import java.util.ArrayList;

public class ListFragment extends Fragment implements View.OnClickListener, ListContract.View{

    private TextView txtSearch;
    private TextView txtCondition;
    private RecyclerView recyclerView;
    private ListPresenter presenter;
    private MemoRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        init(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.releaseView();
    }

    private void init(View v){
        txtSearch=v.findViewById(R.id.txtSearch);
        txtCondition=v.findViewById(R.id.txtCondition);
        recyclerView=v.findViewById(R.id.recyclerMemo);

        txtSearch.setOnClickListener(this);
        txtCondition.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new MemoRecyclerAdapter(getContext());
        recyclerView.setAdapter(adapter);

        presenter=new ListPresenter();
        presenter.setView(this);
        presenter.getData(RoomDB.getInstance(getContext()).memoDao());
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.txtSearch){
            ((MainActivity)getActivity()).replaceFragment(new SearchFragment());
        }else if(v.getId()==R.id.txtCondition){
            ((MainActivity)getActivity()).addFragment(new ConditionFragment());
        }
    }

    @Override
    public void setItems(ArrayList<MemoData> items) {
        adapter.setItems(items);
    }

}