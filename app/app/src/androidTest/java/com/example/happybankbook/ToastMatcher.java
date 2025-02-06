package com.example.happybankbook;

import android.os.IBinder;

import androidx.test.espresso.Root;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ToastMatcher extends TypeSafeMatcher<Root> {
    @Override
    public boolean matchesSafely(Root root) {
        // WindowToken 및 ApplicationToken 가져오기
        IBinder windowToken = root.getDecorView().getWindowToken();
        IBinder appToken = root.getDecorView().getApplicationWindowToken();

        // Root의 windowToken과 appToken이 동일하면 Toast
        return windowToken == appToken && !root.getDecorView().hasWindowFocus();
    }
    @Override
    public void describeTo(Description description) {
        description.appendText("is a toast");
    }
}
