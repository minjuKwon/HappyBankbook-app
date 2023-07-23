package com.example.happybankbook;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment implements View.OnClickListener,ListContract.View{

    private TextView txtSearch;
    private TextView txtCondition;
    private RecyclerView recyclerView;
    ListPresenter presenter;
    MemoRecyclerAdapter adapter;

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