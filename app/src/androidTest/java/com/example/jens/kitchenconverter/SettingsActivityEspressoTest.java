package com.example.jens.kitchenconverter;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.notNullValue;


@RunWith(AndroidJUnit4.class)
public class SettingsActivityEspressoTest extends TestHelper {

    @Rule
    public ActivityTestRule<SettingsActivity> mActivityRule =
            new ActivityTestRule<>(SettingsActivity.class);

    @Test
    public void testToolbarTitle() {
        CharSequence title = InstrumentationRegistry.getTargetContext().getString(R.string.action_settings);
        matchToolbarTitle(title);
    }

    @Test
    public void testEditUnitsLink() {

        CharSequence titleHome = InstrumentationRegistry.getTargetContext().getString(R.string.action_settings);
        CharSequence titleNew = InstrumentationRegistry.getTargetContext().getString(R.string.units);

        onView(withText(R.string.edit_units)).check(matches(notNullValue()));
        onView(withText(R.string.edit_units)).perform(click());

        // check  (via title) that we are in Units activity
        matchToolbarTitle(titleNew);

        // test back button in units activity
        closeSoftKeyboard();
        onView(isRoot()).perform(pressBack());
        matchToolbarTitle(titleHome);

        // test action bar back button in general converter
        onView(withText(R.string.edit_units)).perform(click());
        closeSoftKeyboard();
        onView(withContentDescription("Navigate up")).perform(click());
        matchToolbarTitle(titleHome);
    }

    @Test
    public void testEditSubstancesLink() {

        CharSequence titleHome = InstrumentationRegistry.getTargetContext().getString(R.string.action_settings);
        CharSequence titleNew = InstrumentationRegistry.getTargetContext().getString(R.string.substances);

        onView(withText(R.string.edit_substances)).check(matches(notNullValue()));
        onView(withText(R.string.edit_substances)).perform(click());

        // check  (via title) that we are in Units activity
        matchToolbarTitle(titleNew);

        // test back button in units activity
        closeSoftKeyboard();
        onView(isRoot()).perform(pressBack());
        matchToolbarTitle(titleHome);

        // test action bar back button in general converter
        onView(withText(R.string.edit_substances)).perform(click());
        closeSoftKeyboard();
        onView(withContentDescription("Navigate up")).perform(click());
        matchToolbarTitle(titleHome);
    }

    @Test
    public void testEditPackagesLink() {

        CharSequence titleHome = InstrumentationRegistry.getTargetContext().getString(R.string.action_settings);
        CharSequence titleNew = InstrumentationRegistry.getTargetContext().getString(R.string.packages);

        onView(withText(R.string.edit_packages)).check(matches(notNullValue()));
        onView(withText(R.string.edit_packages)).perform(click());

        // check  (via title) that we are in Units activity
        matchToolbarTitle(titleNew);

        // test back button in units activity
        closeSoftKeyboard();
        onView(isRoot()).perform(pressBack());
        matchToolbarTitle(titleHome);

        // test action bar back button in general converter
        onView(withText(R.string.edit_packages)).perform(click());
        closeSoftKeyboard();
        onView(withContentDescription("Navigate up")).perform(click());
        matchToolbarTitle(titleHome);
    }

    @Test
    public void testRevertDatabaseLink() {

        onView(withText(R.string.revert_database)).check(matches(notNullValue()));
        onView(withText(R.string.revert_database)).perform(click());

        // check  if dialog with buttons is displayed (check if dialog question is displayed
        onView(withText(R.string.revert_database_question)).check(matches(isDisplayed()));
        onView(withText(R.string.yes)).check(matches(isDisplayed()));
        onView(withText(R.string.no)).check(matches(isDisplayed()));

        // test dialog cancel/no button in units activity
        onView(withText(R.string.no)).perform(click());
        onView(withText(R.string.revert_database_question)).check(doesNotExist());

    }
}
