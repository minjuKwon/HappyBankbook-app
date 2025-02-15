package com.example.happybankbook.helper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.happybankbook.util.TestUtil.waitFor;

import androidx.test.espresso.matcher.ViewMatchers;

import com.example.happybankbook.R;

public class DetailScreenHelper {

    public static void checkDetailScreenClickedBack(String txt){
        checkMiddleScreen(txt);
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).perform(click());
    }

    public static void checkDetailScreenClickedForward(String txt){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).perform(click());
        checkMiddleScreen(txt);
    }

    public static void checkFirstScreen(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(isRoot()).perform(waitFor(500));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPriceTxt))
                .check(matches(withText("50")));
    }

    public static void checkLastScreen(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(isRoot()).perform(waitFor(500));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPriceTxt))
                .check(matches(withText("10")));
    }

    public static void checkMiddleScreen(String txt){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(isRoot()).perform(waitFor(500));
        onView(ViewMatchers.withId(R.id.memoDetailPriceTxt)).check(matches(withText(txt)));
    }

}
