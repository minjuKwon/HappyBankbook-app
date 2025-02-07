package com.example.happybankbook;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.PickerActions.setDate;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isFocused;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.DatePicker;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class MemoSaveTest {

    private ActivityScenario<MainActivity> scenario;
    private final String memo1="...?";
    private final String memo2=".,.,";
    private final String price1="100";
    private final String price2="200";

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(MainActivity.class);
        Intents.init();
    }

    @Before
    public void moveToMemoFragment(){
        onView(withId(R.id.addMenu)).check(matches(isDisplayed()));
        onView(withId(R.id.addMenu)).perform(click());
    }

    @After
    public void closeActivityScenario(){
        if(scenario!=null)
            scenario.close();
        Intents.release();
    }

    @Test
    public void givenAppIsLaunched_whenMemoFragmentIsDisplayed_thenCorrectInitialScreenIsShown(){
        onView(withId(R.id.addPicture)).check(matches(isDisplayed()));
        onView(withId(R.id.save)).check(matches(isDisplayed()));
        onView(withId(R.id.txtMemoDate)).check(matches(withText(getCurrentDate())));
        onView(withId(R.id.editMemo)).check(matches(withHint((R.string.memo))));
    }

    @Test
    public void givenMemoFragment_whenClickSaveButton_thenCorrectDialogScreenIsShown(){
        onView(withId(R.id.save)).perform(click());
        onView(withId(R.id.howHappy)).check(matches(isDisplayed()));
        onView(withId(R.id.editHappy)).check(matches(isFocused()));
        onView(withId(R.id.ok)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel)).check(matches(isDisplayed()));
    }

    @Test
    public void givenMemoFragment_whenClickCancelButtonInDialog_thenDialogScreenIsDismissed(){
        onView(withId(R.id.save)).perform(click());
        onView(withId(R.id.howHappy)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel)).perform(click());
        onView(withId(R.id.howHappy)).check(doesNotExist());
    }

    @Test
    public void givenMemoWithoutPrice_whenClickOkButtonInDialog_thenCorrectToastIsShown(){
        ToastMatcher toast=new ToastMatcher();

        onView(withId(R.id.editMemo)).perform(typeText("memo"));
        onView(withId(R.id.save)).perform(click());
        onView(withId(R.id.ok)).perform(click());

        onView(withText(R.string.memoPriceEmpty))
                .inRoot(toast)
                .check(matches(isDisplayed()));
    }

    @Test
    public void givenMemoWithoutContent_whenClickOkButtonInDialog_thenCorrectToastIsShown(){
        ToastMatcher toast=new ToastMatcher();

        onView(withId(R.id.save)).perform(click());
        onView(withId(R.id.editHappy)).perform(typeText("123"));
        onView(withId(R.id.ok)).perform(click());

        onView(isRoot()).perform(waitFor(1000));
        onView(withText(R.string.memoContentEmpty))
                .inRoot(toast)
                .check(matches(isDisplayed()));
    }

    @Test
    public void givenMemoWithoutPriceAndContent_whenClickOkButtonInDialog_thenCorrectToastIsShown(){
        ToastMatcher toast=new ToastMatcher();

        onView(withId(R.id.save)).perform(click());
        onView(withId(R.id.ok)).perform(click());

        onView(withText(R.string.memoContentEmpty))
                .inRoot(toast)
                .check(matches(isDisplayed()));
    }

    @Test
    public void givenMemoWithOverPrice_whenClickOkButtonInDialog_thenCorrectToastIsShown(){
        ToastMatcher toast =new ToastMatcher();

        onView(withId(R.id.editMemo)).perform(typeText("memo"));
        onView(withId(R.id.save)).perform(click());
        onView(withId(R.id.editHappy)).perform(typeText("2147483648"));
        onView(withId(R.id.ok)).perform(click());

        onView(withText(R.string.memoPriceOver))
                .inRoot(toast)
                .check(matches(isDisplayed()));
    }

    @Test
    public void givenSaveMemoScreen_whenClickGalleryButton_thenCorrectImgIsShown(){
        //가짜 이미지 생성
        Intent resultData = new Intent();
        Uri mockImageUri =
                Uri.parse("android.resource://"
                        +ApplicationProvider.getApplicationContext()
                        .getPackageName() + "/" +R.drawable.test_img_png);
        resultData.setData(mockImageUri);
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(anyIntent()).respondWith(result);
        //갤러리 버튼 클릭
        onView(withId(R.id.addPicture)).perform(click());
        //이미지 보이는 지 확인
        onView(withId(R.id.imageView))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void givenSaveMemoScreen_whenClickDateText_thenDateIsChanged(){
        //날짜 텍스트 클릭 후 날짜 변경
        selectDay(6);
        //해당 날짜 일치 여부 확인
        onView(withId(R.id.txtMemoDate))
                .check(matches(withText("2025.02.06")));
    }

    @Test
    public void givenMemoWithoutPicture_whenClickOkButtonInDialog_thenCorrectMemoIsSaved(){
        //메모 저장
        saveMemo(memo1,price1);
        //저장 후 화면 이동 확인, 저장한 메모 클릭.
        onView(withId(R.id.mainMenu)).check(matches(ViewMatchers.isSelected()));
        onView(withText(R.string.condition)).check(matches(isDisplayed()));
        onView(withId(R.id.recyclerMemo)).check(matches(hasMinimumChildCount(1)));
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        //저장된 메모 일치 여부 확인
        onView(withId(R.id.memoDetailDate)).check(matches((withText(getCurrentDate()))));
        onView(withId(R.id.memoDetailImg))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.memoDetailContent)).check(matches((withText(memo1))));
        onView(withId(R.id.memoDetailPriceTxt)).check(matches((withText(price1))));
    }

    @Test
    public void givenMemoWithOtherDate_whenClickOkButtonInDialog_thenCorrectMemoIsSaved(){
        //메모 저장
        saveMemoWithDay(memo1, price1, 6);
        onView(withId(R.id.addMenu)).perform(click());
        saveMemoWithDay(memo2, price2, 5);
        //저장 후 화면 이동 확인, 저장한 메모 클릭.
        onView(withId(R.id.mainMenu)).check(matches(ViewMatchers.isSelected()));
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        //저장된 메모 일치 여부 확인
        onView(withId(R.id.memoDetailPriceTxt)).check(matches((withText(price1))));
    }

    @Test
    public void givenMultipleMemo_whenClickOkButtonInDialog_thenCorrectMemoIsSaved(){
        //메모 저장
        saveMemo(memo1, price1);
        onView(withId(R.id.addMenu)).perform(click());
        saveMemo(memo2, price2);
        //저장 후 화면 이동 확인, 저장한 메모 클릭.
        onView(withId(R.id.mainMenu)).check(matches(ViewMatchers.isSelected()));
        onView(withId(R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        //저장된 메모 일치 여부 확인
        onView(withId(R.id.memoDetailPriceTxt)).check(matches((withText(price2))));
    }

    private String getCurrentDate(){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy.MM.dd",java.util.Locale.getDefault());
        Date date=new Date();
        return dateFormat.format(date);
    }

    private void selectDay(int day){
        onView(withId(R.id.txtMemoDate)).perform(click());
        onView(withClassName(org.hamcrest.Matchers.equalTo(DatePicker.class.getName())))
                .perform(setDate(2025, 2, day));
        onView(withText("확인")).perform(click());
    }

    private void saveMemo(String memo, String price){
        onView(withId(R.id.editMemo)).perform(typeText(memo));
        onView(withId(R.id.save)).perform(click());
        onView(withId(R.id.editHappy)).perform(typeText(price));
        onView(withId(R.id.ok)).perform(click());
    }

    private void saveMemoWithDay(String memo, String price, int day){
        selectDay(day);
        saveMemo(memo, price);
    }

    // 커스텀 waitFor() 구현
    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

}
