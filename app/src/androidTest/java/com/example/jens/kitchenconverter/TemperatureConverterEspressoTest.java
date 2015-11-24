package com.example.jens.kitchenconverter;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


public class TemperatureConverterEspressoTest extends TestHelper {

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
}
