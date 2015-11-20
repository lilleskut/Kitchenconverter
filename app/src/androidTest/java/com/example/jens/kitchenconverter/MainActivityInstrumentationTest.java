package com.example.jens.kitchenconverter;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.*;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;


import org.junit.Before;

public class MainActivityInstrumentationTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private MainActivity mainActivity;
    private Activity nextActivity;
    private Button generalConverterButton;
    private View temperatureButton;

    public MainActivityInstrumentationTest() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);

        mainActivity = getActivity();

        generalConverterButton = (Button) mainActivity.findViewById(R.id.general_converter);
    }

    public void testPreconditions() {
        assertNotNull("mainActivity is null", mainActivity);
        assertNotNull("generalConverterButton is null", generalConverterButton);
    }

    public void testGeneralConverterButton_layout() {
        View decorView = mainActivity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(decorView, generalConverterButton);

        final ViewGroup.LayoutParams layoutParams = generalConverterButton.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void testGeneralConverterButton_labelText() {
        final String expected = mainActivity.getString(R.string.general);
        final String actual = generalConverterButton.getText().toString();
        assertEquals(expected, actual);
    }

    @SmallTest
    public void testClickGeneralConverterButton() {

        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                generalConverterButton.performClick();
            }
        });

        // wait for the request to go through
        getInstrumentation().waitForIdleSync();


    }
}