package com.example.happybankbook;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.contrib.PickerActions.setDate;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;
import android.widget.DatePicker;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

public class TestUtil {

    // 커스텀 waitFor() 구현
    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    public void saveMemo(boolean isMain, String memo, String price){
        if(isMain){
            onView(withId(R.id.addMenu)).perform(click());
        }
        onView(withId(R.id.editMemo)).perform(typeText(memo));
        onView(withId(R.id.save)).perform(click());
        onView(withId(R.id.editHappy)).perform(typeText(price));
        onView(withId(R.id.ok)).perform(click());
    }

    public void selectDay(int day){
        onView(withId(R.id.txtMemoDate)).perform(click());
        onView(withClassName(org.hamcrest.Matchers.equalTo(DatePicker.class.getName())))
                .perform(setDate(2025, 2, day));
        onView(withText("확인")).perform(click());
    }

    public void saveMemoWithDay(boolean isMain, String memo, String price, int day){
        if(isMain){
            onView(withId(R.id.addMenu)).perform(click());
        }
        selectDay(day);
        saveMemo(false,memo, price);
    }

}
