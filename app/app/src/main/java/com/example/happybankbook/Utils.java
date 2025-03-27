package com.example.happybankbook;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.apps.common.testing.accessibility.framework.BuildConfig;

public class Utils {

    private Utils(){}//객체 생성 방지

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

}
