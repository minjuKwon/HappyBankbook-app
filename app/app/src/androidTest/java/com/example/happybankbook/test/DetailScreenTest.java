package com.example.happybankbook.test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

import static com.example.happybankbook.helper.DetailScreenHelper.checkDetailScreenClickedBack;
import static com.example.happybankbook.helper.DetailScreenHelper.checkDetailScreenClickedForward;
import static com.example.happybankbook.helper.DetailScreenHelper.checkFirstScreen;
import static com.example.happybankbook.helper.DetailScreenHelper.checkLastScreen;
import static com.example.happybankbook.helper.DetailScreenHelper.checkMiddleScreen;
import static com.example.happybankbook.helper.TestHelper.saveMemo;
import static com.example.happybankbook.util.CustomerMatcher.withImageAlpha;
import static com.example.happybankbook.util.TestUtil.waitFor;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.happybankbook.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DetailScreenTest {

    private static boolean isMemoSaved=false;
    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp(){
        scenario=ActivityScenario.launch(MainActivity.class);
        if(!isMemoSaved){
            saveMemo(true, "1", "10");
            saveMemo(true, "2", "20");
            saveMemo(true, "3", "30");
            saveMemo(true, "4", "40");
            saveMemo(true, "5", "50");
            isMemoSaved=true;
        }
    }

    @After
    public void closeScenario(){
        if(scenario!=null) scenario.close();
    }

   @Test
    public void givenDetailScreen_whenClickedBackBtn_thenBtnIsNotShownAFewLater(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(1,click()));

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).check(matches(withImageAlpha(255)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).check(matches(withImageAlpha(0)));
    }

   @Test
    public void givenDetailScreen_whenClickedForwardBtn_thenBtnIsNotShownAFewLater(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(2,click()));

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).check(matches(withImageAlpha(255)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).check(matches(withImageAlpha(0)));
    }

    @Test
    public void given0thItemDetailScreen_whenClickedBackBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));

        checkFirstScreen();
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).perform(click());

        for(int i=4;i>1;i--){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedBack(txt);
        }

        checkLastScreen();

        for(int i=2;i<5;i++){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedForward(txt);
        }

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).perform(click());
        checkFirstScreen();
    }

    @Test
    public void given1thItemDetailScreen_whenClickedBackBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(1,click()));

        for(int i=4;i>1;i--){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedBack(txt);
        }

        checkLastScreen();

        for(int i=2;i<5;i++){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedForward(txt);
            onView(isRoot()).perform(waitFor(300));
        }

    }

    @Test
    public void given1thItemDetailScreen_whenClickedForwardBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(1,click()));

        checkMiddleScreen("40");

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).perform(click());
        checkFirstScreen();
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).perform(click());

        checkMiddleScreen("40");
    }

    @Test
    public void given2ndItemDetailScreen_whenClickedBackBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(2,click()));

        for(int i=3;i>1;i--){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedBack(txt);
        }

        checkLastScreen();

        for(int i=2;i<3;i++){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedForward(txt);
        }

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).perform(click());
        checkMiddleScreen("30");
    }

    @Test
    public void given2ndItemDetailScreen_whenClickedForwardBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(2,click()));

        checkMiddleScreen("30");
        checkDetailScreenClickedForward("40");
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).perform(click());

        checkFirstScreen();

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).perform(click());
        checkDetailScreenClickedBack("40");
        checkMiddleScreen("30");
    }

    @Test
    public void given3rdItemDetailScreen_whenClickedBackBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(3,click()));

        checkDetailScreenClickedBack("20");
        checkLastScreen();
        checkDetailScreenClickedForward("20");
    }

    @Test
    public void given3rdItemDetailScreen_whenClickedForwardBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(3,click()));

        checkMiddleScreen("20");

        for(int i=3;i<5;i++){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedForward(txt);
        }

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).perform(click());
        checkFirstScreen();
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).perform(click());

        for(int i=4;i>1;i--){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedBack(txt);
        }

    }

    @Test
    public void given4thItemDetailScreen_whenClickedForwardBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(4,click()));

        checkLastScreen();

        for(int i=2;i<5;i++){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedForward(txt);
        }

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).perform(click());
        checkFirstScreen();

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).perform(click());
        for(int i=4;i>1;i--){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedBack(txt);
        }

        checkLastScreen();
    }

}
