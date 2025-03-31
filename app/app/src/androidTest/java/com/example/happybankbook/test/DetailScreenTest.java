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

import com.example.happybankbook.view.MainActivity;
import com.example.happybankbook.db.RoomDB;
import com.example.happybankbook.helper.TestMemoData;

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
public class DetailScreenTest {

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Inject
    RoomDB db;

    private static final TestMemoData[] data= {
            new TestMemoData("memo 1", "10"),
            new TestMemoData("memo 2", "20"),
            new TestMemoData("memo 3", "30"),
            new TestMemoData("memo 4", "40"),
            new TestMemoData("memo 5", "50"),
    };
    private static final List<String> dataPriceList= Arrays.stream(data)
            .map(TestMemoData::getPrice)
            .collect(Collectors.toList());
    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp(){
        scenario=ActivityScenario.launch(MainActivity.class);
        hiltRule.inject();
        db.clearAllTables();

        saveMemo(true, data[0]);
        saveMemo(true, data[1]);
        saveMemo(true, data[2]);
        saveMemo(true, data[3]);
        saveMemo(true, data[4]);
    }

    @After
    public void closeResource(){
        if(scenario!=null) scenario.close();
        db.close();
    }

   @Test
    public void givenDetailScreen_whenClickedBackBtn_thenBtnIsNotShownAFewLater(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .perform(actionOnItemAtPosition(1,click()));

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack))
                .check(matches(withImageAlpha(255)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack))
                .check(matches(withImageAlpha(0)));
    }

   @Test
    public void givenDetailScreen_whenClickedForwardBtn_thenBtnIsNotShownAFewLater(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .perform(actionOnItemAtPosition(2,click()));

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack))
                .check(matches(withImageAlpha(255)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).perform(click());
        onView(isRoot()).perform(waitFor(3000));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward))
                .check(matches(withImageAlpha(0)));
    }

    @Test
    public void given0thItemDetailScreen_whenClickedBackBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .perform(actionOnItemAtPosition(0,click()));

        checkFirstScreen(dataPriceList.get(4));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).perform(click());

        for(int i=4;i>1;i--){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedBack(txt);
        }

        checkLastScreen(dataPriceList.get(0));

        for(int i=2;i<5;i++){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedForward(txt);
        }

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).perform(click());
        checkFirstScreen(dataPriceList.get(4));
    }

    @Test
    public void given1thItemDetailScreen_whenClickedBackBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .perform(actionOnItemAtPosition(1,click()));

        for(int i=4;i>1;i--){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedBack(txt);
        }

        checkLastScreen(dataPriceList.get(0));

        for(int i=2;i<5;i++){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedForward(txt);
            onView(isRoot()).perform(waitFor(300));
        }

    }

    @Test
    public void given1thItemDetailScreen_whenClickedForwardBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .perform(actionOnItemAtPosition(1,click()));

        checkMiddleScreen(dataPriceList.get(3));

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).perform(click());
        checkFirstScreen(dataPriceList.get(4));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).perform(click());

        checkMiddleScreen(dataPriceList.get(3));
    }

    @Test
    public void given2ndItemDetailScreen_whenClickedBackBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .perform(actionOnItemAtPosition(2,click()));

        for(int i=3;i>1;i--){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedBack(txt);
        }

        checkLastScreen(dataPriceList.get(0));

        for(int i=2;i<3;i++){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedForward(txt);
        }

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).perform(click());
        checkMiddleScreen(dataPriceList.get(2));
    }

    @Test
    public void given2ndItemDetailScreen_whenClickedForwardBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .perform(actionOnItemAtPosition(2,click()));

        checkMiddleScreen(dataPriceList.get(2));
        checkDetailScreenClickedForward(dataPriceList.get(3));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).perform(click());

        checkFirstScreen(dataPriceList.get(4));

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).perform(click());
        checkDetailScreenClickedBack(dataPriceList.get(3));
        checkMiddleScreen(dataPriceList.get(2));
    }

    @Test
    public void given3rdItemDetailScreen_whenClickedBackBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .perform(actionOnItemAtPosition(3,click()));

        checkDetailScreenClickedBack(dataPriceList.get(1));
        checkLastScreen(dataPriceList.get(0));
        checkDetailScreenClickedForward(dataPriceList.get(1));
    }

    @Test
    public void given3rdItemDetailScreen_whenClickedForwardBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .perform(actionOnItemAtPosition(3,click()));

        checkMiddleScreen(dataPriceList.get(1));

        for(int i=3;i<5;i++){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedForward(txt);
        }

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).perform(click());
        checkFirstScreen(dataPriceList.get(4));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).perform(click());

        for(int i=4;i>1;i--){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedBack(txt);
        }

    }

    @Test
    public void given4thItemDetailScreen_whenClickedForwardBtnAndComeBack_thenCorrectScreenIsDisplayed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .perform(actionOnItemAtPosition(4,click()));

        checkLastScreen(dataPriceList.get(0));

        for(int i=2;i<5;i++){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedForward(txt);
        }

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgForward)).perform(click());
        checkFirstScreen(dataPriceList.get(4));

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imgBack)).perform(click());
        for(int i=4;i>1;i--){
            String txt=String.valueOf(i*10);
            checkDetailScreenClickedBack(txt);
        }

        checkLastScreen(dataPriceList.get(0));
    }

}
