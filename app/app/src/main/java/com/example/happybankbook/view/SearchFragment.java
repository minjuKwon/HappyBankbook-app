package com.example.happybankbook.view;

import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_TEXT_LINE;
import static com.example.happybankbook.constants.BundleKeys.BUNDLE_KEY_TEXT_SIZE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_SEARCH_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_SEARCH_TEXT_LINE;
import static com.example.happybankbook.constants.FragmentRequestKeys.REQUEST_KEY_SEARCH_TEXT_SIZE;
import static com.example.happybankbook.constants.FragmentTag.LIST;
import static com.example.happybankbook.constants.PreferencesDefaults.PREF_DEFAULT_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.PreferencesDefaults.PREF_DEFAULT_TEXT_LINE;
import static com.example.happybankbook.constants.PreferencesDefaults.PREF_DEFAULT_TEXT_SIZE_LARGE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_ELLIPSIZE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_LINE;
import static com.example.happybankbook.constants.PreferencesKeys.PREF_KEY_TEXT_SIZE;
import static com.example.happybankbook.constants.PreferencesNames.PREF_NAME_SEARCH_TEXT_STYLE;
import static com.example.happybankbook.constants.TextStyles.TEXT_ELLIPSIZE_DEFAULT;
import static com.example.happybankbook.constants.TextStyles.TEXT_LINE_DEFAULT;
import static com.example.happybankbook.constants.TextStyles.TEXT_SIZE_DEFAULT_LARGE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.happybankbook.adapter.ListAdapter;
import com.example.happybankbook.R;
import com.example.happybankbook.adapter.OnItemSelectedListener;
import com.example.happybankbook.contract.SearchContract;
import com.example.happybankbook.db.UiMemoData;
import com.example.happybankbook.presenter.SearchPresenter;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchFragment extends Fragment
        implements View.OnClickListener, View.OnFocusChangeListener,
                    SearchView.OnQueryTextListener, SearchContract.View
{
    @Inject
    SearchPresenter presenter;

    private Context mContext;
    private Activity mActivity;
    private OnItemSelectedListener callback;
    private ListAdapter adapter;

    private RecyclerView recyclerView;

    private int textLine= TEXT_LINE_DEFAULT;
    private float textSize= TEXT_SIZE_DEFAULT_LARGE;
    private boolean hasTextEllipsize= TEXT_ELLIPSIZE_DEFAULT;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
        if(context instanceof Activity){
            mActivity=(Activity)context;
        }
        if(context instanceof OnItemSelectedListener){
            callback = (OnItemSelectedListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentResult();
    }

    private void getFragmentResult(){
        getParentFragmentManager()
                .setFragmentResultListener(
                        REQUEST_KEY_SEARCH_TEXT_SIZE,
                        this,
                        (requestKey, result) -> {
                            textSize=result.getFloat(BUNDLE_KEY_TEXT_SIZE);
                            adapter.setTextSize(textSize);
                        }
                );
        //변경 text line 값
        getParentFragmentManager()
                .setFragmentResultListener(
                        REQUEST_KEY_SEARCH_TEXT_LINE,
                        this,
                        (requestKey, result) -> {
                            textLine=result.getInt(BUNDLE_KEY_TEXT_LINE);
                            adapter.setTextLine(textLine);
                        }
                );
        //변경 text ellipsize 값
        getParentFragmentManager()
                .setFragmentResultListener(
                        REQUEST_KEY_SEARCH_TEXT_ELLIPSIZE,
                        this,
                        (requestKey, result) -> {
                            hasTextEllipsize=result.getBoolean(BUNDLE_KEY_TEXT_ELLIPSIZE);
                            adapter.setTextEllipsize(hasTextEllipsize);
                        }
                );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_search, container, false);
        init(view);
        return view;
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

        presenter.setView(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new ListAdapter(textSize, textLine, hasTextEllipsize, item ->
                callback.onItemSelected(item.getIdx())
        );
        recyclerView.setAdapter(adapter);

        adapter.clearItems();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences=
                mActivity.getSharedPreferences(PREF_NAME_SEARCH_TEXT_STYLE,Context.MODE_PRIVATE);
        hasTextEllipsize=preferences.getBoolean(PREF_KEY_TEXT_ELLIPSIZE, PREF_DEFAULT_TEXT_ELLIPSIZE);
        textLine=preferences.getInt(PREF_KEY_TEXT_LINE, PREF_DEFAULT_TEXT_LINE);
        textSize=preferences.getFloat(PREF_KEY_TEXT_SIZE, PREF_DEFAULT_TEXT_SIZE_LARGE);

        adapter.setTextEllipsize(hasTextEllipsize);
        adapter.setTextLine(textLine);
        adapter.setTextSize(textSize);
    }

    @Override
    public void onStop() {
        super.onStop();
        resetTextStyle();
    }

    private void resetTextStyle(){
        SharedPreferences preferences=
                mActivity.getSharedPreferences(PREF_NAME_SEARCH_TEXT_STYLE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean(PREF_KEY_TEXT_ELLIPSIZE, hasTextEllipsize);
        editor.putInt(PREF_KEY_TEXT_LINE, textLine);
        editor.putFloat(PREF_KEY_TEXT_SIZE, textSize);

        editor.apply();
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

    @Override
    public void setItems(ArrayList<UiMemoData> items) {
        adapter.setItems(items);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.previousSearch){
            ((MainActivity)mActivity).replaceFragment(new ListFragment(),LIST);
        }
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
            presenter.getData(newText);
        }
        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            InputMethodManager inputMethodManager=
                    (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(v.findFocus(),InputMethodManager.SHOW_IMPLICIT);
        }
    }

}