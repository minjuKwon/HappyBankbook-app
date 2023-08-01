package com.example.happybankbook.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happybankbook.MainActivity;
import com.example.happybankbook.MemoRecyclerAdapter;
import com.example.happybankbook.R;
import com.example.happybankbook.contract.SearchContract;
import com.example.happybankbook.db.MemoData;
import com.example.happybankbook.db.RoomDB;
import com.example.happybankbook.presenter.SearchPresenter;
import com.example.happybankbook.view.ListFragment;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener, SearchContract.View {

    private TextView txtPrevious;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private SearchPresenter presenter;
    private MemoRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_search, container, false);
        init(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.releaseView();
    }

    private void init(View view){
        txtPrevious=view.findViewById(R.id.previousSearch);
        searchView=view.findViewById(R.id.searchView);
        recyclerView=view.findViewById(R.id.recyclerSearch);

        txtPrevious.setOnClickListener(this);
        searchView.setOnQueryTextListener(this);

        searchView.setIconified(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new MemoRecyclerAdapter(getContext());
        recyclerView.setAdapter(adapter);

        presenter=new SearchPresenter();
        presenter.setView(this);
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
    public void setItems(ArrayList<MemoData> items) {
        adapter.setItems(items);
    }

}