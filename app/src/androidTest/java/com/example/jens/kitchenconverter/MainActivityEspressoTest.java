package com.example.jens.kitchenconverter;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;


@RunWith(AndroidJUnit4.class)
public class MainActivityEspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testToolbarTitle() {
        CharSequence title = InstrumentationRegistry.getTargetContext()
                .getString(R.string.app_name);
        matchToolbarTitle(title);
    }

    @Test
    public void testActionBarOverflowMenu() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_settings)).perform(click());

        CharSequence title = InstrumentationRegistry.getTargetContext().getString(R.string.action_settings);
        matchToolbarTitle(title);
    }

    @Test
    public void testGeneralConverterButton() {
        onView(withId(R.id.general_converter)).check(matches(notNullValue()));
        onView(withId(R.id.general_converter)).check(matches(withText(R.string.general)));
        onView(withId(R.id.general_converter)).perform(click());

        // in new activity
        CharSequence title = InstrumentationRegistry.getTargetContext()
                .getString(R.string.general_converter);
        matchToolbarTitle(title);

    }

    @Test
    public void testTemperatureConverterButton() {
        onView(withText(R.string.temperature)).check(matches(notNullValue()));
        onView(withText(R.string.temperature)).perform(click());

        // in new activity
        CharSequence title = InstrumentationRegistry.getTargetContext()
                .getString(R.string.temperature_converter);
        matchToolbarTitle(title);
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
