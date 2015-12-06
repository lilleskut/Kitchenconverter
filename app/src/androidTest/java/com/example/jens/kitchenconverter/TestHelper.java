package com.example.jens.kitchenconverter;

import android.graphics.drawable.ColorDrawable;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.internal.util.Checks;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

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

    // custom matcher for unit dimension
    public static Matcher<Object> withUnitDimension(String expectedDimension) {
        checkNotNull(expectedDimension);
        return withUnitDimension(equalTo(expectedDimension));
    }

    public static Matcher<Object> withUnitDimension(final Matcher<String> itemDimensionMatcher) {
        checkNotNull(itemDimensionMatcher);
        return new BoundedMatcher<Object, Unit>(Unit.class) {
            @Override
            public boolean matchesSafely(Unit unit) {
                return itemDimensionMatcher.matches(unit.getDimension());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with Unit dimension: ");
                itemDimensionMatcher.describeTo(description);
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

    // custom matcher for density name (= substance)
    public static Matcher<Object> withDensityName(String expectedText) {
        checkNotNull(expectedText);
        return withDensityName(equalTo(expectedText));
    }

    public static Matcher<Object> withDensityName(final Matcher<String> itemTextMatcher) {
        checkNotNull(itemTextMatcher);
        return new BoundedMatcher<Object, Density>(Density.class) {
            @Override
            public boolean matchesSafely(Density density) {
                return itemTextMatcher.matches(density.getSubstance());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with Density name: ");
                itemTextMatcher.describeTo(description);
            }
        };
    }

    // custom matcher for density density (= substance)
    public static Matcher<Object> withDensityDensity(Double expectedDensity) {
        checkNotNull(expectedDensity);
        return withDensityDensity(equalTo(expectedDensity));
    }

    public static Matcher<Object> withDensityDensity(final Matcher<Double> itemDoubleMatcher) {
        checkNotNull(itemDoubleMatcher);
        return new BoundedMatcher<Object, Density>(Density.class) {
            @Override
            public boolean matchesSafely(Density density) {
                return itemDoubleMatcher.matches(density.getDensity());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with Density density: ");
                itemDoubleMatcher.describeTo(description);
            }
        };
    }

    public static Matcher<Object> backgroundShouldHaveColor(int expectedColor) {
        return viewShouldHaveBackgroundColor(equalTo(expectedColor));
    }
    private static Matcher<Object> viewShouldHaveBackgroundColor(final Matcher<Integer> expectedObject) {
        final int[] color = new int[1];
        return new BoundedMatcher<Object, View>(View.class) {
            @Override
            public boolean matchesSafely(View view) {

                //if(view.getBackground() instanceof ColorDrawable) {
                    color[0] = ((ColorDrawable) view.getBackground()).getColor();
                //}


                if( expectedObject.matches(color[0])) {
                    return true;
                } else {
                    return false;
                }
            }
            @Override
            public void describeTo(final Description description) {
                // Should be improved!
                description.appendText("Color did not match " + color[0]);
            }
        };
    }

    public static Matcher<View> withBgColor(final int color) {
        Checks.checkNotNull(color);
        return new BoundedMatcher<View, LinearLayout>(LinearLayout.class) {
            @Override
            public boolean matchesSafely(LinearLayout warning) {
                return color == ((ColorDrawable) warning.getBackground()).getColor();
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
            }
        };
    }

    public static Matcher<View> withBackgroundColor(final int color) {
        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("with class name: ");
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof AdapterView)) {
                    return false;
                }
                @SuppressWarnings("rawtypes")
                Adapter adapter = ((AdapterView) view).getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (color == ((ColorDrawable) view.getBackground()).getColor() ) {
                        return true;
                    }
                }
                return false;
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
