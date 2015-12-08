package com.example.jens.kitchenconverter;

import android.content.Context;
import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
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

public class PackageDensitiesActivityEspressoTest extends TestHelper {

    @Rule
    public ActivityTestRule<PackageDensitiesActivity> mActivityRule =
            new ActivityTestRule<>(PackageDensitiesActivity.class);

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
                .getString(R.string.packages);
        matchToolbarTitle(title);
    }


    @Test
    public void testActionBarOverflowMenu() {
        CharSequence titleHome = InstrumentationRegistry.getTargetContext().getString(R.string.packages);
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
    public void testAddPackageDensity() { // adds: "water/box/9/l"
        // check if item "addItem" does not exist yet
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withPackageName("box")))));

        // click on "add item" button
        onView(withId(99)).perform(click());

        // check whether add unit dialog appears
        onView(withText(R.string.addPackageDensity)).inRoot(isDialog()).check(matches(isDisplayed())); //  dialog title
        onView(withText(R.string.add)).inRoot(isDialog()).check(matches(isDisplayed())); //  add button
        onView(withText(R.string.cancel)).inRoot(isDialog()).check(matches(isDisplayed())); // cancel button

        onView(withId(R.id.substance_spinner)).check(matches(isDisplayed())); //  substance spinner is  visible
        onView(withId(R.id.package_spinner)).check(matches(isDisplayed())); //  package spinner is  visible
        onView(withId(R.id.editTextPackageDensity)).check(matches(isDisplayed())); //  package density editText is  visible
        onView(withId(R.id.package_density_dimension)).check(matches(isDisplayed())); // dimension is displayed


        // fill and submit dialog
        onView(withId(R.id.substance_spinner)).perform(click());
        onData(withSubstance("water")).inRoot(isPlatformPopup()).perform(click());
        closeSoftKeyboard();

        onView(withId(R.id.package_spinner)).perform(click());
        onData(withPackage("box")).inRoot(isPlatformPopup()).perform(click());
        closeSoftKeyboard();

        onView(withId(R.id.editTextPackageDensity)).perform(replaceText("9"));
        closeSoftKeyboard();

        onView(withText(R.string.add)).inRoot(isDialog()).perform(click());

        // check whether item has been added

        onData(allOf(withSubstanceName("water"), withPackageName("box"), withPackageDensityDensity(9.0d)))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

    }


    @Test
    public void testAddPackageDensitySameAsExisting() { // try to add same combination of substance/packagetype: "sugar/package" which should overwrite the existing value
        // check if item exists

        onData(allOf(withSubstanceName("sugar"), withPackageName("package"), withPackageDensityDensity(1.0d)))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        // click on "add item" button
        onView(withId(99)).perform(click());

        // fill and submit dialog
        onView(withId(R.id.substance_spinner)).perform(click());
        onData(withSubstance("sugar")).inRoot(isPlatformPopup()).perform(click());
        closeSoftKeyboard();

        onView(withId(R.id.package_spinner)).perform(click());
        onData(withPackage("package")).inRoot(isPlatformPopup()).perform(click());
        closeSoftKeyboard();

        onView(withId(R.id.editTextPackageDensity)).perform(replaceText("123"));
        closeSoftKeyboard();

        onView(withText(R.string.add)).inRoot(isDialog()).perform(click());

        // check whether item has been added and old item does not exist anymore

        onData(allOf(withSubstanceName("sugar"), withPackageName("package"), withPackageDensityDensity(123.0d)))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(allOf(withSubstanceName("sugar"), withPackageName("package"), withPackageDensityDensity(1.0d))))));
    }


    @Test
    public void testAddPackageDensityWithDensityZero() { // try to add: "water/box/0/l"
        // check if item "addItem" does not exist yet
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withPackageName("box")))));

        // click on "add item" button
        onView(withId(99)).perform(click());

        // fill and submit dialog
        onView(withId(R.id.substance_spinner)).perform(click());
        onData(withSubstance("water")).inRoot(isPlatformPopup()).perform(click());
        closeSoftKeyboard();

        onView(withId(R.id.package_spinner)).perform(click());
        onData(withPackage("box")).inRoot(isPlatformPopup()).perform(click());
        closeSoftKeyboard();

        onView(withId(R.id.editTextPackageDensity)).perform(replaceText("0"));
        closeSoftKeyboard();

        onView(withText(R.string.add)).inRoot(isDialog()).perform(click());

        // check whether item has not been added
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withPackageName("box")))));

    }

    @Test
    public void testModifyPackageDensity() { // change "vanilla sugar/sachet/0.008" to "flour/box/456"

        // check if item exists

        onData(allOf(withSubstanceName("vanilla sugar"), withPackageName("sachet"), withPackageDensityDensity(0.008d)))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        // click on it
        onData(allOf(withSubstanceName("vanilla sugar"), withPackageName("sachet"), withPackageDensityDensity(0.008d)))
                .inAdapterView(withId(R.id.listView))
                .perform(click());

        // check whether modify packagedensity dialog appears
        onView(withText(R.string.editPackageDensity)).inRoot(isDialog()).check(matches(isDisplayed())); // check dialog title
        onView(withText(R.string.modify)).inRoot(isDialog()).check(matches(isDisplayed())); // check modify button
        onView(withText(R.string.delete)).inRoot(isDialog()).check(matches(isDisplayed())); // check delete button
        onView(withText(R.string.cancel)).inRoot(isDialog()).check(matches(isDisplayed())); // check cancel button

        // check saved values are properly displayed
        //onView(withId(R.id.substance_spinner)).check(matches(withSpinnerText(containsString("vanilla sugar"))));

        onView(withId(R.id.substance_spinner)).inRoot(isDialog()).check(matches(withSpinnerText(containsString("vanilla sugar"))));
        onView(withId(R.id.package_spinner)).inRoot(isDialog()).check(matches(withSpinnerText(containsString("sachet"))));
        onView(allOf(withId(R.id.editTextPackageDensity), withText("0.008"))).check(matches(isDisplayed())); // editText says "0.008"

        // modify
        onView(withId(R.id.substance_spinner)).perform(click());
        onData(withSubstance("flour")).inRoot(isPlatformPopup()).perform(click());
        closeSoftKeyboard();

        onView(withId(R.id.package_spinner)).perform(click());
        onData(withPackage("box")).inRoot(isPlatformPopup()).perform(click());
        closeSoftKeyboard();

        onView(withId(R.id.editTextPackageDensity)).perform(replaceText("456"));
        closeSoftKeyboard();

        onView(withText(R.string.modify)).inRoot(isDialog()).perform(click()); // press modify button
        onView(withText(R.string.editPackageDensity)).check(doesNotExist()); // dialog closed

        // check whether old/new item does not/does exist
        onData(allOf(withSubstanceName("flour"), withPackageName("box"), withPackageDensityDensity(456d)))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(allOf(withSubstanceName("vanilla sugar"), withPackageName("sachet"), withPackageDensityDensity(0.008d))))));
    }

    @Test
    public void testDeletePackageDensity() { // delete "baking powder/sachet/0.015"

        onData(allOf(withSubstanceName("baking powder"), withPackageName("sachet"), withPackageDensityDensity(0.015d)))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        // click on it
        onData(allOf(withSubstanceName("baking powder"), withPackageName("sachet"), withPackageDensityDensity(0.015d)))
                .inAdapterView(withId(R.id.listView))
                .perform(click());


        onView(withText(R.string.delete)).inRoot(isDialog()).perform(click()); // press delete button
        onView(withText(R.string.editPackageDensity)).check(doesNotExist()); // dialog closed

        // check whether old item does not exist

        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(allOf(withSubstanceName("baking powder"), withPackageName("sachet"), withPackageDensityDensity(0.015d))))));
    }



    @Test
    public void testModifyToExistingUnit() { // change "water/bottle/1.5" to "baking powder/sachet/123"

        // check if items exist

        onData(allOf(withSubstanceName("water"), withPackageName("bottle"), withPackageDensityDensity(1.5d)))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        onData(allOf(withSubstanceName("baking powder"), withPackageName("sachet"), withPackageDensityDensity(0.015d)))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        // click on it
        onData(allOf(withSubstanceName("water"), withPackageName("bottle"), withPackageDensityDensity(1.5d)))
                .inAdapterView(withId(R.id.listView))
                .perform(click());

        // modify
        onView(withId(R.id.substance_spinner)).perform(click());
        onData(withSubstance("baking powder")).inRoot(isPlatformPopup()).perform(click());
        closeSoftKeyboard();

        onView(withId(R.id.package_spinner)).perform(click());
        onData(withPackage("sachet")).inRoot(isPlatformPopup()).perform(click());
        closeSoftKeyboard();

        onView(withId(R.id.editTextPackageDensity)).perform(replaceText("123"));
        closeSoftKeyboard();

        onView(withText(R.string.modify)).inRoot(isDialog()).perform(click()); // press modify button
        onView(withText(R.string.editPackageDensity)).check(doesNotExist()); // dialog closed

        // check result of modification

        // new baking powder
        onData(allOf(withSubstanceName("baking powder"), withPackageName("sachet"), withPackageDensityDensity(123.0d)))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        // water has been modified and does not exist anymore
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(allOf(withSubstanceName("water"), withPackageName("bottle"), withPackageDensityDensity(1.5d))))));

        // old baking powder does not exist anymore as it is overwritten
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(allOf(withSubstanceName("baking powder"), withPackageName("sachet"), withPackageDensityDensity(0.015d))))));
    }


    @AfterClass
    public static void onlyAfter() { // revert database in order to have the same database after running this test class
        Context context = InstrumentationRegistry.getTargetContext();
        final DataBaseHelper myDbHelper = new DataBaseHelper(context,context.getFilesDir().getAbsolutePath());
        myDbHelper.revertDataBase();
        myDbHelper.close();
    }
}
