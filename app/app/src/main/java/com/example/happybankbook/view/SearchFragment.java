package com.example.happybankbook.view;

import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_SEARCH_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_SEARCH_TEXT_LINE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_SEARCH_TEXT_SIZE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_LINE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_SIZE;
import static com.example.happybankbook.constants.PreferencesNames.PREF_NAME_SEARCH_TEXT_STYLE;

import android.app.Activity;
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
    private boolean hasTextEllipsize=true;
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
        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY_SEARCH_TEXT_SIZE, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                fontSize=result.getFloat(getResources().getString(R.string.text_Size));
                adapter.setFont(fontSize);
            }
        });
        //변경 text line 값
        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY_SEARCH_TEXT_LINE, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                textLine=result.getInt(getResources().getString(R.string.text_line));
                adapter.setTextLine(textLine);
            }
        });
        //변경 text ellipsize 값
        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY_SEARCH_TEXT_ELLIPSIZE, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                hasTextEllipsize=result.getBoolean(getResources().getString(R.string.text_ellipsize));
                adapter.setTextEllipsize(hasTextEllipsize);
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

        SharedPreferences preferences= mActivity.getSharedPreferences(PREF_NAME_SEARCH_TEXT_STYLE,Context.MODE_PRIVATE);
        hasTextEllipsize=preferences.getBoolean(PREF_KEY_TEXT_ELLIPSIZE,true);
        textLine=preferences.getInt(PREF_KEY_TEXT_LINE,2);
        fontSize=preferences.getFloat(PREF_KEY_TEXT_SIZE,15);

        adapter.setTextEllipsize(hasTextEllipsize);
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

    @Override
    public void onDetach() {
        super.onDetach();
        mContext=null;
        mActivity=null;
    }

    private void init(View view){
        TextView previousTextView=view.findViewById(R.id.previousSearch);
        SearchView searchView=view.findViewById(R.id.searchView);
        recyclerView=view.findViewById(R.id.recyclerSearch);

        previousTextView.setOnClickListener(this);
        searchView.setOnQueryTextListener(this);
        searchView.setOnQueryTextFocusChangeListener(this);

        searchView.setIconified(false);
        searchView.setFocusable(true);

        presenter=new SearchPresenter();
        presenter.setView(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new MemoAdapter(getContext(), MemoType.RECYCLER, fontSize, textLine, hasTextEllipsize);
        recyclerView.setAdapter(adapter);

        adapter.clearItems();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.previousSearch){ ((MainActivity)mActivity).replaceFragment(new ListFragment()); }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(TextUtils.isEmpty(newText)){
            adapter.clearItems();
            recyclerView.removeAllViews();
        }else{
            presenter.getData(RoomDB.getInstance(getContext()).memoDao(),newText);
        }
        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            InputMethodManager inputMethodManager=(InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(v.findFocus(),InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void setItems(ArrayList<MemoData> items) {
        adapter.setItems(items);
    }

    private void resetTextSetting(){
        SharedPreferences preferences= mActivity.getSharedPreferences(PREF_NAME_SEARCH_TEXT_STYLE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(PREF_KEY_TEXT_ELLIPSIZE, hasTextEllipsize);
        editor.putInt(PREF_KEY_TEXT_LINE, textLine);
        editor.putFloat(PREF_KEY_TEXT_SIZE, fontSize);

        editor.apply();
    }

}