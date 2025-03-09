package com.example.happybankbook;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Utils {

    private Utils(){}//객체 생성 방지

    public static void hideKeyboard(Context context, View view){
        InputMethodManager imm=
                (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

}
