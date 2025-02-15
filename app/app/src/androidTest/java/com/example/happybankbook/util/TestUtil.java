package com.example.happybankbook.util;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.contrib.PickerActions.setDate;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;
import android.widget.DatePicker;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.happybankbook.R;

import org.hamcrest.Matcher;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestUtil {

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

    public static String getCurrentDate(){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy.MM.dd",java.util.Locale.getDefault());
        Date date=new Date();
        return dateFormat.format(date);
    }

    public static void clickUiButton(){
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        String [] textsToFind={"허용","저장"};

        // 해당 버튼 클릭
        for(String text:textsToFind){
            UiObject allowButton = device.findObject(new UiSelector().text(text));
            try {
                if (allowButton.exists() && allowButton.isEnabled()) {
                    allowButton.click();
                    break;
                }
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
