package com.example.happybankbook;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isFocused;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class MemoSaveTest {

    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(MainActivity.class);
    }

    @After
    public void closeActivityScenario(){
        if(scenario!=null)
            scenario.close();
    }

    @Before
    public void moveToMemoFragment(){
        onView(withId(R.id.addMenu)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.addMenu)).perform(click());
    }

    @Test
    public void givenAppIsLaunched_whenMemoFragmentIsDisplayed_thenCorrectInitialScreenIsShown(){
        onView(withId(R.id.addPicture)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.save)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.txtMemoDate)).check(matches(withText(getCurrentDate())));
        onView(withId(R.id.editMemo)).check(matches(withHint((R.string.memo))));
    }

    @Test
    public void givenMemoFragment_whenClickSaveButton_thenCorrectDialogScreenIsShown(){
        onView(withId(R.id.save)).perform(click());
        onView(withId(R.id.howHappy)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.editHappy)).check(matches(isFocused()));
        onView(withId(R.id.ok)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.cancel)).check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void givenMemoFragment_whenClickCancelButtonInDialog_thenDialogScreenIsDismissed(){
        onView(withId(R.id.save)).perform(click());
        onView(withId(R.id.howHappy)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.cancel)).perform(click());
        onView(withId(R.id.howHappy)).check(doesNotExist());
    }

    private String getCurrentDate(){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy.MM.dd",java.util.Locale.getDefault());
        Date date=new Date();
        return dateFormat.format(date);
    }

}
