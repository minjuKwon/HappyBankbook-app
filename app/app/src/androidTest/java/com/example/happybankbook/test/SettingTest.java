package com.example.happybankbook.test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static com.example.happybankbook.helper.SettingTestHelper.checkExportIsSuccess;
import static com.example.happybankbook.helper.SettingTestHelper.checkTextEllipsize;
import static com.example.happybankbook.helper.SettingTestHelper.checkTextFont;
import static com.example.happybankbook.helper.SettingTestHelper.checkTextFontSetting;
import static com.example.happybankbook.helper.SettingTestHelper.checkTextLine;
import static com.example.happybankbook.helper.SettingTestHelper.checkTextLineSetting;
import static com.example.happybankbook.helper.SettingTestHelper.reset;
import static com.example.happybankbook.helper.TestHelper.BLACK;
import static com.example.happybankbook.helper.TestHelper.GRAY;
import static com.example.happybankbook.helper.TestHelper.saveMemo;
import static com.example.happybankbook.util.CustomerMatcher.isEllipsized;
import static com.example.happybankbook.util.CustomerMatcher.isNotEllipsized;
import static com.example.happybankbook.util.CustomerMatcher.withRadioButtonColor;
import static com.example.happybankbook.util.CustomerMatcher.withTextColor;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.happybankbook.MainActivity;
import com.example.happybankbook.db.RoomDB;
import com.example.happybankbook.helper.TestMemoData;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
public class SettingTest {

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Inject
    RoomDB db;

    private static final String keyword="twinkle";
    private static final int [] fontSize={12,15,18,21};
    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp(){
        scenario = ActivityScenario.launch(MainActivity.class);
        hiltRule.inject();
        db.clearAllTables();
        saveMemo(
                true,
                new TestMemoData(
                        "Twinkle, twinkle, little star,\n" +
                                "How I wonder what you are",
                        "1987654321")
        );
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.settingMenu)).perform(click());
    }

    @After
    public void closeResource(){
        if(scenario!=null) scenario.close();
        db.close();
    }

    @Test
    public void givenSettingScreen_whenClickedNothing_thenScreenIsInitialed(){
        //화면 텍스트 요소 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.manual))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioLineSingle))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioLineMul))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.ellipsis))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioFontSize))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioFontOne))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioFontTwo))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioFontThree))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.export))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.pdf))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.excel))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.txt))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.openSource))
                .check(matches(isDisplayed()));

        //radio 확인
        Espresso.onIdle();
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioLineSingle))
                .check(matches(withRadioButtonColor(GRAY)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioLineMul))
                .check(matches(isChecked()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioLineMul))
                .check(matches(withRadioButtonColor(BLACK)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioFontOne))
                .check(matches(isChecked()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioFontOne))
                .check(matches(withRadioButtonColor(BLACK)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioFontTwo))
                .check(matches(withRadioButtonColor(GRAY)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioFontThree))
                .check(matches(withRadioButtonColor(GRAY)));
    }

    @Test
    public void givenSettingScreen_whenClickedManual_thenCorrectDialogIsShown(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.manual)).perform(click());
        onView(ViewMatchers.withText(com.example.happybankbook.R.string.manualDialog))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withText(com.example.happybankbook.R.string.close)).perform(click());
        onView(ViewMatchers.withText(com.example.happybankbook.R.string.manual))
                .check(matches(isDisplayed()));
    }

    @Test
    public void givenSettingScreen_whenClickedOneTextLine_thenCorrectTextLineIsShown(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioLineSingle)).perform(click());

        checkTextLineSetting(BLACK, GRAY);

        checkTextLine(1,keyword);

        reset(com.example.happybankbook.R.id.radioLineMul);
    }

    @Test
    public void givenSettingScreen_whenClickedTwoTextLine_thenCorrectTextLineIsShown(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioLineMul)).perform(click());

        checkTextLineSetting(GRAY, BLACK);

        checkTextLine(2,keyword);
    }

    @Test
    public void givenSettingScreen_whenClickedEllipsize_thenCorrectEllipsizeIsShown(){
        //Ellipsize 설정
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.ellipsis)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.ellipsis)).perform(click());

        onView(ViewMatchers.withId(com.example.happybankbook.R.id.ellipsis))
                .check(matches(withTextColor(BLACK)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioLineSingle)).perform(click());

        checkTextEllipsize(isEllipsized(),keyword);

        reset(com.example.happybankbook.R.id.radioLineMul);
    }

    @Test
    public void givenSettingScreen_whenNotClickedEllipsize_thenCorrectEllipsizeIsShown(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.ellipsis)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.ellipsis))
                .check(matches(withTextColor(GRAY)));

        checkTextEllipsize(isNotEllipsized(),keyword);

        reset(com.example.happybankbook.R.id.ellipsis);
    }

    @Test
    public void givenSettingScreen_whenClickedOneFontSize_thenCorrectFontSizeIsShown(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioFontOne)).perform(click());

        checkTextFontSetting(BLACK, GRAY, GRAY);

        checkTextFont(fontSize[0], fontSize[1],keyword);
    }

    @Test
    public void givenSettingScreen_whenClickedTwoFontSize_thenCorrectFontSizeIsShown(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioFontTwo)).perform(click());

        checkTextFontSetting(GRAY, BLACK, GRAY);

        checkTextFont(fontSize[1], fontSize[2],keyword);

        reset(com.example.happybankbook.R.id.radioFontOne);
    }

    @Test
    public void givenSettingScreen_whenClickedThreeFontSize_thenCorrectFontSizeIsShown(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.radioFontThree)).perform(click());

        checkTextFontSetting(GRAY, GRAY, BLACK);

        checkTextFont(fontSize[2], fontSize[3],keyword);

        reset(com.example.happybankbook.R.id.radioFontOne);
    }

    @Test
    public void givenSettingScreen_whenClickedExportPdf_thenCorrectToastIsShown(){
        checkExportIsSuccess(com.example.happybankbook.R.id.pdf,"pdf");
    }

    @Test
    public void givenSettingScreen_whenClickedExportCsv_thenCorrectToastIsShown(){
        checkExportIsSuccess(com.example.happybankbook.R.id.excel,"excel");
    }

    @Test
    public void givenSettingScreen_whenClickedExportTxt_thenCorrectToastIsShown(){
        checkExportIsSuccess(com.example.happybankbook.R.id.txt,"txt");
    }

    @Test
    public void givenSettingScreen_whenClickedOpenSource_thenCorrectScreenIsShown(){
        Intents.init();
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.openSource)).perform(click());
        intended(hasComponent(OssLicensesMenuActivity.class.getName()));
        Intents.release();
    }

}