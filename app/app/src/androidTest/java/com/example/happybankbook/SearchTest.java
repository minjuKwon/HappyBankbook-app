package com.example.happybankbook;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;

import android.widget.SearchView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SearchTest {

    private static final TestUtil util=new TestUtil();
    private static boolean isMemoSaved=false;
    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp(){
        scenario= ActivityScenario.launch(MainActivity.class);

        if(!isMemoSaved){
            util.saveMemo(true,"memo 1", "10");
            util.saveMemo(true,"content 2", "20");
            util.saveMemo(true,"memo 3", "30");
            util.saveMemo(true, "content 4", "40");
            util.saveMemo(true,"memo 5", "50");
            isMemoSaved=true;
        }

        onView(withId(R.id.txtSearch)).perform(click());
    }

    @After
    public void closeScenario(){
        if(scenario!=null) scenario.close();
    }

    @Test
    public void givenSearchScreen_whenSearchedNothing_thenEmptyListIsDisplayed(){
        onView(withId(R.id.recyclerSearch)).check(matches(hasChildCount(0)));
    }

    @Test
    public void givenSearchScreen_whenSearchedBlank_thenEmptyListIsDisplayed(){
        searchKeyword("",0);
    }

    @Test
    public void givenSearchScreen_whenSearchedWord_thenFilteredListIsDisplayed(){
        searchKeyword("memo",3);
    }

    @Test
    public void givenSearchScreenWithList_whenGoToMainScreenAndComeBack_thenScreenIsInitialed(){
        searchKeyword("content",2);
        onView(withId(R.id.previousSearch)).perform(click());

        onView(withId(R.id.txtSearch)).check(matches(isDisplayed()));
        onView(withId(R.id.txtSearch)).perform(click());

        searchKeyword("",0);
    }

    @Test
    public void givenSearchScreenWithList_whenClickedListItem_thenCorrectItemDetailIsDisplayed(){
        String keyword="memo";
        searchKeyword(keyword,3);

        onView(withId(R.id.recyclerSearch)).perform(actionOnItemAtPosition(0,click()));
        onView(withId(R.id.memoDetailPriceTxt)).check(matches((withText("10"))));
        onView(withId(R.id.memoDetailPrevious)).perform(click());

        onView(withId(R.id.previousSearch)).check(matches(isDisplayed()));
        //searchView의 text를 찾지 못하여 assertEquals() 사용
        Espresso.onIdle();scenario.onActivity(activity -> {
            SearchView searchView = activity.findViewById(R.id.searchView);
            String query = searchView.getQuery().toString();
            assertEquals(keyword, query);
        });
        onView(withId(R.id.recyclerSearch)).check(matches(hasChildCount(3)));
    }

    private void searchKeyword(String keyword, int count){
        onView(withId(R.id.searchView)).perform(typeText(keyword));
        onView(withId(R.id.recyclerSearch)).check(matches(hasChildCount(count)));
    }

}