package com.example.happybankbook;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.happybankbook.CustomerMatcher.hasAtMostLines;
import static com.example.happybankbook.CustomerMatcher.hasTextSizeSp;
import static com.example.happybankbook.CustomerMatcher.isEllipsized;
import static com.example.happybankbook.CustomerMatcher.isNotEllipsized;
import static com.example.happybankbook.CustomerMatcher.withRadioButtonColor;
import static com.example.happybankbook.CustomerMatcher.withTextColor;
import static com.example.happybankbook.CustomerMatcher.withToast;
import static com.example.happybankbook.TestUtil.clickUiButton;
import static com.example.happybankbook.TestUtil.waitFor;

import android.content.Context;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SettingTest {

    private static final TestUtil util=new TestUtil();
    private static final String keyword="twinkle";
    private static final int [] fontSize={12,15,18,21};
    private static final int black = ContextCompat
            .getColor(ApplicationProvider.getApplicationContext(), R.color.black);
    private static final int gray = ContextCompat
            .getColor(ApplicationProvider.getApplicationContext(), R.color.gray);
    private static boolean isMemoSaved=false;
    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp(){
        scenario = ActivityScenario.launch(MainActivity.class);
        if(!isMemoSaved){
            util.saveMemo(
                    true,
                    "Twinkle, twinkle, little star,\n" +
                            "How I wonder what you are",
                    "1987654321");
            isMemoSaved=true;
        }
        onView(withId(R.id.settingMenu)).perform(click());
    }

    @After
    public void closeScenario(){
        if(scenario!=null) scenario.close();
    }

    @Test
    public void givenSettingScreen_whenClickedNothing_thenScreenIsInitialed(){
        //화면 텍스트 요소 확인
        onView(withId(R.id.manual)).check(matches(isDisplayed()));
        onView(withId(R.id.radioLineSingle)).check(matches(isDisplayed()));
        onView(withId(R.id.radioLineMul)).check(matches(isDisplayed()));
        onView(withId(R.id.ellipsis)).check(matches(isDisplayed()));
        onView(withId(R.id.radioFontSize)).check(matches(isDisplayed()));
        onView(withId(R.id.radioFontOne)).check(matches(isDisplayed()));
        onView(withId(R.id.radioFontTwo)).check(matches(isDisplayed()));
        onView(withId(R.id.radioFontThree)).check(matches(isDisplayed()));
        onView(withId(R.id.export)).check(matches(isDisplayed()));
        onView(withId(R.id.pdf)).check(matches(isDisplayed()));
        onView(withId(R.id.excel)).check(matches(isDisplayed()));
        onView(withId(R.id.txt)).check(matches(isDisplayed()));
        onView(withId(R.id.openSource)).check(matches(isDisplayed()));

        //radio 확인
        Espresso.onIdle();
        onView(withId(R.id.radioLineSingle)).check(matches(withRadioButtonColor(gray)));
        onView(withId(R.id.radioLineMul)).check(matches(isChecked()));
        onView(withId(R.id.radioLineMul)).check(matches(withRadioButtonColor(black)));
        onView(withId(R.id.radioFontOne)).check(matches(isChecked()));
        onView(withId(R.id.radioFontOne)).check(matches(withRadioButtonColor(black)));
        onView(withId(R.id.radioFontTwo)).check(matches(withRadioButtonColor(gray)));
        onView(withId(R.id.radioFontThree)).check(matches(withRadioButtonColor(gray)));
    }

    @Test
    public void givenSettingScreen_whenClickedManual_thenCorrectDialogIsShown(){
        onView(withId(R.id.manual)).perform(click());
        onView(withText(R.string.manualDialog)).check(matches(isDisplayed()));
        onView(withText(R.string.close)).perform(click());
        onView(withText(R.string.manual)).check(matches(isDisplayed()));
    }

    @Test
    public void givenSettingScreen_whenClickedOneTextLine_thenCorrectTextLineIsShown(){
        onView(withId(R.id.radioLineSingle)).perform(click());

        checkTextLineSetting(black, gray);

        checkTextLine(1);

        reset(R.id.radioLineMul);
    }

    @Test
    public void givenSettingScreen_whenClickedTwoTextLine_thenCorrectTextLineIsShown(){
        onView(withId(R.id.radioLineMul)).perform(click());

        checkTextLineSetting(gray, black);

        checkTextLine(2);
    }

    @Test
    public void givenSettingScreen_whenClickedEllipsize_thenCorrectEllipsizeIsShown(){
        //Ellipsize 설정
        onView(withId(R.id.ellipsis)).perform(click());
        onView(withId(R.id.ellipsis)).perform(click());

        onView(withId(R.id.ellipsis)).check(matches(withTextColor(black)));
        onView(withId(R.id.radioLineSingle)).perform(click());

        checkTextEllipsize(isEllipsized());

        reset(R.id.radioLineMul);
    }

    @Test
    public void givenSettingScreen_whenNotClickedEllipsize_thenCorrectEllipsizeIsShown(){
        onView(withId(R.id.ellipsis)).perform(click());
        onView(withId(R.id.ellipsis)).check(matches(withTextColor(gray)));

        checkTextEllipsize(isNotEllipsized());

        reset(R.id.ellipsis);
    }

    @Test
    public void givenSettingScreen_whenClickedOneFontSize_thenCorrectFontSizeIsShown(){
        onView(withId(R.id.radioFontOne)).perform(click());

        checkTextFontSetting(black, gray, gray);

        checkTextFont(fontSize[0], fontSize[1]);
    }

    @Test
    public void givenSettingScreen_whenClickedTwoFontSize_thenCorrectFontSizeIsShown(){
        onView(withId(R.id.radioFontTwo)).perform(click());

        checkTextFontSetting(gray, black, gray);

        checkTextFont(fontSize[1], fontSize[2]);

        reset(R.id.radioFontOne);
    }

    @Test
    public void givenSettingScreen_whenClickedThreeFontSize_thenCorrectFontSizeIsShown(){
        onView(withId(R.id.radioFontThree)).perform(click());

        checkTextFontSetting(gray, gray, black);

        checkTextFont(fontSize[2], fontSize[3]);

        reset(R.id.radioFontOne);
    }

    @Test
    public void givenSettingScreen_whenClickedExportPdf_thenCorrectToastIsShown(){
        checkExportIsSuccess(R.id.pdf,"pdf");
    }

    @Test
    public void givenSettingScreen_whenClickedExportCsv_thenCorrectToastIsShown(){
        checkExportIsSuccess(R.id.excel,"excel");
    }

    @Test
    public void givenSettingScreen_whenClickedExportTxt_thenCorrectToastIsShown(){
        checkExportIsSuccess(R.id.txt,"txt");
    }

    @Test
    public void givenSettingScreen_whenClickedOpenSource_thenCorrectScreenIsShown(){
        Intents.init();
        onView(withId(R.id.openSource)).perform(click());
        intended(hasComponent(OssLicensesMenuActivity.class.getName()));
        Intents.release();
    }

    private void checkTextLineSetting(int lineSingleColor, int lineMulColor){
        onView(withId(R.id.radioLineSingle)).check(matches(withTextColor(lineSingleColor)));
        onView(withId(R.id.radioLineMul)).check(matches(withTextColor(lineMulColor)));
    }

    private void checkTextFontSetting(int fontOneColor, int fontTwoColor, int fontThreeColor){
        onView(withId(R.id.radioFontOne)).check(matches(withTextColor(fontOneColor)));
        onView(withId(R.id.radioFontTwo)).check(matches(withTextColor(fontTwoColor)));
        onView(withId(R.id.radioFontThree)).check(matches(withTextColor(fontThreeColor)));
    }

    private void checkTextLine(int line){
        checkMemoListTextStyle(hasAtMostLines(line));

        searchKeyword();

        checkSearchListTextStyle(hasAtMostLines(line));
    }

    private void checkTextEllipsize(Matcher<View> matcher){
        checkMemoListTextStyle(matcher);

        searchKeyword();

        checkSearchListTextStyle(matcher);
    }

    private void checkTextFont(int sizeSmaller, int sizeLarger){
        Context context=ApplicationProvider.getApplicationContext();

        //메모 추가 화면 검사
        onView(withId(R.id.addMenu)).perform(click());
        onView(withId(R.id.editMemo)).check(matches(hasTextSizeSp(context,sizeSmaller)));

        //메모 리스트 화면 검사
        checkMemoListTextStyle(hasTextSizeSp(context,sizeLarger));
        onView(withId(R.id.inputTxtDate)).check(matches(hasTextSizeSp(context,sizeLarger)));

        //메모 상세 화면 검사
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        onView(withId(R.id.memoDetailContent)).check(matches(hasTextSizeSp(context,sizeSmaller)));
        onView(withId(R.id.memoDetailPrevious)).perform(click());

        searchKeyword();

        //검색 화면 검사
        onView(withId(R.id.inputTxtDate)).check(matches(hasTextSizeSp(context,sizeLarger)));
        checkSearchListTextStyle(hasTextSizeSp(context,sizeLarger));
    }

    private void checkMemoListTextStyle(Matcher<View> matcher){
        onView(withId(R.id.mainMenu)).perform(click());
        onView(withId(R.id.inputTxtContent)).check(matches(matcher));
        onView(withId(R.id.inputTxtDeposit)).check(matches(matcher));
    }

    private void checkSearchListTextStyle(Matcher<View> matcher){
        onView(withId(R.id.inputTxtContent)).check(matches(matcher));
        onView(withId(R.id.inputTxtDeposit)).check(matches(matcher));
    }

    private void searchKeyword(){
        onView(withId(R.id.txtSearch)).perform(click());
        onView(withId(R.id.searchView)).perform(typeText(keyword));
    }

    private void reset(int id){
        onView(withId(R.id.settingMenu)).perform(click());
        onView(withId(id)).perform(click());
    }

    private void checkExportIsSuccess(int id,String type){
        onView(withId(id)).perform(click());

        String str=type+" "+ApplicationProvider.getApplicationContext().getString(R.string.doExport);
        onView(withText(str)).check(matches(isDisplayed()));
        onView(withText(R.string.OK)).perform(click());
        clickUiButton();

        onView(withText(R.string.savePermissionYes))
                .inRoot(withToast())
                .check(matches(isDisplayed()));

        onView(isRoot()).perform(waitFor(2000));

        onView(withText(R.string.completeSaving))
                .inRoot(withToast())
                .check(matches(isDisplayed()));
    }

}