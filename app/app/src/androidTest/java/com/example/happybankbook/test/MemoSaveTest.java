package com.example.happybankbook.test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isFocused;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.happybankbook.util.CustomerMatcher.withToast;
import static com.example.happybankbook.util.TestUtil.getCurrentDate;
import static com.example.happybankbook.util.TestUtil.waitFor;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.happybankbook.MainActivity;
import com.example.happybankbook.R;
import com.example.happybankbook.util.TestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MemoSaveTest {

    private static final TestUtil util=new TestUtil();
    private ActivityScenario<MainActivity> scenario;
    private final String memo1="...?";
    private final String memo2=".,.,";
    private final String price1="100";
    private final String price2="200";

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(MainActivity.class);
        Intents.init();
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.addMenu)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.addMenu)).perform(click());
    }

    @After
    public void closeResource(){
        if(scenario!=null)
            scenario.close();
        Intents.release();
    }

    @Test
    public void givenAppIsLaunched_whenMemoFragmentIsDisplayed_thenCorrectInitialScreenIsShown(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.addPicture)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.save)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.txtMemoDate)).check(matches(withText(getCurrentDate())));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.editMemo)).check(matches(ViewMatchers.withHint((com.example.happybankbook.R.string.memo))));
    }

    @Test
    public void givenMemoFragment_whenClickSaveButton_thenCorrectDialogScreenIsShown(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.save)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.howHappy)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.editHappy)).check(matches(isFocused()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.ok)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.cancel)).check(matches(isDisplayed()));
    }

    @Test
    public void givenMemoFragment_whenClickCancelButtonInDialog_thenDialogScreenIsDismissed(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.save)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.howHappy)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.cancel)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.howHappy)).check(doesNotExist());
    }

    @Test
    public void givenMemoWithoutPrice_whenClickOkButtonInDialog_thenCorrectToastIsShown(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.editMemo)).perform(typeText("memo"));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.save)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.ok)).perform(click());

        onView(ViewMatchers.withText(com.example.happybankbook.R.string.memoPriceEmpty))
                .inRoot(withToast())
                .check(matches(isDisplayed()));
    }

    @Test
    public void givenMemoWithoutContent_whenClickOkButtonInDialog_thenCorrectToastIsShown(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.save)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.editHappy)).perform(typeText("123"));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.ok)).perform(click());

        onView(isRoot()).perform(waitFor(1000));
        onView(ViewMatchers.withText(com.example.happybankbook.R.string.memoContentEmpty))
                .inRoot(withToast())
                .check(matches(isDisplayed()));
    }

    @Test
    public void givenMemoWithoutPriceAndContent_whenClickOkButtonInDialog_thenCorrectToastIsShown(){
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.save)).perform(click());
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.ok)).perform(click());

        onView(ViewMatchers.withText(com.example.happybankbook.R.string.memoContentEmpty))
                .inRoot(withToast())
                .check(matches(isDisplayed()));
    }

    @Test
    public void givenMemoWithOverPrice_whenClickOkButtonInDialog_thenCorrectToastIsShown(){
        util.saveMemo(false, "memo","2147483648" );

        onView(ViewMatchers.withText(com.example.happybankbook.R.string.memoPriceOver))
                .inRoot(withToast())
                .check(matches(isDisplayed()));
    }

    @Test
    public void givenSaveMemoScreen_whenClickGalleryButton_thenCorrectImgIsShown(){
        //가짜 이미지 생성
        Intent resultData = new Intent();
        Uri mockImageUri =
                Uri.parse("android.resource://"
                        +ApplicationProvider.getApplicationContext()
                        .getPackageName() + "/" + com.example.happybankbook.R.drawable.test_img_png);
        resultData.setData(mockImageUri);
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(anyIntent()).respondWith(result);
        //갤러리 버튼 클릭
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.addPicture)).perform(click());
        //이미지 보이는 지 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.imageView))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void givenSaveMemoScreen_whenClickDateText_thenDateIsChanged(){
        //날짜 텍스트 클릭 후 날짜 변경
        util.selectDay(com.example.happybankbook.R.id.txtMemoDate,6);
        //해당 날짜 일치 여부 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.txtMemoDate))
                .check(matches(withText("2025.02.06")));
    }

    @Test
    public void givenMemoWithoutPicture_whenClickOkButtonInDialog_thenCorrectMemoIsSaved(){
        //메모 저장
        util.saveMemo(false, memo1,price1);
        //저장 후 화면 이동 확인, 저장한 메모 클릭.
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.mainMenu)).check(matches(ViewMatchers.isSelected()));
        onView(ViewMatchers.withText(com.example.happybankbook.R.string.condition)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).check(matches(hasMinimumChildCount(1)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        //저장된 메모 일치 여부 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailDate)).check(matches((withText(getCurrentDate()))));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailImg))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailContent)).check(matches((withText(memo1))));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPriceTxt)).check(matches((withText(price1))));
    }

    @Test
    public void givenMemoWithOtherDate_whenClickOkButtonInDialog_thenCorrectMemoIsSaved(){
        //메모 저장
        util.saveMemoWithDay(false, memo1, price1, 6);
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.addMenu)).perform(click());
        util.saveMemoWithDay(false, memo2, price2, 5);
        //저장 후 화면 이동 확인, 저장한 메모 클릭.
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.mainMenu)).check(matches(ViewMatchers.isSelected()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        //저장된 메모 일치 여부 확인
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.memoDetailPriceTxt)).check(matches((withText(price1))));
    }

    @Test
    public void givenMultipleMemo_whenClickOkButtonInDialog_thenCorrectMemoIsSaved(){
        //메모 저장
        util.saveMemo(false, memo1, price1);
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.addMenu)).perform(click());
        util.saveMemo(false, memo2, price2);
        //저장 후 화면 이동 확인, 저장한 메모 클릭.
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.mainMenu)).check(matches(ViewMatchers.isSelected()));
        onView(ViewMatchers.withId(com.example.happybankbook.R.id.recyclerMemo)).perform(actionOnItemAtPosition(0,click()));
        //저장된 메모 일치 여부 확인
        onView(ViewMatchers.withId(R.id.memoDetailPriceTxt)).check(matches((withText(price2))));
    }

}
