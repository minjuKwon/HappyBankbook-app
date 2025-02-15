package com.example.happybankbook.util;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Root;
import androidx.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class CustomerMatcher {

    public static Matcher<Root> withToast(){
        return new TypeSafeMatcher<Root>() {
            @Override
            protected boolean matchesSafely(Root item) {
                /*경우에 따라 Toast windowToken 와 appToken 이 같을 수도 다를 수도 있음
                 * 동일 테스트 결과를 위해 단일 조건 사용*/
                return !item.getDecorView().hasWindowFocus();
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("is a toast");
            }
        };
    }

    public static Matcher<View> withTextColor(final int expectedColor){
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

    public static Matcher<View> withRadioButtonColor(final int expectedColor){
        return new BoundedMatcher<View, RadioButton>(RadioButton.class){
            @Override
            protected boolean matchesSafely(RadioButton item) {
                return item.getCurrentTextColor()==expectedColor;
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("expected radio text color : "+expectedColor);
            }
        };
    }

    public static Matcher<View> withImageAlpha(final int expectedAlpha){
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

    public static Matcher<View> atPosition(final int position, final Matcher<View> itemMatcher){
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

    public static Matcher<View> isEllipsized(){
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            protected boolean matchesSafely(TextView item) {
                int lineCount = item.getLineCount();
                for (int i=0; i<lineCount; i++) {
                    if (item.getLayout().getEllipsisCount(i) > 0) {
                        return true;
                    }
                }
                return false;
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("check text ellipsized");
            }
        };
    }

    public static Matcher<View> isNotEllipsized(){
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            protected boolean matchesSafely(TextView item) {
                int lineCount = item.getLineCount();
                for (int i=0; i<lineCount; i++) {
                    if (item.getLayout().getEllipsisCount(i) > 0) {
                        return false;
                    }
                }
                return true;
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("check text ellipsized");
            }
        };
    }

    public static Matcher<View> hasAtMostLines(final int expectedLines){
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            protected boolean matchesSafely(TextView item) {
                return item.getLineCount()<=expectedLines;
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("text line is "+expectedLines);
            }
        };
    }

    public static Matcher<View> hasTextSizeSp(final Context context, final float expectedSize){
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            protected boolean matchesSafely(TextView item) {
                float expectedSizePx=
                        TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_SP,
                                expectedSize,
                                context.getResources().getDisplayMetrics()
                        );
                return item.getTextSize()==expectedSizePx;
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("text size is "+expectedSize);
            }
        } ;
    }

}
