package com.example.happybankbook.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.happybankbook.MainActivity;
import com.example.happybankbook.adapter.MemoAdapter;
import com.example.happybankbook.R;
import com.example.happybankbook.adapter.MemoType;
import com.example.happybankbook.contract.SearchContract;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.db.RoomDB;
import com.example.happybankbook.presenter.SearchPresenter;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener,SearchView.OnQueryTextListener, SearchContract.View {

    private RecyclerView recyclerView;
    private SearchPresenter presenter;
    private MemoAdapter adapter;
    private float fontSize=15;
    private int textLine=2;
    private boolean textEllipsize=true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener(getResources().getString(R.string.fontSize2), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                fontSize=result.getFloat(getResources().getString(R.string.fontSize));
                adapter.setFont(fontSize);
            }
        });
        //변경 text line 값
        getParentFragmentManager().setFragmentResultListener(getResources().getString(R.string.textLine2), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                textLine=result.getInt(getResources().getString(R.string.textLine));
                adapter.setTextLine(textLine);
            }
        });
        //변경 text ellipsize 값
        getParentFragmentManager().setFragmentResultListener(getResources().getString(R.string.textEllipsize2), this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                textEllipsize=result.getBoolean(getResources().getString(R.string.textEllipsize));
                adapter.setTextEllipsize(textEllipsize);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_search, container, false);
        init(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences= getActivity().getSharedPreferences(getResources().getString(R.string.searchTextSetting),Context.MODE_PRIVATE);
        textEllipsize=preferences.getBoolean(getResources().getString(R.string.textEllipsize),true);
        textLine=preferences.getInt(getResources().getString(R.string.textLine),2);
        fontSize=preferences.getFloat(getResources().getString(R.string.fontSize),15);

        adapter.setTextEllipsize(textEllipsize);
        adapter.setTextLine(textLine);
        adapter.setFont(fontSize);
    }

    @Override
    public void onStop() {
        super.onStop();
        resetTextSetting();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.releaseView();
    }

    private void init(View view){
        TextView txtPrevious=view.findViewById(R.id.previousSearch);
        SearchView searchView=view.findViewById(R.id.searchView);
        recyclerView=view.findViewById(R.id.recyclerSearch);

        txtPrevious.setOnClickListener(this);
        searchView.setOnQueryTextListener(this);
        searchView.setOnQueryTextFocusChangeListener(this);

        searchView.setIconified(false);
        searchView.setFocusable(true);

        presenter=new SearchPresenter();
        presenter.setView(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new MemoAdapter(getContext(), MemoType.RECYCLER, fontSize, textLine, textEllipsize);
        recyclerView.setAdapter(adapter);

        adapter.clear();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.previousSearch){ ((MainActivity)getActivity()).replaceFragment(new ListFragment()); }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(TextUtils.isEmpty(newText)){
            adapter.clear();
            recyclerView.removeAllViews();
        }else{
            presenter.getData(RoomDB.getInstance(getContext()).memoDao(),newText);
        }
        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            InputMethodManager imm=(InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(v.findFocus(),InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void setItems(ArrayList<MemoData> items) {
        adapter.setItems(items);
    }

    private void resetTextSetting(){
        SharedPreferences preferences= getActivity().getSharedPreferences(getResources().getString(R.string.searchTextSetting), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(getResources().getString(R.string.textEllipsize), textEllipsize);
        editor.putInt(getResources().getString(R.string.textLine), textLine);
        editor.putFloat(getResources().getString(R.string.fontSize), fontSize);

        editor.apply();
    }

}