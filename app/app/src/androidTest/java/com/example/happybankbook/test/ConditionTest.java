package com.example.happybankbook.test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.happybankbook.util.CustomerMatcher.atPosition;
import static com.example.happybankbook.util.CustomerMatcher.withTextColor;
import static com.example.happybankbook.util.TestUtil.getCurrentDate;

import androidx.core.content.ContextCompat;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.happybankbook.MainActivity;
import com.example.happybankbook.R;
import com.example.happybankbook.util.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConditionTest {

    private static final TestUtil util=new TestUtil();
    private static boolean isMemoSaved=false;
    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp(){
        scenario= ActivityScenario.launch(MainActivity.class);

        if(!isMemoSaved){
            util.saveMemoWithDay(true, "memo 1", "10", 3);
            util.saveMemoWithDay(true, "memo 2", "20", 5);
            util.saveMemoWithDay(true, "memo 3", "30", 8);
            util.saveMemoWithDay(true, "memo 4", "40", 9);
            util.saveMemoWithDay(true, "memo 5", "50", 9);
            isMemoSaved=true;
        }

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.txtCondition)).perform(click());
    }

    @After
    public void closeScenario(){
        if(scenario!=null) scenario.close();
    }

    @Test
    public void givenConditionScreen_whenClickedNothing_thenScreenIsInitialed(){
        checkInitialCondition();

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonInit)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonSubmit)).check(matches(isDisplayed()));
    }

    @Test
    public void givenConditionScreen_whenClickedDurationText_thenCurrentDateButtonIsShown(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.duration)).perform(click());

        int expectedColor = ContextCompat
                .getColor(ApplicationProvider.getApplicationContext(), com.example.happybankbook.R.color.black);
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.duration)).check(matches(withTextColor(expectedColor)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.fromDuration)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.toDuration)).check(matches(isDisplayed()));

        String currentDate=getCurrentDate();
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.fromDuration)).check(matches(withText(currentDate)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.toDuration)).check(matches(withText(currentDate)));
        
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonInit)).perform(click());
    }

    @Test
    public void givenConditionScreen_whenInputCnt_thenCorrectFilteredListIsDisplayed(){
        //조건
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.editCount)).perform(typeText("3"));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonSubmit)).perform(click());

        //recyclerview 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).check(matches(hasChildCount(3)));
        for(int i=0;i<3;i++){
            String str=String.valueOf(10*(5-i));
            checkRecyclerViewItemPrice(i, str);
        }

        //viewpager 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        checkBackViewpagerItemPrice("50");
        checkBackViewpagerItemPrice("40");
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPriceTxt)).check(matches(withText("30")));
        checkForwardViewpagerItemPrice("40");
        checkForwardViewpagerItemPrice("50");

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenInputDateAndCnt_thenCorrectFilteredListIsDisplayed(){
        //조건
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.duration)).perform(click());
        util.selectDay(com.example.happybankbook.R.id.fromDuration, 3);
        util.selectDay(com.example.happybankbook.R.id.toDuration, 8);
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.editCount)).perform(typeText("2"));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonSubmit)).perform(click());

        //recyclerview 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).check(matches(hasChildCount(2)));
        checkRecyclerViewItemPrice(0, "30");
        checkRecyclerViewItemPrice(1, "20");

        //viewpager 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        checkBackViewpagerItemPrice("30");
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPriceTxt)).check(matches(withText("20")));
        checkForwardViewpagerItemPrice("30");

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenSortedDesc_thenCorrectFilteredListIsDisplayed(){
        //조건
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioOldest)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonSubmit)).perform(click());

        //recyclerview 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).check(matches(hasChildCount(5)));
        for(int i=0;i<5;i++){
            String str=String.valueOf(10*(i+1));
            checkRecyclerViewItemPrice(i, str);
        }

        //viewpager 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        for(int i=1;i<5;i++){
            String str=String.valueOf(10*i);
            checkBackViewpagerItemPrice(str);
        }
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPriceTxt)).check(matches(withText("50")));
        for(int i=4;i>0;i--){
            String str=String.valueOf(10*i);
            checkForwardViewpagerItemPrice(str);
        }

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenSortedDescAndInputCnt_thenCorrectFilteredListIsDisplayed(){
        //조건
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioOldest)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.editCount)).perform(typeText("2"));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonSubmit)).perform(click());

        //recyclerview 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).check(matches(hasChildCount(2)));
        for(int i=0;i<2;i++){
            String str=String.valueOf(10*(i+1));
            checkRecyclerViewItemPrice(i, str);
        }

        //viewpager 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        checkBackViewpagerItemPrice("10");
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPriceTxt)).check(matches(withText("20")));
        checkForwardViewpagerItemPrice("10");

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenSortedDescAndInputDateAndCnt_thenCorrectFilteredListIsDisplayed(){
        //조건
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.duration)).perform(click());
        util.selectDay(com.example.happybankbook.R.id.fromDuration, 3);
        util.selectDay(com.example.happybankbook.R.id.toDuration, 8);
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioOldest)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.editCount)).perform(typeText("2"));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonSubmit)).perform(click());

        //recyclerview 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).check(matches(hasChildCount(2)));
        for(int i=0;i<2;i++){
            String str=String.valueOf(10*(i+1));
            checkRecyclerViewItemPrice(i, str);
        }

        //viewpager 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        checkBackViewpagerItemPrice("10");
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPriceTxt)).check(matches(withText("20")));
        checkForwardViewpagerItemPrice("10");

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenClickedInitialBtn_thenScreenIsInitialed(){
        setTempCondition();
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonInit)).perform(click());

        checkInitialCondition();
    }

    @Test
    public void givenConditionScreenWithFilter_whenClickedListItem_thenConditionIsRetained(){
        setTempCondition();
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonSubmit)).perform(click());

        checkTempConditionList();

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPrevious)).perform(click());

        checkTempConditionList();

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.txtCondition)).perform(click());

        //조건 유지 확인
        int expectedColor = ContextCompat
                .getColor(ApplicationProvider.getApplicationContext(), com.example.happybankbook.R.color.black);
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.duration)).check(matches(withTextColor(expectedColor)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.fromDuration)).check(matches(withText("2025.02.05")));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.toDuration)).check(matches(withText("2025.02.09")));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioOldest)).check(matches(isChecked()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.editCount)).check(matches(withText("3")));

        resetCondition();
    }

    @Test
    public void givenConditionScreenWithFilter_whenMovedOtherScreen_thenConditionIsInitialed(){
        setTempCondition();
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonSubmit)).perform(click());

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.addMenu)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.mainMenu)).perform(click());

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).check(matches(hasChildCount(5)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.txtCondition)).perform(click());

        checkInitialCondition();
    }

    private void checkInitialCondition(){
        int expectedColor = ContextCompat
                .getColor(ApplicationProvider.getApplicationContext(), com.example.happybankbook.R.color.darkGray);
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.duration)).check(matches(withTextColor(expectedColor)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.fromDuration))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.toDuration))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioNewest)).check(matches(isChecked()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.editCount)).check(matches(withText("")));
    }

    private void resetCondition(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.txtCondition)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonInit)).perform(click());
    }

    private void checkRecyclerViewItemPrice(int position, String text){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .check(matches(atPosition(position, hasDescendant(withText(text)))));
    }

    private void checkBackViewpagerItemPrice(String text){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPriceTxt)).check(matches(withText(text)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).perform(click());
    }

    private void checkForwardViewpagerItemPrice(String text){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPriceTxt)).check(matches(withText(text)));
    }

    private void setTempCondition(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.duration)).perform(click());
        util.selectDay(com.example.happybankbook.R.id.fromDuration, 5);
        util.selectDay(com.example.happybankbook.R.id.toDuration, 9);
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioOldest)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.editCount)).perform(typeText("3"));
    }

    private void checkTempConditionList(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).check(matches(hasChildCount(3)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .check(matches(atPosition(0, hasDescendant(withText("20")))));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .check(matches(atPosition(1, hasDescendant(withText("30")))));
        onView(ViewMatchers.withId(R.id.recyclerMemo))
                .check(matches(atPosition(2, hasDescendant(withText("40")))));
    }

}