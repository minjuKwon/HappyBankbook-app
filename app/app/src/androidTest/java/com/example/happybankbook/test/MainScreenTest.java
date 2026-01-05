package com.example.happybankbook.test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.happybankbook.helper.TestHelper.saveMemo;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.happybankbook.view.MainActivity;
import com.example.happybankbook.R;
import com.example.happybankbook.db.RoomDB;
import com.example.happybankbook.helper.TestMemoData;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
public class MainScreenTest {

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Inject
    RoomDB db;

    private static final TestMemoData[] data= {
            new TestMemoData("a","12"),
            new TestMemoData("b","0"),
            new TestMemoData("c","6")
    };
    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp(){
        scenario=ActivityScenario.launch(MainActivity.class);
    }

    @After
    public void closeResources(){
        if(scenario!=null) scenario.close();
    }

    @Test
    public void givenAppIsLaunched_whenClickedNothing_thenListScreenIsShown(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.mainMenu))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.mainMenu))
                .check(matches(isSelected()));
    }

    @Test
    public void givenMainScreen_whenClickedNavigation_thenCorrectScreenIsShown(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.mainMenu))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.addMenu))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.settingMenu))
                .check(matches(isDisplayed()));

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.addMenu)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.addMenu))
                .check(matches(isSelected()));

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.settingMenu)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.settingMenu))
                .check(matches(isSelected()));

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.mainMenu)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.mainMenu))
                .check(matches(isSelected()));
    }

    @Test
    public void givenAddedMultipleMemo_whenMainScreenIsDisplayed_thenCorrectPriceIsDisplayed(){
        hiltRule.inject();

        int totalPrice=0;
        for(TestMemoData price:data){
            totalPrice+=Integer.parseInt(price.getPrice());
        }

        saveMemo(true,data[0]);
        saveMemo(true,data[1]);
        saveMemo(true,data[2]);

        onView(ViewMatchers.withId(R.id.priceTotalTxt))
                .check(matches(withText(String.valueOf(totalPrice))));

        db.close();
    }

}
