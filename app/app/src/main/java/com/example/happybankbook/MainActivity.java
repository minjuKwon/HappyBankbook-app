package com.example.happybankbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

        navigation();
    }

    public void navigation(){
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

    public void replaceFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout,fragment).commit();
    }

    public void addFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().add(R.id.mainLayout,fragment).commit();
    }

    public void setNowDate(TextView textView){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy.MM.dd");
        Date date=new Date();
        textView.setText(dateFormat.format(date));
    }

    public void setDate(TextView textview, Context context){

        DatePickerDialog.OnDateSetListener calendarListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                textview.setText(String.format("%d.%d.%d",year,month+1,dayOfMonth));
            }
        };

        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(context,R.style.DialogTheme,calendarListener,year,month,day).show();

    }

}