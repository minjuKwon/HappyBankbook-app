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

    private TestHelper(){}

    public static final int BLACK=
            ContextCompat.getColor( ApplicationProvider.getApplicationContext(),
                    com.example.happybankbook.R.color.black  );
    public static final int GRAY =
            ContextCompat.getColor( ApplicationProvider.getApplicationContext(),
                    com.example.happybankbook.R.color.gray   );
    public static final int YEAR=2025;
    public static final int MONTH=2;
    public static final String dateFormat="%d.%02d.%02d";

    public static void saveMemo(boolean isMain, TestMemoData data){
        if(isMain){
            onView(ViewMatchers.withId(R.id.addMenu)).perform(click());
        }
        onView(withId(R.id.editMemo)).perform(typeText(data.getMemo()));
        onView(withId(R.id.save)).perform(click());
        onView(withId(R.id.editHappy)).perform(typeText(data.getPrice()));
        onView(withId(R.id.ok)).perform(click());
    }

    public static void selectDay(int id, int day){
        onView(withId(id)).perform(click());
        onView(withClassName(org.hamcrest.Matchers.equalTo(DatePicker.class.getName())))
                .perform(setDate(YEAR, MONTH, day));
        onView(withText("확인")).perform(click());
    }

    public static void saveMemoWithDay(boolean isMain, TestMemoWithDayData data){
        if(isMain){
            onView(withId(R.id.addMenu)).perform(click());
        }
        selectDay(R.id.txtMemoDate,data.getDay());
        saveMemo(false,data);
    }

}
