package com.example.jens.kitchenconverter;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;

public class GeneralConverterActivityEspressoTest extends TestHelper {

    @Rule
    public ActivityTestRule<GeneralConverterActivity> mActivityRule =
            new ActivityTestRule<>(GeneralConverterActivity.class);
    @BeforeClass
    public static void onlyBefore() { // revert database in order to have the same database after running this test class
        Context context = InstrumentationRegistry.getTargetContext();
        final DataBaseHelper myDbHelper = new DataBaseHelper(context,context.getFilesDir().getAbsolutePath());
        myDbHelper.revertDataBase();
        myDbHelper.close();
    }


    @Test
    public void testToolbarTitle() {
        CharSequence title = InstrumentationRegistry.getTargetContext()
                .getString(R.string.general_converter);
        matchToolbarTitle(title);
    }

    @Test
    public void testUserInterface() { // check whether all elements are there, with proper values at onCreate
        closeSoftKeyboard();
        onView(withId(R.id.enter_value)).check(matches(allOf(is(instanceOf(EditText.class)), withText(""), withHint(R.string.enter_value), isDisplayed())));
        onView(withId(R.id.from_spinner)).check(matches(allOf(is(instanceOf(MySpinner.class)), isDisplayed())));
        onView(withId(R.id.to_spinner)).check(matches(allOf(is(instanceOf(MySpinner.class)), isDisplayed())));
        onView(withId(R.id.result_value)).check(matches(allOf(is(instanceOf(TextView.class)), withText(""), isDisplayed())));
        onView(withId(R.id.clear_button)).check(matches(allOf(is(instanceOf(Button.class)), withText(R.string.clear), isDisplayed())));
        onView(withId(R.id.toggle_button)).check(matches(allOf(is(instanceOf(ToggleButton.class)), withText(R.string.fractions), isDisplayed())));
        closeSoftKeyboard();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.density_spinner)).check(matches(allOf(is(instanceOf(MySpinner.class)), isDisplayed())));
    }

    @Test
    public void sameDimensionConversion() { // convert g -> kg and ml to teaspoons
        onView(withId(R.id.enter_value)).perform(typeText("1 1/3"));
        onView(withId(R.id.from_spinner)).perform(click());
        onData(withUnitName("g")).inRoot(isDialog()).perform(click());

        onView(withId(R.id.to_spinner)).perform(click());
        onData(withUnitName("kg")).inRoot(isDialog()).perform(click());

        onView(withId(R.id.result_value)).check(matches(withText("1/750")));

        onView(withId(R.id.toggle_button)).perform(click());
        onView(withId(R.id.result_value)).check(matches(withText("0.001")));

        onView(withId(R.id.toggle_button)).perform(click());
        onView(withId(R.id.result_value)).check(matches(withText("1/750")));

        onView(withId(R.id.clear_button)).perform(click());
        onView(withId(R.id.result_value)).check(matches(allOf(is(instanceOf(TextView.class)), withText(""), isDisplayed())));
    }

    @Test
    public void massVolumeConversion() { // convert 1.05 kg flour to 1500 ml
        onView(withId(R.id.enter_value)).perform(typeText("1.05"));
        ViewActions.closeSoftKeyboard();
        onView(withId(R.id.enter_value)).check(matches(withText("1.05")));

        onView(withId(R.id.from_spinner)).perform(click());
        onData(withUnitName("kg")).inRoot(isDialog()).perform(click());

        onView(withId(R.id.to_spinner)).perform(click());
        onData(withUnitName("ml")).inRoot(isDialog()).perform(click());

        closeSoftKeyboard();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.density_spinner)).perform(click());
        onData(withDensityName("flour")).inRoot(isDialog()).perform(click());

        onView(withId(R.id.result_value)).check(matches(withText("1500")));

        onView(withId(R.id.to_spinner)).perform(click());
        onData(withUnitName("l")).inRoot(isDialog()).perform(click());
        onView(withId(R.id.result_value)).check(matches(withText("1 1/2")));

        closeSoftKeyboard();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.toggle_button)).perform(click());
        onView(withId(R.id.result_value)).check(matches(withText("1.5")));

        onView(withId(R.id.toggle_button)).perform(click());
        onView(withId(R.id.result_value)).check(matches(withText("1 1/2")));

        onView(withId(R.id.clear_button)).perform(click());
        onView(withId(R.id.result_value)).check(matches(allOf(is(instanceOf(TextView.class)), withText(""), isDisplayed())));
    }
}
