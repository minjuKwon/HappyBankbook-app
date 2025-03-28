package com.example.happybankbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.happybankbook.view.ListFragment;
import com.example.happybankbook.view.MemoFragment;
import com.example.happybankbook.view.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

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

}