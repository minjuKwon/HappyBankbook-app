package com.example.happybankbook.view;

import static com.example.happybankbook.constants.ConditionDefaults.DEFAULT_COUNT;
import static com.example.happybankbook.constants.ConditionDefaults.DEFAULT_FROM_DATE;
import static com.example.happybankbook.constants.ConditionDefaults.DEFAULT_SORT;
import static com.example.happybankbook.constants.ConditionDefaults.DEFAULT_TO_DATE;
import static com.example.happybankbook.constants.FragmentTag.CONDITION;
import static com.example.happybankbook.constants.FragmentTag.LIST;
import static com.example.happybankbook.constants.FragmentTag.MEMO;
import static com.example.happybankbook.constants.FragmentTag.SETTING;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.happybankbook.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements OnItemSelectedListener {

    private BottomNavigationView navigationView;
    private ListFragment listFragment;
    private MemoFragment memoFragment;
    private SettingFragment settingFragment;

    private final ListConditionState listState = new ListConditionState();

    public ListConditionState getListState() {
        return listState;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        navigationView=findViewById(R.id.bottomNavi);

        listFragment=new ListFragment();
        memoFragment=new MemoFragment();
        settingFragment=new SettingFragment();

        navigation();
    }

    public void navigation(){
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,listFragment).commit();
        navigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId()== R.id.mainMenu){
                replaceFragment(listFragment,LIST);
                return true;
            }
            else if(item.getItemId()==R.id.addMenu){
                replaceFragment(memoFragment,MEMO);
                return true;
            }
            else if(item.getItemId()==R.id.settingMenu){
                replaceFragment(settingFragment,SETTING);
                return true;
            }
            return false;
        });
    }
    public void navigation(int id){
        if(id== R.id.mainMenu){
            navigationView.setSelectedItemId(id);
            replaceFragment(listFragment,LIST);
        }
        else if(id==R.id.addMenu){
            navigationView.setSelectedItemId(id);
            replaceFragment(memoFragment,MEMO);
        }
        else if(id==R.id.settingMenu){
            navigationView.setSelectedItemId(id);
            replaceFragment(settingFragment, SETTING);
        }
    }

    public void replaceFragment(Fragment fragment, String tag){
        resetCondition();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,fragment,tag).commit();
    }

    public void addFragment(Fragment fragment){
        Fragment existing = getSupportFragmentManager().findFragmentByTag(CONDITION);
        if(existing==null){
            getSupportFragmentManager().beginTransaction().add(R.id.mainLayout,fragment,CONDITION).commit();
        }
    }

    public void removeFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    public void popFragment(){
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onItemSelected(int itemId) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mainLayout, MemoDetailFragment.newInstance(itemId))
                .addToBackStack(null)
                .commit();
    }

    public void onConditionChanged() {
        Fragment f = getSupportFragmentManager()
                .findFragmentByTag(LIST);

        if (f instanceof ListFragment) {
            ((ListFragment) f).getCondition();
            ((ListFragment) f).keepCondition();
        }
    }

    public void resetCondition(){
        listState.isNewestSort=DEFAULT_SORT;
        listState.fromDate=DEFAULT_FROM_DATE;
        listState.toDate=DEFAULT_TO_DATE;
        listState.count=DEFAULT_COUNT;
    }

}