package com.example.jens.kitchenconverter;

import android.support.test.InstrumentationRegistry;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.notNullValue;


@RunWith(AndroidJUnit4.class)
public class MainActivityEspressoTest extends TestHelper {

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
        CharSequence titleHome = InstrumentationRegistry.getTargetContext().getString(R.string.app_name);
        CharSequence titleNew = InstrumentationRegistry.getTargetContext().getString(R.string.action_settings);

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_settings)).perform(click());

        // check  (via title) that we are in Settings
        matchToolbarTitle(titleNew);

        // test back button in settings
        closeSoftKeyboard();
        onView(isRoot()).perform(pressBack());
        matchToolbarTitle(titleHome);

        // test action bar back button in settings
        onView(withId(R.id.general_converter)).perform(click());
        closeSoftKeyboard();
        onView(withContentDescription("Navigate up")).perform(click());
        matchToolbarTitle(titleHome);
    }

    @Test
    public void testGeneralConverterButton() {

        CharSequence titleHome = InstrumentationRegistry.getTargetContext().getString(R.string.app_name);
        CharSequence titleNew = InstrumentationRegistry.getTargetContext().getString(R.string.general_converter);

        onView(withId(R.id.general_converter)).check(matches(notNullValue()));
        onView(withId(R.id.general_converter)).check(matches(withText(R.string.general)));
        onView(withId(R.id.general_converter)).perform(click());

        // check  (via title) that we are in General Converter
        matchToolbarTitle(titleNew);

        // test back button in general converter
        closeSoftKeyboard();
        onView(isRoot()).perform(pressBack());
        matchToolbarTitle(titleHome);

        // test action bar back button in general converter
        onView(withId(R.id.general_converter)).perform(click());
        closeSoftKeyboard();
        onView(withContentDescription("Navigate up")).perform(click());
        matchToolbarTitle(titleHome);
    }

    @Test
    public void testTemperatureConverterButton() {
        CharSequence titleHome = InstrumentationRegistry.getTargetContext().getString(R.string.app_name);
        CharSequence titleNew = InstrumentationRegistry.getTargetContext().getString(R.string.temperature_converter);

        onView(withText(R.string.temperature)).check(matches(notNullValue()));
        onView(withText(R.string.temperature)).perform(click());

        // check  (via title) that we are in Temperature Converter
        matchToolbarTitle(titleNew);

        // test back button in temperature converter
        closeSoftKeyboard();
        onView(isRoot()).perform(pressBack());
        matchToolbarTitle(titleHome);

        // test action bar back button in temperature converter
        onView(withText(R.string.temperature)).perform(click());
        closeSoftKeyboard();
        onView(withContentDescription("Navigate up")).perform(click());
        matchToolbarTitle(titleHome);
    }
}
