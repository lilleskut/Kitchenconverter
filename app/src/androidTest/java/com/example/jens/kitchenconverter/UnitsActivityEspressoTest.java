package com.example.jens.kitchenconverter;


import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;

public class UnitsActivityEspressoTest {

    @Rule
    public ActivityTestRule<UnitsActivity> mActivityRule =
            new ActivityTestRule<>(UnitsActivity.class);
}
