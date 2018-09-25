package com.example.dex.letsbake;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.dex.letsbake.ui.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static org.hamcrest.core.Is.is;


@RunWith(AndroidJUnit4.class)
public class RecipeListTest {

    private static final int RECIPE_ID = 0;
    private static final CharSequence RECIPE_NAME = "Nutella Pie";


    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private static Matcher<View> withItems() {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                return ((RecyclerView) view).getAdapter() != null
                        && ((RecyclerView) view).getAdapter().getItemCount() > 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView should not be null or empty");
            }
        };
    }

    private static Matcher<Object> withToolbarTitle(final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Toolbar where title ");
                textMatcher.describeTo(description);
            }
        };
    }

    @Test
    public void isRecyclerViewAvailableAndNotEmpty() {
        onView(ViewMatchers.withId(R.id.rv_recipes))
                .check(ViewAssertions.matches(withItems()));
    }

    @Test
    public void clickItem_OpensRecipeActivity() {
        onView(ViewMatchers.withId(R.id.rv_recipes))
                .perform(RecyclerViewActions.actionOnItemAtPosition(RECIPE_ID, click()));

        onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(RECIPE_NAME))));
    }
}