package com.example.happybankbook.helper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.contrib.PickerActions.setDate;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.widget.DatePicker;

import androidx.core.content.ContextCompat;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.happybankbook.R;

public class TestHelper {

    public static final int black=
            ContextCompat.getColor( ApplicationProvider.getApplicationContext(),
                    com.example.happybankbook.R.color.black  );
    public static final int gray =
            ContextCompat.getColor( ApplicationProvider.getApplicationContext(),
                    com.example.happybankbook.R.color.gray   );

    public static void saveMemo(boolean isMain, String memo, String price){
        if(isMain){
            onView(ViewMatchers.withId(R.id.addMenu)).perform(click());
        }
        onView(withId(R.id.editMemo)).perform(typeText(memo));
        onView(withId(R.id.save)).perform(click());
        onView(withId(R.id.editHappy)).perform(typeText(price));
        onView(withId(R.id.ok)).perform(click());
    }

    public static void selectDay(int id, int day){
        onView(withId(id)).perform(click());
        onView(withClassName(org.hamcrest.Matchers.equalTo(DatePicker.class.getName())))
                .perform(setDate(2025, 2, day));
        onView(withText("확인")).perform(click());
    }

    public static void saveMemoWithDay(boolean isMain, String memo, String price, int day){
        if(isMain){
            onView(withId(R.id.addMenu)).perform(click());
        }
        selectDay(R.id.txtMemoDate,day);
        saveMemo(false,memo, price);
    }

}
