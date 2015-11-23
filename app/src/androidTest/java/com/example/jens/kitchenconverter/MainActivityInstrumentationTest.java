package com.example.jens.kitchenconverter;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.*;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MainActivityInstrumentationTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private MainActivity mainActivity;
    private Activity nextActivity;
    private Button generalConverterButton;
    private Button temperatureButton;

    public MainActivityInstrumentationTest() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);


        mainActivity = getActivity();

        final String[] dimensions = mainActivity.getResources().getStringArray(R.array.dimensions_array);
        generalConverterButton = (Button) mainActivity.findViewById(R.id.general_converter);
        temperatureButton = (Button) mainActivity.findViewById(dimensions.length);
    }

    @SmallTest
    public void testPreconditions() {
        assertNotNull("mainActivity is null", mainActivity);
        assertNotNull("generalConverterButton is null", generalConverterButton);
        assertNotNull("temperatureButton is null", temperatureButton);
    }

    @SmallTest
    public void testConverterButtons_layout() {
        View decorView = mainActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, generalConverterButton);
        ViewAsserts.assertOnScreen(decorView, temperatureButton);

        final ViewGroup.LayoutParams layoutParamsGeneralConverter = generalConverterButton.getLayoutParams();
        assertNotNull(layoutParamsGeneralConverter);
        assertEquals(layoutParamsGeneralConverter.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParamsGeneralConverter.height, WindowManager.LayoutParams.WRAP_CONTENT);

        final ViewGroup.LayoutParams layoutParamsTemperatureConverter = temperatureButton.getLayoutParams();
        assertNotNull(layoutParamsTemperatureConverter);
        assertEquals(layoutParamsTemperatureConverter.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParamsTemperatureConverter.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @SmallTest
    public void testConverterButtons_labelText() {
        final String expectedGeneral = mainActivity.getString(R.string.general);
        final String actualGeneral = generalConverterButton.getText().toString();

        final String expectedTemperature = mainActivity.getString(R.string.temperature);
        final String actualTemperature = temperatureButton.getText().toString();

        assertEquals(expectedGeneral, actualGeneral);
        assertEquals(expectedTemperature, actualTemperature);
    }

    @MediumTest
    public void testGeneralConverterButton_clickButton() {

        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(GeneralConverterActivity.class.getName(), null, false);
        TouchUtils.clickView(this, generalConverterButton);
        GeneralConverterActivity targetActivity = (GeneralConverterActivity) activityMonitor.waitForActivity();

        assertNotNull("Target activity is not launched", targetActivity);
    }

    @MediumTest
    public void testTemperatureConverterButton_clickButton() {

        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(ConverterActivity.class.getName(), null, false);
        TouchUtils.clickView(this, temperatureButton);
        ConverterActivity targetActivity = (ConverterActivity) activityMonitor.waitForActivity();

        assertNotNull("Target activity is not launched",targetActivity);
    }

}