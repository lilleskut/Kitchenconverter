package com.example.jens.kitchenconverter;


import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;

import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
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
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.internal.util.Checks.checkNotNull;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class UnitsActivityEspressoTest extends TestHelper {

    @Rule
    public ActivityTestRule<UnitsActivity> mActivityRule =
            new ActivityTestRule<>(UnitsActivity.class);

    @Before
    public void setUp() throws Exception { // revert database
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
    public void testAddUnit() {
        // check if item "addItem" does not exist yet
/*        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withUnitName("addItem")))));
*/
        // click on "add item" button
        // openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        // onData(allOf(instanceOf(MenuItem.class), withTitle("Add"))).perform(click());
        //onView(withContentDescription(R.string.add)).perform(click());
        // onView(withText(R.string.add)).perform(click());
        // onView(withId(99)).perform(click());


        // check whether add unit dialog appears
        /* onView(withText(R.string.addUnit)).inRoot(isDialog()).check(matches(isDisplayed())); // check dialog title
        onView(withText(R.string.add)).inRoot(isDialog()).check(matches(isDisplayed())); // check add button
        onView(withText(R.string.cancel)).inRoot(isDialog()).check(matches(isDisplayed())); // check cancel button
*/
    }

    @Test
    public void testModifyUnit() { // change "g/mass" to "dl/volume"

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
        onView(allOf(withId(R.id.editTextFactor), withText("0.001"))).check(matches(isDisplayed())); // editText says ""
        onView(withId(R.id.unit_spinner)).check(matches(withSpinnerText(containsString("kg")))); // base unit for mass


        // modify mass->volume; and checks
        onView(withText("volume")).perform(click());
        onView(allOf(withId(R.id.editTextUnit), withText("g"))).check(matches(isDisplayed())); // editText says "g"
        onView(allOf(withId(R.id.editTextFactor), withText("0.001"))).check(matches(isDisplayed())); // editText sa
        onView(withId(R.id.unit_spinner)).check(matches(withSpinnerText(containsString("l")))); // base unit should change from "kg" to "l"

        // modify unit nam "g" -> "dl"; and factor "0.001" -> "0.1"
        onView(withId(R.id.editTextUnit)).perform(replaceText("dl"));
        onView(withId(R.id.editTextFactor)).perform(replaceText("0.1"));

        onView(withText(R.string.modify)).inRoot(isDialog()).perform(click()); // press modify button
        onView(withText(R.string.editUnit)).check(doesNotExist()); // dialog closed

        // check whether "dl"/"g" does/doesnt exist
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withUnitName("g")))));

        onData(withUnitName("dl"))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));
    }


    @Test
    public void testModifyBaseUnit() { // try to modify base unit (kg)
        /*
            only unit name should be allowed to change

            check that dimension cannot be changed for a base unit
            check that cannot be deleted
            check that factor cannot be modified (equal to 1
         */

        onData(withUnitName("kg"))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));

        // click on "kg" which is to be modified
        onData(withUnitName("kg"))
                .inAdapterView(withId(R.id.listView))
                .perform(click());

        // check whether modify unit dialog appears
        onView(withText(R.string.editUnit)).inRoot(isDialog()).check(matches(isDisplayed())); // check dialog title
        onView(withText(R.string.modify)).inRoot(isDialog()).check(matches(isDisplayed())); // check modify button
        onView(withText("volume")).inRoot(isDialog()).check(doesNotExist()); // check volume radio button does not exist
        onView(withText(R.string.delete)).inRoot(isDialog()).check(doesNotExist()); // check delete button does not exist
        onView(withText(R.string.cancel)).inRoot(isDialog()).check(matches(isDisplayed())); // check cancel button


    }

    @Test
    public void testModifyToExistingUnit() { // modified unit name exists already
        // what to do if modified unit name exists already?
    }


    @Test
    public void testDataItenInAdapter() {
        onData(withUnitName("kg"))
                .inAdapterView(withId(R.id.listView))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testDataItemNotInAdapter() {
        onView(withId(R.id.listView))
                .check(matches(not(withAdaptedData(withUnitName("kgl")))));
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



    private static Matcher<View> withAdaptedData(final Matcher<Object> dataMatcher) {
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
