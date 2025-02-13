package com.example.happybankbook;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Root;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class CustomerMatcher {

    static Matcher<Root> withToast(){
        return new TypeSafeMatcher<Root>() {
            @Override
            protected boolean matchesSafely(Root item) {
                /*경우에 따라 Toast의 windowToken와 appToken이 같을 수도 다를 수도 있음
                 * 동일 테스트 결과를 위해 단일 조건 사용*/
                return !item.getDecorView().hasWindowFocus();
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("is a toast");
            }
        };
    }

    static Matcher<View> withTextColor(final int expectedColor){
        return new BoundedMatcher<View, TextView>(TextView.class){
            @Override
            protected boolean matchesSafely(TextView item) {
                return item.getCurrentTextColor()==expectedColor;
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("expected text color : "+expectedColor);
            }
        };
    }

    static Matcher<View> atPosition(final int position, final Matcher<View> itemMatcher){
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                RecyclerView recyclerview= (RecyclerView) item;
                RecyclerView.ViewHolder viewHolder=
                        recyclerview.findViewHolderForAdapterPosition(position);
                return viewHolder!=null&& itemMatcher.matches(viewHolder.itemView);
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("position is "+position);
            }
        };
    }

    static Matcher<View> withImageAlpha(final int expectedAlpha){
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            protected boolean matchesSafely(ImageView item) {
                return item.getImageAlpha()==expectedAlpha;
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("image alpha is "+expectedAlpha);
            }
        };
    }

}
