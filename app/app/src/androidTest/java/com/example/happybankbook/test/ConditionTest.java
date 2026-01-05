package com.example.happybankbook.test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.happybankbook.helper.ConditionTestHelper.checkInitialCondition;
import static com.example.happybankbook.helper.ConditionTestHelper.checkRecyclerViewItem;
import static com.example.happybankbook.helper.ConditionTestHelper.checkRecyclerviewSize;
import static com.example.happybankbook.helper.ConditionTestHelper.checkTempConditionList;
import static com.example.happybankbook.helper.ConditionTestHelper.checkViewpagerItemBack;
import static com.example.happybankbook.helper.ConditionTestHelper.checkViewpagerItemCurrent;
import static com.example.happybankbook.helper.ConditionTestHelper.checkViewpagerItemForward;
import static com.example.happybankbook.helper.ConditionTestHelper.resetCondition;
import static com.example.happybankbook.helper.ConditionTestHelper.setCountCondition;
import static com.example.happybankbook.helper.ConditionTestHelper.setDateCondition;
import static com.example.happybankbook.helper.ConditionTestHelper.setTempCondition;
import static com.example.happybankbook.helper.TestHelper.BLACK;
import static com.example.happybankbook.helper.TestHelper.MONTH;
import static com.example.happybankbook.helper.TestHelper.YEAR;
import static com.example.happybankbook.helper.TestHelper.dateFormat;
import static com.example.happybankbook.helper.TestHelper.saveMemoWithDay;
import static com.example.happybankbook.util.CustomerMatcher.withTextColor;
import static com.example.happybankbook.util.TestUtil.getCurrentDate;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.happybankbook.view.MainActivity;
import com.example.happybankbook.db.RoomDB;
import com.example.happybankbook.helper.TestMemoWithDayData;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
public class ConditionTest {

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Inject
    RoomDB db;

    private static final TestMemoWithDayData [] data= {
            new TestMemoWithDayData("memo 1", "10", 3),
            new TestMemoWithDayData("memo 2", "20", 5),
            new TestMemoWithDayData("memo 3", "30", 8),
            new TestMemoWithDayData("memo 4", "40", 9),
            new TestMemoWithDayData("memo 5", "50", 9),
            };
    private static final List<String> dataPriceList= Arrays.stream(data)
                                                            .map(TestMemoWithDayData::getPrice)
                                                            .collect(Collectors.toList());
    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp(){
        scenario= ActivityScenario.launch(MainActivity.class);
        hiltRule.inject();

        saveMemoWithDay(true, data[0]);
        saveMemoWithDay(true, data[1]);
        saveMemoWithDay(true, data[2]);
        saveMemoWithDay(true, data[3]);
        saveMemoWithDay(true, data[4]);

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.txtCondition)).perform(click());
    }

    @After
    public void closeResource(){
        if(scenario!=null) scenario.close();
        db.close();
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
                .check(matches(withTextColor(BLACK)));
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
        int count=3;
        setCountCondition(String.valueOf(count));

        //recyclerview 확인
        checkRecyclerviewSize(count);
        for(int i=0;i<count;i++){
            String str=String.valueOf(10*(5-i));
            checkRecyclerViewItem(i, str);
        }

        //viewpager 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .perform(actionOnItemAtPosition(0,click()));
        checkViewpagerItemBack(dataPriceList.get(4));
        checkViewpagerItemBack(dataPriceList.get(3));
        checkViewpagerItemCurrent(dataPriceList.get(2));
        checkViewpagerItemForward(dataPriceList.get(3));
        checkViewpagerItemForward(dataPriceList.get(4));

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenInputDateAndCnt_thenCorrectFilteredListIsDisplayed(){
        //조건
        setDateCondition(3,8);

        int count=2;
        setCountCondition(String.valueOf(count));

        //recyclerview 확인
        checkRecyclerviewSize(count);
        checkRecyclerViewItem(0, dataPriceList.get(2));
        checkRecyclerViewItem(1, dataPriceList.get(1));

        //viewpager 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .perform(actionOnItemAtPosition(0,click()));
        checkViewpagerItemBack(dataPriceList.get(2));
        checkViewpagerItemCurrent(dataPriceList.get(1));
        checkViewpagerItemForward(dataPriceList.get(2));

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenSortedDesc_thenCorrectFilteredListIsDisplayed(){
        //조건
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioOldest)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonSubmit)).perform(click());

        int len=data.length;

        //recyclerview 확인
        checkRecyclerviewSize(len);
        for(int i=0;i<len;i++){
            String str=String.valueOf(10*(i+1));
            checkRecyclerViewItem(i, str);
        }

        //viewpager 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .perform(actionOnItemAtPosition(0,click()));
        for(int i=1;i<len;i++){
            String str=String.valueOf(10*i);
            checkViewpagerItemBack(str);
        }
        checkViewpagerItemCurrent(dataPriceList.get(4));
        for(int i=len-1;i>0;i--){
            String str=String.valueOf(10*i);
            checkViewpagerItemForward(str);
        }

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenSortedDescAndInputCnt_thenCorrectFilteredListIsDisplayed(){
        //조건
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioOldest)).perform(click());

        int count=2;
        setCountCondition(String.valueOf(count));

        //recyclerview 확인
        checkRecyclerviewSize(2);
        for(int i=0;i<count;i++){
            String str=String.valueOf(10*(i+1));
            checkRecyclerViewItem(i, str);
        }

        //viewpager 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .perform(actionOnItemAtPosition(0,click()));
        checkViewpagerItemBack(dataPriceList.get(0));
        checkViewpagerItemCurrent(dataPriceList.get(1));
        checkViewpagerItemForward(dataPriceList.get(0));

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenSortedDescAndInputDateAndCnt_thenCorrectFilteredListIsDisplayed(){
        //조건
        setDateCondition(3,8);

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioOldest)).perform(click());
        int count=2;
        setCountCondition(String.valueOf(count));

        //recyclerview 확인
        checkRecyclerviewSize(count);
        for(int i=0;i<count;i++){
            String str=String.valueOf(10*(i+1));
            checkRecyclerViewItem(i, str);
        }

        //viewpager 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .perform(actionOnItemAtPosition(0,click()));
        checkViewpagerItemBack(dataPriceList.get(0));
        checkViewpagerItemCurrent(dataPriceList.get(1));
        checkViewpagerItemForward(dataPriceList.get(0));

        resetCondition();
    }

    @Test
    public void givenConditionScreen_whenClickedInitialBtn_thenScreenIsInitialed(){
        setTempCondition(5,9,3);

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonInit)).perform(click());

        checkInitialCondition();
    }

    @Test
    public void givenConditionScreenWithFilter_whenClickedListItem_thenConditionIsRetained(){
        int fromDay=5;
        int toDay=9;
        int count=3;
        setTempCondition(fromDay,toDay,count);
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonSubmit))
                .perform(click());

        checkTempConditionList(dataPriceList.get(1), dataPriceList.get(2), dataPriceList.get(3),count);

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .perform(actionOnItemAtPosition(0,click()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPrevious))
                .perform(click());

        checkTempConditionList(dataPriceList.get(1), dataPriceList.get(2), dataPriceList.get(3),count);

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.txtCondition)).perform(click());

        //조건 유지 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.duration))
                .check(matches(withTextColor(BLACK)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.fromDuration))
                .check(matches(withText(String.format(dateFormat,YEAR, MONTH, fromDay))));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.toDuration))
                .check(matches(withText(String.format(dateFormat,YEAR, MONTH, toDay))));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioOldest))
                .check(matches(isChecked()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.editCount))
                .check(matches(withText(String.valueOf(count))));

        resetCondition();
    }

    @Test
    public void givenConditionScreenWithFilter_whenMovedOtherScreen_thenConditionIsInitialed(){
        setTempCondition(5,9,3);
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.buttonSubmit)).perform(click());

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.addMenu)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.mainMenu)).perform(click());

        checkRecyclerviewSize(data.length);

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.txtCondition)).perform(click());

        checkInitialCondition();
    }

}