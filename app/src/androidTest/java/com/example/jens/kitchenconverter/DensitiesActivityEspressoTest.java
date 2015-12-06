package com.example.jens.kitchenconverter;

import android.content.Context;
import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

public class DensitiesActivityEspressoTest extends TestHelper {


    @Rule
    public ActivityTestRule<DensitiesActivity> mActivityRule =
            new ActivityTestRule<>(DensitiesActivity.class);

    @BeforeClass
    public static void onlyBefore() { // revert database in order to have the same database after running this test class
        Context context = InstrumentationRegistry.getTargetContext();
        final DataBaseHelper myDbHelper = new DataBaseHelper(context,context.getFilesDir().getAbsolutePath());
        myDbHelper.revertDataBase();
        myDbHelper.close();
    }


    @Before
    public void setUp() throws Exception { // revert database in order to have the same database for each test
        Context context = InstrumentationRegistry.getTargetContext();
        final DataBaseHelper myDbHelper = new DataBaseHelper(context,context.getFilesDir().getAbsolutePath());
        myDbHelper.revertDataBase();
        myDbHelper.close();
        closeSoftKeyboard();
    }

    @Test
    public void testToolbarTitle() {
        CharSequence title = InstrumentationRegistry.getTargetContext()
                .getString(R.string.substances);
        matchToolbarTitle(title);
    }


    @Test
    public void testActionBarOverflowMenu() {
        CharSequence titleHome = InstrumentationRegistry.getTargetContext().getString(R.string.substances);
        CharSequence titleNew = InstrumentationRegistry.getTargetContext().getString(R.string.action_settings);

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.action_settings)).perform(click());

        // check  (via title) that we are in Settings
        matchToolbarTitle(titleNew);

        // test back button in settings
        closeSoftKeyboard();
        onView(isRoot()).perform(pressBack());
        matchToolbarTitle(titleHome);
    }




    @Test
    public void testAddDensity() { // adds: "additem/volume/321/ml"
        // check if item "iron" does not exist yet
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withDensityName("iron")))));

        // click on "add item" button
        onView(withId(99)).perform(click());

        Context context = InstrumentationRegistry.getTargetContext();
        final DataBaseHelper myDbHelper = new DataBaseHelper(context,context.getFilesDir().getAbsolutePath());
        final String baseDensity = myDbHelper.getBaseDensity();
        myDbHelper.close();

        // check whether add density dialog appears
        onView(withText(R.string.addDensity)).inRoot(isDialog()).check(matches(isDisplayed())); //  dialog title
        onView(withText(baseDensity)).inRoot(isDialog()).check(matches(isDisplayed())); // base density
        onView(withText(R.string.add)).inRoot(isDialog()).check(matches(isDisplayed())); //  add button
        onView(withText(R.string.cancel)).inRoot(isDialog()).check(matches(isDisplayed())); // cancel button

        onView(withId(R.id.editTextSubstance)).check(matches(isDisplayed())); //  unitname editText is  visible
        onView(withId(R.id.editTextDensity)).check(matches(isDisplayed())); // factor editText is  visible


        onView(withId(R.id.editTextSubstance)).perform(replaceText("iron"));
        closeSoftKeyboard();

        onView(withId(R.id.editTextDensity)).perform(replaceText("8000"));
        closeSoftKeyboard();


        onView(withText(R.string.add)).inRoot(isDialog()).perform(click());

        // check whether item has been added


        onData(withDensityName("iron"))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        onData(withDensityDensity(8000d)) // match with 0.321 since 321 ml in base unit are 0.321 l
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

    }


    @Test
    public void testAddDensitySameAsExistingDensity() { // try to add: "water/123"; which should not be possible as substance "water" exists
        // check if item "water" exists and element with density=123 does not exist
        onView(withId(R.id.listView))
                .check(matches(withAdaptedData(withDensityName("water"))));

        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withDensityDensity(123d)))));

        // click on "add item" button
        onView(withId(99)).perform(click());

        // fill and submit dialog

        onView(withId(R.id.editTextSubstance)).perform(replaceText("water"));
        closeSoftKeyboard();

        onView(withId(R.id.editTextDensity)).perform(replaceText("123"));
        closeSoftKeyboard();

        onView(withText(R.string.add)).inRoot(isDialog()).perform(click());

        onView(withText(R.string.substance_exists)).inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed())); //check whether toast displays

        // check whether item has not been added
        onView(withId(R.id.listView))
                .check(matches(withAdaptedData(withDensityName("water"))));

        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withDensityDensity(123d)))));
    }

    @Test
    public void testAddDensityWithDensityZero() { // try to add: "newDensity/0"
        // check if item "newDensity" does not exist
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withDensityName("newDensity")))));


        // click on "add item" button
        onView(withId(99)).perform(click());

        // fill and submit dialog

        onView(withId(R.id.editTextSubstance)).perform(replaceText("newDensity"));
        closeSoftKeyboard();

        onView(withId(R.id.editTextDensity)).perform(replaceText("0"));
        closeSoftKeyboard();


        onView(withText(R.string.add)).inRoot(isDialog()).perform(click());

        // check whether item has not been added
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withDensityName("newDensity")))));

    }

    @Test
    public void testModifyDensity() { // change "water/1" to "wodka/456"

        // check whether "dl" does not exist and whether "g" exists
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withDensityName("wodka")))));

        onData(withDensityName("water"))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        // click on "water" which is to be modified
        onData(withDensityName("water"))
                .inAdapterView(withId(R.id.listView))
                .perform(click());

        // check whether modify unit dialog appears
        onView(withText(R.string.editDensity)).inRoot(isDialog()).check(matches(isDisplayed())); // check dialog title
        onView(withText(R.string.modify)).inRoot(isDialog()).check(matches(isDisplayed())); // check modify button
        onView(withText(R.string.delete)).inRoot(isDialog()).check(matches(isDisplayed())); // check delete button
        onView(withText(R.string.cancel)).inRoot(isDialog()).check(matches(isDisplayed())); // check cancel button

        // check saved values are properly displayed
        onView(allOf(withId(R.id.editTextSubstance), withText("water"))).check(matches(isDisplayed())); // editText says "water"
        onView(allOf(withId(R.id.editTextDensity), withText("1.0"))).check(matches(isDisplayed())); // editText says "1.0"

        // modify unit name "water" -> "wodka"; and factor "1.0" -> "456"
        onView(withId(R.id.editTextSubstance)).perform(replaceText("wodka"));
        closeSoftKeyboard();
        onView(withId(R.id.editTextDensity)).perform(replaceText("456"));

        closeSoftKeyboard();


        onView(withText(R.string.modify)).inRoot(isDialog()).perform(click()); // press modify button
        onView(withText(R.string.editDensity)).check(doesNotExist()); // dialog closed

        // check whether "wodka"/"water" does/doesnt exist
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withDensityName("water")))));

        onData(withDensityName("wodka"))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        onData(withDensityDensity(456.0d))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));
    }



    @Test
    public void testModifyToExistingDensity() { // change "water/1" to "flour/123"

        // check whether "water" and "flour"  exist
        onData(withDensityName("water"))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        onData(withDensityName("flour"))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));


        // click on "water" which is to be modified
        onData(withDensityName("water"))
                .inAdapterView(withId(R.id.listView))
                .perform(click());


        // modify unit name "water" -> "flour"; and factor "1.0" -> "123"
        onView(withId(R.id.editTextSubstance)).perform(replaceText("flour"));
        closeSoftKeyboard();
        onView(withId(R.id.editTextDensity)).perform(replaceText("123"));

        closeSoftKeyboard();


        onView(withText(R.string.modify)).inRoot(isDialog()).perform(click()); // press modify button
        onView(withText(R.string.editDensity)).check(doesNotExist()); // dialog closed

        // check whether "water" still exists and density = 456 does not exist
        onData(withDensityName("water"))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        onData(withDensityName("flour"))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withDensityDensity(456.0d)))));
    }

    @AfterClass
    public static void onlyAfter() { // revert database in order to have the same database after running this test class
        Context context = InstrumentationRegistry.getTargetContext();
        final DataBaseHelper myDbHelper = new DataBaseHelper(context,context.getFilesDir().getAbsolutePath());
        myDbHelper.revertDataBase();
        myDbHelper.close();
    }
}
