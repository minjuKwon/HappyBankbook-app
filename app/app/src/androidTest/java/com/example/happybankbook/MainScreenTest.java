package com.example.happybankbook;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MainScreenTest {

    private static final TestUtil util=new TestUtil();
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
        onView(withId(R.id.mainMenu)).check(matches(isDisplayed()));
        onView(withId(R.id.mainMenu)).check(matches(isSelected()));
    }

    @Test
    public void givenMainScreen_whenClickedNavigation_thenCorrectScreenIsShown(){
        onView(withId(R.id.mainMenu)).check(matches(isDisplayed()));
        onView(withId(R.id.addMenu)).check(matches(isDisplayed()));
        onView(withId(R.id.settingMenu)).check(matches(isDisplayed()));

        onView(withId(R.id.addMenu)).perform(click());
        onView(withId(R.id.addMenu)).check(matches(isSelected()));

        onView(withId(R.id.settingMenu)).perform(click());
        onView(withId(R.id.settingMenu)).check(matches(isSelected()));

        onView(withId(R.id.mainMenu)).perform(click());
        onView(withId(R.id.mainMenu)).check(matches(isSelected()));
    }

    @Test
    public void givenAddedMultipleMemo_whenMainScreenIsDisplayed_thenCorrectPriceIsDisplayed(){
        util.saveMemo(true,"a","12");
        util.saveMemo(true,"b","0");
        util.saveMemo(true,"c","6");

        onView(withId(R.id.priceTotalTxt)).check(matches(withText("18")));
    }

}
