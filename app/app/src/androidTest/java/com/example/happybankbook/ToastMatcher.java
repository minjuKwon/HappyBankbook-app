package com.example.happybankbook;

import android.os.IBinder;

import androidx.test.espresso.Root;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ToastMatcher extends TypeSafeMatcher<Root> {
    @Override
    public boolean matchesSafely(Root root) {
        /*경우에 따라 Toast의 windowToken와 appToken이 같을 수도 다를 수도 있음
        * 동일 테스트 결과를 위해 단일 조건 사용*/
        return !root.getDecorView().hasWindowFocus();
    }
    @Override
    public void describeTo(Description description) {
        description.appendText("is a toast");
    }
}
