package com.example.happybankbook;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ListFragment extends Fragment implements View.OnClickListener{

    private TextView txtSearch;
    private TextView txtCondition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        init(view);
        return view;
    }

    private void init(View v){
        txtSearch=v.findViewById(R.id.txtSearch);
        txtCondition=v.findViewById(R.id.txtCondition);

        txtSearch.setOnClickListener(this);
        txtCondition.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.txtSearch){
            ((MainActivity)getActivity()).replaceFragment(new SearchFragment());
        }else if(v.getId()==R.id.txtCondition){
            ((MainActivity)getActivity()).addFragment(new ConditionFragment());
        }
    }

}