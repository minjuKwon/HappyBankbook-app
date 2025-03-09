package com.example.happybankbook.helper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.happybankbook.util.CustomerMatcher.hasAtMostLines;
import static com.example.happybankbook.util.CustomerMatcher.hasTextSizeSp;
import static com.example.happybankbook.util.CustomerMatcher.withTextColor;
import static com.example.happybankbook.util.CustomerMatcher.withToast;
import static com.example.happybankbook.util.TestUtil.clickUiButton;
import static com.example.happybankbook.util.TestUtil.waitFor;

import android.content.Context;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.happybankbook.R;

import org.hamcrest.Matcher;

public class SettingTestHelper {

    private SettingTestHelper(){}

    public static void checkTextLineSetting(int lineSingleColor, int lineMulColor){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioLineSingle))
                .check(matches(withTextColor(lineSingleColor)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioLineMul))
                .check(matches(withTextColor(lineMulColor)));
    }

    public static void checkTextFontSetting(int fontOneColor, int fontTwoColor, int fontThreeColor){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioFontOne))
                .check(matches(withTextColor(fontOneColor)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioFontTwo))
                .check(matches(withTextColor(fontTwoColor)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioFontThree))
                .check(matches(withTextColor(fontThreeColor)));
    }

    public static void checkTextLine(int line, String keyword){
        checkMemoListTextStyle(hasAtMostLines(line));

        searchKeyword(keyword);

        checkSearchListTextStyle(hasAtMostLines(line));
    }

    public static void checkTextEllipsize(Matcher<View> matcher, String keyword){
        checkMemoListTextStyle(matcher);

        searchKeyword(keyword);

        checkSearchListTextStyle(matcher);
    }

    public static void checkTextFont(int sizeSmaller, int sizeLarger, String keyword){
        Context context= ApplicationProvider.getApplicationContext();

        //메모 추가 화면 검사
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.addMenu)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.editMemo))
                .check(matches(hasTextSizeSp(context,sizeSmaller)));

        //메모 리스트 화면 검사
        checkMemoListTextStyle(hasTextSizeSp(context,sizeLarger));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.inputTxtDate))
                .check(matches(hasTextSizeSp(context,sizeLarger)));

        //메모 상세 화면 검사
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo))
                .perform(actionOnItemAtPosition(0,click()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailContent))
                .check(matches(hasTextSizeSp(context,sizeSmaller)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPrevious))
                .perform(click());

        searchKeyword(keyword);

        //검색 화면 검사
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.inputTxtDate))
                .check(matches(hasTextSizeSp(context,sizeLarger)));
        checkSearchListTextStyle(hasTextSizeSp(context,sizeLarger));
    }

    public static void checkMemoListTextStyle(Matcher<View> matcher){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.mainMenu)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.inputTxtContent))
                .check(matches(matcher));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.inputTxtDeposit))
                .check(matches(matcher));
    }

    public static void checkSearchListTextStyle(Matcher<View> matcher){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.inputTxtContent))
                .check(matches(matcher));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.inputTxtDeposit))
                .check(matches(matcher));
    }

    public static void searchKeyword(String keyword){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.txtSearch)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.searchView))
                .perform(typeText(keyword));
    }

    public static void reset(int id){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.settingMenu))
                .perform(click());
        onView(withId(id)).perform(click());
    }

    public static void checkExportIsSuccess(int id,String type){
        onView(withId(id)).perform(click());

        String str=type+" "+ApplicationProvider
                .getApplicationContext()
                .getString(com.example.happybankbook.R.string.doExport);
        onView(withText(str)).check(matches(isDisplayed()));
        onView(ViewMatchers.withText(com.example.happybankbook.R.string.OK)).perform(click());
        clickUiButton();

        onView(ViewMatchers.withText(com.example.happybankbook.R.string.savePermissionYes))
                .inRoot(withToast())
                .check(matches(isDisplayed()));

        onView(isRoot()).perform(waitFor(2000));

        onView(ViewMatchers.withText(R.string.completeSaving))
                .inRoot(withToast())
                .check(matches(isDisplayed()));
    }

}
