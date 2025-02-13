package com.example.happybankbook;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.happybankbook.CustomerMatcher.withImageAlpha;
import static com.example.happybankbook.TestUtil.waitFor;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DetailScreenTest {

    private static final TestUtil util=new TestUtil();
    private static boolean isMemoSaved=false;
    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp(){
        scenario=ActivityScenario.launch(MainActivity.class);
        if(!isMemoSaved){
            util.saveMemo(true, "1", "10");
            util.saveMemo(true, "2", "20");
            util.saveMemo(true, "3", "30");
            util.saveMemo(true, "4", "40");
            util.saveMemo(true, "5", "50");
            isMemoSaved=true;
        }
    }

    @After
    public void closeScenario(){
        if(scenario!=null) scenario.close();
    }

   @Test
    public void givenDetailScreen_whenClickedBackBtn_thenBtnIsNotShownAFewLater(){
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(1,click()));

        onView(withId(R.id.imgBack)).check(matches(withImageAlpha(255)));
        onView(withId(R.id.imgBack)).perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withId(R.id.imgBack)).check(matches(withImageAlpha(0)));
    }

   @Test
    public void givenDetailScreen_whenClickedForwardBtn_thenBtnIsNotShownAFewLater(){
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(2,click()));

        onView(withId(R.id.imgBack)).check(matches(withImageAlpha(255)));
        onView(withId(R.id.imgForward)).perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(withId(R.id.imgForward)).check(matches(withImageAlpha(0)));
    }

    @Test
    public void given0thItemDetailScreen_whenClickedBackBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));

        checkFirstScreen();
        onView(withId(R.id.imgBack)).perform(click());

        for(int i=4;i>1;i--){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedBack(txt);
        }

        checkLastScreen();

        for(int i=2;i<5;i++){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedForward(txt);
        }

        onView(withId(R.id.imgForward)).perform(click());
        checkFirstScreen();
    }

    @Test
    public void given1thItemDetailScreen_whenClickedBackBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(1,click()));

        for(int i=4;i>1;i--){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedBack(txt);
        }

        checkLastScreen();

        for(int i=2;i<5;i++){
            Log.d("dd","i: "+i);
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedForward(txt);
            onView(isRoot()).perform(waitFor(300));
        }

    }

    @Test
    public void given1thItemDetailScreen_whenClickedForwardBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(1,click()));

        checkMiddleScreen("40");

        onView(withId(R.id.imgForward)).perform(click());
        checkFirstScreen();
        onView(withId(R.id.imgBack)).perform(click());

        checkMiddleScreen("40");
    }

    @Test
    public void given2ndItemDetailScreen_whenClickedBackBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(2,click()));

        for(int i=3;i>1;i--){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedBack(txt);
        }

        checkLastScreen();

        for(int i=2;i<3;i++){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedForward(txt);
        }

        onView(withId(R.id.imgForward)).perform(click());
        checkMiddleScreen("30");
    }

    @Test
    public void given2ndItemDetailScreen_whenClickedForwardBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(2,click()));

        checkMiddleScreen("30");
        checkDetailScreenClickedForward("40");
        onView(withId(R.id.imgForward)).perform(click());

        checkFirstScreen();

        onView(withId(R.id.imgBack)).perform(click());
        checkDetailScreenClickedBack("40");
        checkMiddleScreen("30");
    }

    @Test
    public void given3rdItemDetailScreen_whenClickedBackBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(3,click()));

        checkDetailScreenClickedBack("20");
        checkLastScreen();
        checkDetailScreenClickedForward("20");
    }

    @Test
    public void given3rdItemDetailScreen_whenClickedForwardBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(3,click()));

        checkMiddleScreen("20");

        for(int i=3;i<5;i++){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedForward(txt);
        }

        onView(withId(R.id.imgForward)).perform(click());
        checkFirstScreen();
        onView(withId(R.id.imgBack)).perform(click());

        for(int i=4;i>1;i--){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedBack(txt);
        }

    }

    @Test
    public void given4thItemDetailScreen_whenClickedForwardBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(4,click()));

        checkLastScreen();

        for(int i=2;i<5;i++){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedForward(txt);
        }

        onView(withId(R.id.imgForward)).perform(click());
        checkFirstScreen();

        onView(withId(R.id.imgBack)).perform(click());
        for(int i=4;i>1;i--){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedBack(txt);
        }

        checkLastScreen();
    }


    private void checkDetailScreenClickedBack(String txt){
        checkMiddleScreen(txt);
        onView(withId(R.id.imgBack)).perform(click());
    }

    private void checkDetailScreenClickedForward(String txt){
        onView(withId(R.id.imgForward)).perform(click());
        checkMiddleScreen(txt);
    }

    private void checkFirstScreen(){
        onView(withId(R.id.imgForward))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.imgBack))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.memoDetailPriceTxt)).check(matches(withText("50")));
    }

    private void checkLastScreen(){
        onView(withId(R.id.imgForward))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imgBack))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.memoDetailPriceTxt)).check(matches(withText("10")));
    }

    private void checkMiddleScreen(String txt){
        onView(withId(R.id.imgForward)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.imgBack))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.memoDetailPriceTxt)).check(matches(withText(txt)));
    }

}
