package com.example.happybankbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

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

        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,listFragment).commit();
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                   if(item.getItemId()== R.id.mainMenu){
                       getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,listFragment).commit();
                       return true;
                   }
                   else if(item.getItemId()==R.id.addMenu){
                       getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,memoFragment).commit();
                       return true;
                   }
                   else if(item.getItemId()==R.id.settingMenu){
                       getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,settingFragment).commit();
                       return true;
                   }
                return false;
            }
        });
    }

}