package com.example.happybankbook.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.happybankbook.R;
import com.example.happybankbook.adapter.OnItemSelectedListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements OnItemSelectedListener {

    private BottomNavigationView navigationView;
    private ListFragment listFragment;
    private MemoFragment memoFragment;
    private SettingFragment settingFragment;

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
                replaceFragment(listFragment);
                return true;
            }
            else if(item.getItemId()==R.id.addMenu){
                replaceFragment(memoFragment);
                return true;
            }
            else if(item.getItemId()==R.id.settingMenu){
                replaceFragment(settingFragment);
                return true;
            }
            return false;
        });
    }
    public void navigation(int id){
        if(id== R.id.mainMenu){
            navigationView.setSelectedItemId(id);
            replaceFragment(listFragment);
        }
        else if(id==R.id.addMenu){
            navigationView.setSelectedItemId(id);
            replaceFragment(memoFragment);
        }
        else if(id==R.id.settingMenu){
            navigationView.setSelectedItemId(id);
            replaceFragment(settingFragment);
        }
    }

    public void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,fragment).commit();
    }

    public void addFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().add(R.id.mainLayout,fragment).commit();
    }

    public void removeFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    @Override
    public void onItemSelected(int itemId) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mainLayout, MemoDetailFragment.newInstance(itemId))
                .commit();
    }

}