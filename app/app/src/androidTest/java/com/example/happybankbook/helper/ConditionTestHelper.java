package com.example.happybankbook.helper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.happybankbook.helper.TestHelper.selectDay;
import static com.example.happybankbook.util.CustomerMatcher.atPosition;
import static com.example.happybankbook.util.CustomerMatcher.withTextColor;

import androidx.core.content.ContextCompat;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.happybankbook.R;

public class ConditionTestHelper {

    private ConditionTestHelper(){}
    public static void resetCondition(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.txtCondition)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonInit)).perform(click());
    }


    public static void checkInitialCondition(){
        int expectedColor = ContextCompat
                .getColor(ApplicationProvider.getApplicationContext(),
                        com.example.happybankbook.R.color.darkGray);

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.duration))
                .check(matches(withTextColor(expectedColor)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.fromDuration))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.toDuration))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioNewest))
                .check(matches(isChecked()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.editCount))
                .check(matches(withText("")));
    }

    public static void setDateCondition(int fromDay, int toDay){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.duration)).perform(click());
        selectDay(com.example.happybankbook.R.id.fromDuration, fromDay);
        selectDay(com.example.happybankbook.R.id.toDuration, toDay);
    }
    public static void setCountCondition(String count){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.editCount))
                .perform(typeText(count));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonSubmit)).perform(click());
    }

    public static void setTempCondition(int fromDay, int toDay, int count){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.duration)).perform(click());
        selectDay(com.example.happybankbook.R.id.fromDuration, fromDay);
        selectDay(com.example.happybankbook.R.id.toDuration, toDay);

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioOldest)).perform(click());

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.editCount))
                .perform(typeText(String.valueOf(count)));
    }

    public static void checkRecyclerviewSize(int size){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .check(matches(hasChildCount(size)));
    }

    public static void checkRecyclerViewItem(int position, String text){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .check(matches(atPosition(position, hasDescendant(withText(text)))));
    }

    public static void checkViewpagerItemBack(String text){
        checkViewpagerItemCurrent(text);
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).perform(click());
    }

    public static void checkViewpagerItemForward(String text){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).perform(click());
        checkViewpagerItemCurrent(text);
    }

    public static void checkViewpagerItemCurrent(String text){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPriceTxt))
                .check(matches(withText(text)));
    }

    public static void checkTempConditionList(String price0, String price1, String price2, int count){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .check(matches(hasChildCount(count)));

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .check(matches(atPosition(0, hasDescendant(withText(price0)))));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .check(matches(atPosition(1, hasDescendant(withText(price1)))));
        onView(ViewMatchers.withId(R.id.recyclerMemo))
                .check(matches(atPosition(2, hasDescendant(withText(price2)))));
    }

}
