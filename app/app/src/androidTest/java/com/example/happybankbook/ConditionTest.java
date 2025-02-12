package com.example.happybankbook;

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

import static com.example.happybankbook.CustomerMatcher.atPosition;
import static com.example.happybankbook.CustomerMatcher.withTextColor;
import static com.example.happybankbook.TestUtil.getCurrentDate;

import androidx.core.content.ContextCompat;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;

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

        onView(withId(R.id.txtCondition)).perform(click());
    }

    @After
    public void closeScenario(){
        if(scenario!=null) scenario.close();
    }

    @Test
    public void givenConditionScreen_whenClickedNothing_thenScreenIsInitialed(){
        checkInitialCondition();

        onView(withId(R.id.buttonInit)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonSubmit)).check(matches(isDisplayed()));
    }

    @Test
    public void givenConditionScreen_whenClickedDurationText_thenCurrentDateButtonIsShown(){
        onView(withId(R.id.duration)).perform(click());

        int expectedColor = ContextCompat
                .getColor(ApplicationProvider.getApplicationContext(), R.color.black);
        onView(withId(R.id.duration)).check(matches(withTextColor(expectedColor)));
        onView(withId(R.id.fromDuration)).check(matches(isDisplayed()));
        onView(withId(R.id.toDuration)).check(matches(isDisplayed()));

        String currentDate=getCurrentDate();
        onView(withId(R.id.fromDuration)).check(matches(withText(currentDate)));
        onView(withId(R.id.toDuration)).check(matches(withText(currentDate)));
        
        onView(withId(R.id.buttonInit)).perform(click());
    }

    @Test
    public void givenConditionScreen_whenInputCnt_thenCorrectFilteredListIsDisplayed(){
        //조건
        onView(withId(R.id.editCount)).perform(typeText("3"));
        onView(withId(R.id.buttonSubmit)).perform(click());

        //recyclerview 확인
        onView(withId(R.id.recyclerMemo)).check(matches(hasChildCount(3)));
        for(int i=0;i<3;i++){
            String str=String.valueOf(10*(5-i));
            checkRecyclerViewItemPrice(i, str);
        }

        //viewpager 확인
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        checkBackViewpagerItemPrice("50");
        checkBackViewpagerItemPrice("40");
        onView(withId(R.id.memoDetailPriceTxt)).check(matches(withText("30")));
        checkForwardViewpagerItemPrice("40");
        checkForwardViewpagerItemPrice("50");

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenInputDateAndCnt_thenCorrectFilteredListIsDisplayed(){
        //조건
        onView(withId(R.id.duration)).perform(click());
        util.selectDay(R.id.fromDuration, 3);
        util.selectDay(R.id.toDuration, 8);
        onView(withId(R.id.editCount)).perform(typeText("2"));
        onView(withId(R.id.buttonSubmit)).perform(click());

        //recyclerview 확인
        onView(withId(R.id.recyclerMemo)).check(matches(hasChildCount(2)));
        checkRecyclerViewItemPrice(0, "30");
        checkRecyclerViewItemPrice(1, "20");

        //viewpager 확인
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        checkBackViewpagerItemPrice("30");
        onView(withId(R.id.memoDetailPriceTxt)).check(matches(withText("20")));
        checkForwardViewpagerItemPrice("30");

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenSortedDesc_thenCorrectFilteredListIsDisplayed(){
        //조건
        onView(withId(R.id.radioOldest)).perform(click());
        onView(withId(R.id.buttonSubmit)).perform(click());

        //recyclerview 확인
        onView(withId(R.id.recyclerMemo)).check(matches(hasChildCount(5)));
        for(int i=0;i<5;i++){
            String str=String.valueOf(10*(i+1));
            checkRecyclerViewItemPrice(i, str);
        }

        //viewpager 확인
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        for(int i=1;i<5;i++){
            String str=String.valueOf(10*i);
            checkBackViewpagerItemPrice(str);
        }
        onView(withId(R.id.memoDetailPriceTxt)).check(matches(withText("50")));
        for(int i=4;i>0;i--){
            String str=String.valueOf(10*i);
            checkForwardViewpagerItemPrice(str);
        }

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenSortedDescAndInputCnt_thenCorrectFilteredListIsDisplayed(){
        //조건
        onView(withId(R.id.radioOldest)).perform(click());
        onView(withId(R.id.editCount)).perform(typeText("2"));
        onView(withId(R.id.buttonSubmit)).perform(click());

        //recyclerview 확인
        onView(withId(R.id.recyclerMemo)).check(matches(hasChildCount(2)));
        for(int i=0;i<2;i++){
            String str=String.valueOf(10*(i+1));
            checkRecyclerViewItemPrice(i, str);
        }

        //viewpager 확인
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        checkBackViewpagerItemPrice("10");
        onView(withId(R.id.memoDetailPriceTxt)).check(matches(withText("20")));
        checkForwardViewpagerItemPrice("10");

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenSortedDescAndInputDateAndCnt_thenCorrectFilteredListIsDisplayed(){
        //조건
        onView(withId(R.id.duration)).perform(click());
        util.selectDay(R.id.fromDuration, 3);
        util.selectDay(R.id.toDuration, 8);
        onView(withId(R.id.radioOldest)).perform(click());
        onView(withId(R.id.editCount)).perform(typeText("2"));
        onView(withId(R.id.buttonSubmit)).perform(click());

        //recyclerview 확인
        onView(withId(R.id.recyclerMemo)).check(matches(hasChildCount(2)));
        for(int i=0;i<2;i++){
            String str=String.valueOf(10*(i+1));
            checkRecyclerViewItemPrice(i, str);
        }

        //viewpager 확인
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        checkBackViewpagerItemPrice("10");
        onView(withId(R.id.memoDetailPriceTxt)).check(matches(withText("20")));
        checkForwardViewpagerItemPrice("10");

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenClickedInitialBtn_thenScreenIsInitialed(){
        setTempCondition();
        onView(withId(R.id.buttonInit)).perform(click());

        checkInitialCondition();
    }

    @Test
    public void givenConditionScreenWithFilter_whenClickedListItem_thenConditionIsRetained(){
        setTempCondition();
        onView(withId(R.id.buttonSubmit)).perform(click());

        checkTempConditionList();

        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        onView(withId(R.id.memoDetailPrevious)).perform(click());

        checkTempConditionList();

        onView(withId(R.id.txtCondition)).perform(click());

        //조건 유지 확인
        int expectedColor = ContextCompat
                .getColor(ApplicationProvider.getApplicationContext(), R.color.black);
        onView(withId(R.id.duration)).check(matches(withTextColor(expectedColor)));
        onView(withId(R.id.fromDuration)).check(matches(withText("2025.02.05")));
        onView(withId(R.id.toDuration)).check(matches(withText("2025.02.09")));
        onView(withId(R.id.radioOldest)).check(matches(isChecked()));
        onView(withId(R.id.editCount)).check(matches(withText("3")));

        resetCondition();
    }

    @Test
    public void givenConditionScreenWithFilter_whenMovedOtherScreen_thenConditionIsInitialed(){
        setTempCondition();
        onView(withId(R.id.buttonSubmit)).perform(click());

        onView(withId(R.id.addMenu)).perform(click());
        onView(withId(R.id.mainMenu)).perform(click());

        onView(withId(R.id.recyclerMemo)).check(matches(hasChildCount(5)));
        onView(withId(R.id.txtCondition)).perform(click());

        checkInitialCondition();
    }

    private void checkInitialCondition(){
        int expectedColor = ContextCompat
                .getColor(ApplicationProvider.getApplicationContext(), R.color.darkGray);
        onView(withId(R.id.duration)).check(matches(withTextColor(expectedColor)));
        onView(withId(R.id.fromDuration))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.toDuration))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.radioNewest)).check(matches(isChecked()));
        onView(withId(R.id.editCount)).check(matches(withText("")));
    }

    private void resetCondition(){
        onView(withId(R.id.txtCondition)).perform(click());
        onView(withId(R.id.buttonInit)).perform(click());
    }

    private void checkRecyclerViewItemPrice(int position, String text){
        onView(withId(R.id.recyclerMemo))
                .check(matches(atPosition(position, hasDescendant(withText(text)))));
    }

    private void checkBackViewpagerItemPrice(String text){
        onView(withId(R.id.memoDetailPriceTxt)).check(matches(withText(text)));
        onView(withId(R.id.imgBack)).perform(click());
    }

    private void checkForwardViewpagerItemPrice(String text){
        onView(withId(R.id.imgForward)).perform(click());
        onView(withId(R.id.memoDetailPriceTxt)).check(matches(withText(text)));
    }

    private void setTempCondition(){
        onView(withId(R.id.duration)).perform(click());
        util.selectDay(R.id.fromDuration, 5);
        util.selectDay(R.id.toDuration, 9);
        onView(withId(R.id.radioOldest)).perform(click());
        onView(withId(R.id.editCount)).perform(typeText("3"));
    }

    private void checkTempConditionList(){
        onView(withId(R.id.recyclerMemo)).check(matches(hasChildCount(3)));
        onView(withId(R.id.recyclerMemo))
                .check(matches(atPosition(0, hasDescendant(withText("20")))));
        onView(withId(R.id.recyclerMemo))
                .check(matches(atPosition(1, hasDescendant(withText("30")))));
        onView(withId(R.id.recyclerMemo))
                .check(matches(atPosition(2, hasDescendant(withText("40")))));
    }

}