package com.example.jens.kitchenconverter;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
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
import static android.support.test.internal.util.Checks.checkNotNull;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;

public class UnitsActivityEspressoTest extends TestHelper {

    @Rule
    public ActivityTestRule<UnitsActivity> mActivityRule =
            new ActivityTestRule<>(UnitsActivity.class);

    @Before
    public void setUp() throws Exception { // revert database in order to have the same database for each test
        Context context = InstrumentationRegistry.getTargetContext();
        final DataBaseHelper myDbHelper = new DataBaseHelper(context,context.getFilesDir().getAbsolutePath());
        myDbHelper.revertDataBase();
    }

    @Test
    public void testToolbarTitle() {
        CharSequence title = InstrumentationRegistry.getTargetContext()
                .getString(R.string.units);
        matchToolbarTitle(title);
    }


    @Test
    public void testActionBarOverflowMenu() {
        CharSequence titleHome = InstrumentationRegistry.getTargetContext().getString(R.string.units);
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
    public void testAddUnit() { // adds: "additem/volume/321/ml"
        // check if item "addItem" does not exist yet
       onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withUnitName("addItem")))));

        // click on "add item" button
        onView(withId(99)).perform(click());

        // check whether add unit dialog appears
        onView(withText(R.string.addUnit)).inRoot(isDialog()).check(matches(isDisplayed())); //  dialog title
        onView(withText(R.string.add)).inRoot(isDialog()).check(matches(isDisplayed())); //  add button
        onView(withText(R.string.cancel)).inRoot(isDialog()).check(matches(isDisplayed())); // cancel button

        onView(withText("mass")).inRoot(isDialog()).check(matches(isDisplayed())); // mass radio button exists
        onView(withText("volume")).inRoot(isDialog()).check(matches(isDisplayed())); // volume radio button exists
        onView(withId(R.id.editTextUnit)).check(matches(isDisplayed())); //  unitname editText is  visible
        onView(withId(R.id.editTextFactor)).check(matches(isDisplayed())); // factor editText is  visible
        onView(withId(R.id.unit_spinner)).check(matches(isDisplayed())); //  unit spinner is  visible

        // fill and submit dialog
        onView(withText("volume")).inRoot(isDialog()).perform(click());
        closeSoftKeyboard();

        onView(withId(R.id.editTextUnit)).perform(replaceText("addItem"));
        closeSoftKeyboard();

        onView(withId(R.id.editTextFactor)).perform(replaceText("321"));
        closeSoftKeyboard();

        onView(withId(R.id.unit_spinner)).perform(click());
        onData(withUnitName("ml")).inRoot(isPlatformPopup()).perform(click());

        onView(withText(R.string.add)).inRoot(isDialog()).perform(click());

        // check whether item has been added


        onData(withUnitName("addItem"))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        onData(withUnitFactor(0.321d)) // match with 0.321 since 321 ml in base unit are 0.321 l
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

    }

    @Test
    public void testAddUnitSameAsExistingUnit() { // try to add: "g/mass/123/kg"; which should not be possible as unitName "g" exists
        // check if item "g" exists and element with factor=123 does not exist
        onView(withId(R.id.listView))
                .check(matches(withAdaptedData(withUnitName("g"))));

        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withUnitFactor(123d)))));

        // click on "add item" button
        onView(withId(99)).perform(click());

        // fill and submit dialog
        onView(withText("mass")).inRoot(isDialog()).perform(click());
        closeSoftKeyboard();

        onView(withId(R.id.editTextUnit)).perform(replaceText("g"));
        closeSoftKeyboard();

        onView(withId(R.id.editTextFactor)).perform(replaceText("10"));
        closeSoftKeyboard();

        onView(withId(R.id.unit_spinner)).perform(click());
        onData(withUnitName("kg")).inRoot(isPlatformPopup()).perform(click());

        onView(withText(R.string.add)).inRoot(isDialog()).perform(click());

        // check whether item has not been added
        onView(withId(R.id.listView))
                .check(matches(withAdaptedData(withUnitName("g"))));

        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withUnitFactor(123d)))));
    }

    @Test
    public void testAddUnitWithFactorZero() { // try to add: "newUnit/volume/0/ml"
        // check if item "newUnit" does not exist
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withUnitName("newUnit")))));


        // click on "add item" button
        onView(withId(99)).perform(click());

        // fill and submit dialog
        onView(withText("volume")).inRoot(isDialog()).perform(click());
        closeSoftKeyboard();

        onView(withId(R.id.editTextUnit)).perform(replaceText("newUnit"));
        closeSoftKeyboard();

        onView(withId(R.id.editTextFactor)).perform(replaceText("0"));
        closeSoftKeyboard();

        onView(withId(R.id.unit_spinner)).perform(click());
        onData(withUnitName("ml")).inRoot(isPlatformPopup()).perform(click());

        onView(withText(R.string.add)).inRoot(isDialog()).perform(click());

        // check whether item has not been added
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withUnitName("newUnit")))));

    }

    @Test
    public void testModifyUnit() { // change "g/mass/0.001/kg" to "dl/volume/456/ml"

        // check whether "dl" does not exist and whether "g" exists
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withUnitName("dl")))));

        onData(withUnitName("g"))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        // click on "g" which is to be modified
        onData(withUnitName("g"))
                .inAdapterView(withId(R.id.listView))
                .perform(click());

        // check whether modify unit dialog appears
        onView(withText(R.string.editUnit)).inRoot(isDialog()).check(matches(isDisplayed())); // check dialog title
        onView(withText(R.string.modify)).inRoot(isDialog()).check(matches(isDisplayed())); // check modify button
        onView(withText(R.string.delete)).inRoot(isDialog()).check(matches(isDisplayed())); // check delete button
        onView(withText(R.string.cancel)).inRoot(isDialog()).check(matches(isDisplayed())); // check cancel button

        // check saved values are properly displayed
        onView(allOf(withId(R.id.editTextUnit), withText("g"))).check(matches(isDisplayed())); // editText says "g"
        onView(allOf(withId(R.id.editTextFactor), withText("0.001"))).check(matches(isDisplayed())); // editText says "0.001"
        onView(withId(R.id.unit_spinner)).check(matches(withSpinnerText(containsString("kg")))); // base unit for mass


        // modify mass->volume; and checks
        onView(withText("volume")).perform(click());
        onView(allOf(withId(R.id.editTextUnit), withText("g"))).check(matches(isDisplayed())); // editTextUnit says "g"
        onView(allOf(withId(R.id.editTextFactor), withText("0.001"))).check(matches(isDisplayed())); // editTextFactor says "0.001"
        onView(withId(R.id.unit_spinner)).check(matches(withSpinnerText(containsString("l")))); // base unit should change from "kg" to "l"

        // modify unit name "g" -> "dl"; and factor "0.001" -> "0.1"
        onView(withId(R.id.editTextUnit)).perform(replaceText("dl"));
        closeSoftKeyboard();
        onView(withId(R.id.editTextFactor)).perform(replaceText("456"));

        closeSoftKeyboard();

        onView(withId(R.id.unit_spinner)).perform(click());
        onData(withUnitName("ml")).inRoot(isPlatformPopup()).perform(click());

        onView(withText(R.string.modify)).inRoot(isDialog()).perform(click()); // press modify button
        onView(withText(R.string.editUnit)).check(doesNotExist()); // dialog closed

        // check whether "dl"/"g" does/doesnt exist
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withUnitName("g")))));

        onData(withUnitName("dl"))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        onData(withUnitFactor(0.456d)) // match with 0.456 since 456 ml = 0.456 l (in base unit "l")
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testModifyBaseUnit() { // try to modify base unit (kg)
        /*
            only unit name should be allowed to change

            check that dimension cannot be changed for a base unit
            check that factor cannot be modified (equal to 1
            check that cannot be deleted
         */

        // check whether kg exists and kgnew not
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withUnitName("kgnew")))));

        onData(withUnitName("kg"))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));


        onData(withUnitName("kg"))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        // click on "kg" which is to be modified
        onData(withUnitName("kg"))
                .inAdapterView(withId(R.id.listView))
                .perform(click());

        // check whether modify unit dialog appears with correct properties
        onView(withText(R.string.editUnit)).inRoot(isDialog()).check(matches(isDisplayed())); // check dialog title
        onView(withText("volume")).inRoot(isDialog()).check(doesNotExist()); // check volume radio button does not exist
        onView(withId(R.id.editTextFactor)).check(matches(not(isDisplayed()))); // check factor is not visible
        onView(withId(R.id.unit_spinner)).check(matches(not(isDisplayed()))); // check spinner is not visible

        onView(withText(R.string.modify)).inRoot(isDialog()).check(matches(isDisplayed())); // check modify button
        onView(withText(R.string.delete)).inRoot(isDialog()).check(matches(not(isEnabled()))); // check delete button is disabled
        onView(withText(R.string.cancel)).inRoot(isDialog()).check(matches(isDisplayed())); // check cancel button

        // check saved values are properly displayed
        onView(allOf(withId(R.id.editTextUnit), withText("kg"))).check(matches(isDisplayed())); // editText says "kg"

        // modify unit nam "kg" -> "kgnew";
        onView(withId(R.id.editTextUnit)).perform(replaceText("kgnew"));

        onView(withText(R.string.modify)).inRoot(isDialog()).perform(click()); // press modify button
        onView(withText(R.string.editUnit)).check(doesNotExist()); // dialog closed

        // check whether "kg"/"kgnew" does/doesnt exist
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withUnitName("kg")))));

        onData(withUnitName("kgnew"))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testModifyToExistingUnit() { // modified unit name exists already
        // what to do if modified unit name exists already?
    }

    @Test
    public void testFilter() {

        // check whether there are "mass" and "volume" elements
        onData(withUnitDimension("mass"))
                .inAdapterView(withId(R.id.listView))
                .atPosition(0)
                .check(matches(isDisplayed()));

        onData(withUnitDimension("volume"))
                .inAdapterView(withId(R.id.listView))
                .atPosition(0)
                .check(matches(isDisplayed()));

        // 1. click on "filter" button and then select "mass"
        onView(withId(98)).perform(click());

        onView(withText("mass")).inRoot(isDialog()).perform(click());

        // check whether only mass elements exist
        onData(withUnitDimension("mass"))
                .inAdapterView(withId(R.id.listView))
                .atPosition(0)
                .check(matches(isDisplayed()));

        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withUnitDimension("volume")))));

        // 2. click on "filter" button and then select "volume"
        onView(withId(98)).perform(click());

        onView(withText("volume")).inRoot(isDialog()).perform(click());

        // check whether only volume elements exist
        onData(withUnitDimension("volume"))
                .inAdapterView(withId(R.id.listView))
                .atPosition(0)
                .check(matches(isDisplayed()));

        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withUnitDimension("mass")))));

        // 3. click on "filter" button and then select "All"
        onView(withId(98)).perform(click());

        onView(withText("All")).inRoot(isDialog()).perform(click());

        // check whether mass and volume elements exist
        onData(withUnitDimension("mass"))
                .inAdapterView(withId(R.id.listView))
                .atPosition(0)
                .check(matches(isDisplayed()));

        onData(withUnitDimension("volume"))
                .inAdapterView(withId(R.id.listView))
                .atPosition(0)
                .check(matches(isDisplayed()));

    }

    @Test
    public void testBaseUnit() {
        // check layout/background
        // change base unit
        /* onView(backgroundShouldHaveColor(Color.LTGRAY))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));
                */
        // onView(withText("l")).check(matches(backgroundShouldHaveColor(Color.LTGRAY)));
        //onView(withText("kg")).check(matches(withBgColor(Color.LTGRAY)));
/*        onView(withId(R.id.listView))
                .check(matches(withAdaptedData(backgroundShouldHaveColor(Color.LTGRAY))));
*/
        //onView(withChild(withText("kg"))).check(matches(withBackgroundColor(Color.LTGRAY)));
        onView(withChild(withText("l"))).check(matches(withBgColor(Color.LTGRAY)));
        /*
        onView(withText("l"))
                .check(matches(not(withAdaptedData(backgroundShouldHaveColor(Color.LTGRAY)))));
                 */
    }

    @Test
    public void testDataItemInAdapter() {
        onData(withUnitName("kg"))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testDataItemNotInAdapter() {
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withUnitName("kgl")))));
    }


}
