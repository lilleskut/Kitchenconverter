package com.example.jens.kitchenconverter;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SeekBar;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;

public class TemperatureConverterEspressoTest {

    @Rule
    public ActivityTestRule<ConverterActivity> mActivityRule =
            new ActivityTestRule<ConverterActivity>(ConverterActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, MainActivity.class);
                    result.putExtra("dimension", "temperature");
                    return result;
                }
            };


    @Test
    public void testToolbarTitle() {
        CharSequence title = InstrumentationRegistry.getTargetContext()
                .getString(R.string.temperature_converter);
        matchToolbarTitle(title);
    }

    @Test
    public void testInitialConversion() {
        onView(withId(R.id.celsius)).check(matches(withText("100 \u2103")));
        onView(withId(R.id.fahrenheit)).check(matches(withText("212 \u2109")));
        onView(withId(R.id.kelvin)).check(matches(withText("373 K")));
    }

    @Test
    public void testSeekBar() {

        onView(withId(R.id.seek_bar)).perform(setProgress(123));

        onView(withId(R.id.celsius)).check(matches(withText("-150 \u2103")));
        onView(withId(R.id.fahrenheit)).check(matches(withText("-238 \u2109")));
        onView(withId(R.id.kelvin)).check(matches(withText("123 K")));
    }


    @Test
    public void testActionBarOverflowMenu() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_settings)).perform(click());

        CharSequence title = InstrumentationRegistry.getTargetContext().getString(R.string.action_settings);
        matchToolbarTitle(title);
    }

    private static ViewAction setProgress(final int progress) {
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

    private static ViewInteraction matchToolbarTitle(
            CharSequence title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
    }

    private static Matcher<Object> withToolbarTitle(
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
}
