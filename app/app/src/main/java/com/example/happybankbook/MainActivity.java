package com.example.happybankbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.example.happybankbook.view.ListFragment;
import com.example.happybankbook.view.MemoFragment;
import com.example.happybankbook.view.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    public String setCurrentDate(){
        SimpleDateFormat dateFormat=
                new SimpleDateFormat("yyyy.MM.dd",java.util.Locale.getDefault());
        Date date=new Date();
        return dateFormat.format(date);
    }

    public void setCurrentDate(TextView textView){
        textView.setText(setCurrentDate());
    }

    public void setDate(TextView textview, Context context){

        DatePickerDialog.OnDateSetListener calendarListener= (view, year, month, dayOfMonth) -> {
            String date=String.format( java.util.Locale.getDefault(),
                                 "%d.%02d.%02d",
                                 year,month+1,dayOfMonth );
            textview.setText(date);
        };

        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(context,R.style.DialogTheme,calendarListener,year,month,day).show();

    }

    public int convertDateToInt(TextView textView){
        String [] dateStr=textView.getText().toString().split("\\.");
        return Integer.parseInt(dateStr[0]+dateStr[1]+dateStr[2]);
    }

}