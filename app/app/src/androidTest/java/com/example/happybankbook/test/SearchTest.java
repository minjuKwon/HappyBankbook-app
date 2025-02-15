package com.example.happybankbook.test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.happybankbook.helper.TestHelper.saveMemo;
import static org.junit.Assert.assertEquals;

import android.widget.SearchView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.happybankbook.MainActivity;
import com.example.happybankbook.R;
import com.example.happybankbook.helper.TestMemoData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SearchTest {

    private static final TestMemoData[] data= {
            new TestMemoData("memo 1", "10"),
            new TestMemoData("content 2", "20"),
            new TestMemoData("memo 3", "30"),
            new TestMemoData("content 4", "40"),
            new TestMemoData("memo 5", "50"),
    };
    private static boolean isMemoSaved=false;
    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp(){
        scenario= ActivityScenario.launch(MainActivity.class);

        if(!isMemoSaved){
            saveMemo(true,data[0]);
            saveMemo(true,data[1]);
            saveMemo(true,data[2]);
            saveMemo(true,data[3]);
            saveMemo(true,data[4]);

            isMemoSaved=true;
        }

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.txtSearch)).perform(click());
    }

    @After
    public void closeScenario(){
        if(scenario!=null) scenario.close();
    }

    @Test
    public void givenSearchScreen_whenSearchedNothing_thenEmptyListIsDisplayed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerSearch))
                .check(matches(hasChildCount(0)));
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
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.previousSearch)).perform(click());

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.txtSearch))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.txtSearch)).perform(click());

        searchKeyword("",0);
    }

    @Test
    public void givenSearchScreenWithList_whenClickedListItem_thenCorrectItemDetailIsDisplayed(){
        String keyword="memo";
        searchKeyword(keyword,3);

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerSearch))
                .perform(actionOnItemAtPosition(0,click()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPriceTxt))
                .check(matches((withText(data[0].getPrice()))));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPrevious))
                .perform(click());

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.previousSearch))
                .check(matches(isDisplayed()));
        //searchView text 찾지 못하여 assertEquals() 사용
        scenario.onActivity(activity -> {
            SearchView searchView = activity.findViewById(com.example.happybankbook.R.id.searchView);
            String query = searchView.getQuery().toString();
            assertEquals(keyword, query);
        });
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerSearch))
                .check(matches(hasChildCount(3)));
    }

    private void searchKeyword(String keyword, int count){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.searchView))
                .perform(typeText(keyword));
        onView(ViewMatchers.withId(R.id.recyclerSearch)).check(matches(hasChildCount(count)));
    }

}