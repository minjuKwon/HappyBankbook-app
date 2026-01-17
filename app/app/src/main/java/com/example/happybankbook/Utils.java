package com.example.happybankbook;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happybankbook.presenter.ListPresenter;
import com.example.happybankbook.view.MainActivity;
import com.google.android.apps.common.testing.accessibility.framework.BuildConfig;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    private Utils(){}//객체 생성 방지

    public static String setCurrentDate(){
        SimpleDateFormat dateFormat=
                new SimpleDateFormat("yyyy.MM.dd",java.util.Locale.getDefault());
        Date date=new Date();
        return dateFormat.format(date);
    }

    public static void setCurrentDate(TextView textView){
        textView.setText(setCurrentDate());
    }

    public static String formatDateToString(int date){
        String strDate=Integer.toString(date);
        String year=strDate.substring(0,4);
        String month=strDate.substring(4,6);
        String day=strDate.substring(6);
        return String.format(java.util.Locale.getDefault(), "%s.%s.%s", year, month, day);
    }

    public static void setDate(TextView textview, Context context){

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

    public static int convertDateToInt(TextView textView){
        String [] dateStr=textView.getText().toString().split("\\.");
        return Integer.parseInt(dateStr[0]+dateStr[1]+dateStr[2]);
    }

    public static void hideKeyboard(Context context, View view){
        InputMethodManager imm=
                (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public static void logDebugData(String tag, String message){
        if(BuildConfig.DEBUG){
            Log.e(tag, message);
        }
    }

    public static void showToastOnUi(Context mContext, int id){
        ((MainActivity)mContext).runOnUiThread( ()->
                Toast.makeText(
                        mContext,
                        mContext.getText(id),
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    public static void loadItemsByCondition(
            ListPresenter presenter,
            int fromDate,
            int toDate,
            int itemCount,
            boolean isNewestSort
    ){
        if(itemCount==0){
            presenter.setIntResultCallback(value -> {
                if(isNewestSort){
                    presenter.getDataDesc(fromDate, toDate, value);
                }else{
                    presenter.getDataAsc(fromDate, toDate, value);
                }
            });
            presenter.getDataCount();
        }else{
            if(isNewestSort){
                presenter.getDataDesc(fromDate, toDate, itemCount);
            }else{
                presenter.getDataAsc(fromDate, toDate, itemCount);
            }
        }
    }

}
