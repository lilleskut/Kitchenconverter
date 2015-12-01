package com.example.jens.kitchenconverter;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.SeekBar;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.internal.util.Checks.checkNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;

public class TestHelper {

    static ViewInteraction matchToolbarTitle(
            CharSequence title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
    }

    static Matcher<Object> withToolbarTitle(
            final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }
            @Override public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }

    static ViewAction setProgress(final int progress) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                ((SeekBar) view).setProgress(progress);
            }

            @Override
            public String getDescription() {
                return "Set a progress on a seek bar";
            }

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }
        };
    }

    // custom matcher for unit factor
    public static Matcher<Object> withUnitFactor(Double expectedFactor) {
        checkNotNull(expectedFactor);
        return withUnitFactor(equalTo(expectedFactor));
    }

    public static Matcher<Object> withUnitFactor(final Matcher<Double> itemFactorMatcher) {
        checkNotNull(itemFactorMatcher);
        return new BoundedMatcher<Object, Unit>(Unit.class) {
            @Override
            public boolean matchesSafely(Unit unit) {
                return itemFactorMatcher.matches(unit.getFactor());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with Unit factor: ");
                itemFactorMatcher.describeTo(description);
            }
        };
    }

    // custom matcher for unit name
    public static Matcher<Object> withUnitName(String expectedText) {
        checkNotNull(expectedText);
        return withUnitName(equalTo(expectedText));
    }

    public static Matcher<Object> withUnitName(final Matcher<String> itemTextMatcher) {
        checkNotNull(itemTextMatcher);
        return new BoundedMatcher<Object, Unit>(Unit.class) {
            @Override
            public boolean matchesSafely(Unit unit) {
                return itemTextMatcher.matches(unit.getName());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with Unit name: ");
                itemTextMatcher.describeTo(description);
            }
        };
    }



    public static Matcher<View> withAdaptedData(final Matcher<Object> dataMatcher) {
        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("with class name: ");
                dataMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof AdapterView)) {
                    return false;
                }
                @SuppressWarnings("rawtypes")
                Adapter adapter = ((AdapterView) view).getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (dataMatcher.matches(adapter.getItem(i))) {
                        return true;
                    }
                }
                return false;
            }
        };
    }


    public MenuItemTitleMatcher withTitle(String title) {
        return new MenuItemTitleMatcher(title);
    }

    class MenuItemTitleMatcher extends BaseMatcher<Object> {
        private final String title;
        public MenuItemTitleMatcher(String title) { this.title = title; }

        @Override public boolean matches(Object o) {
            if (o instanceof MenuItem) {
                return ((MenuItem) o).getTitle().equals(title);
            }
            return false;
        }
        @Override public void describeTo(Description description) { }
    }
}
