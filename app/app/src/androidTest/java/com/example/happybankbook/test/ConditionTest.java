package com.example.happybankbook.test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.happybankbook.helper.ConditionTestHelper.checkInitialCondition;
import static com.example.happybankbook.helper.ConditionTestHelper.checkRecyclerViewItemPrice;
import static com.example.happybankbook.helper.ConditionTestHelper.checkRecyclerviewSize;
import static com.example.happybankbook.helper.ConditionTestHelper.checkTempConditionList;
import static com.example.happybankbook.helper.ConditionTestHelper.checkViewpagerItemBack;
import static com.example.happybankbook.helper.ConditionTestHelper.checkViewpagerItemCurrent;
import static com.example.happybankbook.helper.ConditionTestHelper.checkViewpagerItemForward;
import static com.example.happybankbook.helper.ConditionTestHelper.moveToFirstDetailScreen;
import static com.example.happybankbook.helper.ConditionTestHelper.resetCondition;
import static com.example.happybankbook.helper.ConditionTestHelper.setCountCondition;
import static com.example.happybankbook.helper.ConditionTestHelper.setDateCondition;
import static com.example.happybankbook.helper.ConditionTestHelper.setTempCondition;
import static com.example.happybankbook.helper.TestHelper.black;
import static com.example.happybankbook.helper.TestHelper.saveMemoWithDay;
import static com.example.happybankbook.util.CustomerMatcher.withTextColor;
import static com.example.happybankbook.util.TestUtil.getCurrentDate;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.happybankbook.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConditionTest {

    private static boolean isMemoSaved=false;
    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp(){
        scenario= ActivityScenario.launch(MainActivity.class);

        if(!isMemoSaved){
            saveMemoWithDay(true, "memo 1", "10", 3);
            saveMemoWithDay(true, "memo 2", "20", 5);
            saveMemoWithDay(true, "memo 3", "30", 8);
            saveMemoWithDay(true, "memo 4", "40", 9);
            saveMemoWithDay(true, "memo 5", "50", 9);
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

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonInit))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonSubmit))
                .check(matches(isDisplayed()));
    }

    @Test
    public void givenConditionScreen_whenClickedDurationText_thenCurrentDateButtonIsShown(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.duration)).perform(click());

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.duration))
                .check(matches(withTextColor(black)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.fromDuration))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.toDuration))
                .check(matches(isDisplayed()));

        String currentDate=getCurrentDate();
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.fromDuration))
                .check(matches(withText(currentDate)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.toDuration))
                .check(matches(withText(currentDate)));
        
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonInit)).perform(click());
    }

    @Test
    public void givenConditionScreen_whenInputCnt_thenCorrectFilteredListIsDisplayed(){
        //조건
        setCountCondition("3");

        //recyclerview 확인
        checkRecyclerviewSize(3);
        for(int i=0;i<3;i++){
            String str=String.valueOf(10*(5-i));
            checkRecyclerViewItemPrice(i, str);
        }

        //viewpager 확인
        moveToFirstDetailScreen();
        checkViewpagerItemBack("50");
        checkViewpagerItemBack("40");
        checkViewpagerItemCurrent("30");
        checkViewpagerItemForward("40");
        checkViewpagerItemForward("50");

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenInputDateAndCnt_thenCorrectFilteredListIsDisplayed(){
        //조건
        setDateCondition(3,8);

        setCountCondition("2");

        //recyclerview 확인
        checkRecyclerviewSize(2);
        checkRecyclerViewItemPrice(0, "30");
        checkRecyclerViewItemPrice(1, "20");

        //viewpager 확인
        moveToFirstDetailScreen();
        checkViewpagerItemBack("30");
        checkViewpagerItemCurrent("20");
        checkViewpagerItemForward("30");

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenSortedDesc_thenCorrectFilteredListIsDisplayed(){
        //조건
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioOldest)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonSubmit)).perform(click());

        //recyclerview 확인
        checkRecyclerviewSize(5);
        for(int i=0;i<5;i++){
            String str=String.valueOf(10*(i+1));
            checkRecyclerViewItemPrice(i, str);
        }

        //viewpager 확인
        moveToFirstDetailScreen();
        for(int i=1;i<5;i++){
            String str=String.valueOf(10*i);
            checkViewpagerItemBack(str);
        }
        checkViewpagerItemCurrent("50");
        for(int i=4;i>0;i--){
            String str=String.valueOf(10*i);
            checkViewpagerItemForward(str);
        }

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenSortedDescAndInputCnt_thenCorrectFilteredListIsDisplayed(){
        //조건
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioOldest)).perform(click());

        setCountCondition("2");

        //recyclerview 확인
        checkRecyclerviewSize(2);
        for(int i=0;i<2;i++){
            String str=String.valueOf(10*(i+1));
            checkRecyclerViewItemPrice(i, str);
        }

        //viewpager 확인
        moveToFirstDetailScreen();
        checkViewpagerItemBack("10");
        checkViewpagerItemCurrent("20");
        checkViewpagerItemForward("10");

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenSortedDescAndInputDateAndCnt_thenCorrectFilteredListIsDisplayed(){
        //조건
        setDateCondition(3,8);

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioOldest)).perform(click());
        setCountCondition("2");

        //recyclerview 확인
        checkRecyclerviewSize(2);
        for(int i=0;i<2;i++){
            String str=String.valueOf(10*(i+1));
            checkRecyclerViewItemPrice(i, str);
        }

        //viewpager 확인
        moveToFirstDetailScreen();
        checkViewpagerItemBack("10");
        checkViewpagerItemCurrent("20");
        checkViewpagerItemForward("10");

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
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonSubmit))
                .perform(click());

        checkTempConditionList();

        moveToFirstDetailScreen();
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPrevious))
                .perform(click());

        checkTempConditionList();

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.txtCondition)).perform(click());

        //조건 유지 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.duration))
                .check(matches(withTextColor(black)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.fromDuration))
                .check(matches(withText("2025.02.05")));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.toDuration))
                .check(matches(withText("2025.02.09")));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioOldest))
                .check(matches(isChecked()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.editCount))
                .check(matches(withText("3")));

        resetCondition();
    }

    @Test
    public void givenConditionScreenWithFilter_whenMovedOtherScreen_thenConditionIsInitialed(){
        setTempCondition();
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonSubmit)).perform(click());

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.addMenu)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.mainMenu)).perform(click());

        checkRecyclerviewSize(5);

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.txtCondition)).perform(click());

        checkInitialCondition();
    }

}