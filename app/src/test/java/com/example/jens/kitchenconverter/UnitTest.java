package com.example.jens.kitchenconverter;


import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.containsString;

@RunWith(MockitoJUnitRunner.class)
public class UnitTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS) private Context context;
    Unit unit;


    @Before
    public void init() throws Exception {
        System.out.println("Setting up ...");
        when(context.getResources().getStringArray(R.array.dimensions_array)).thenReturn(new String [] {"length", "mass", "volume"});
        unit = new Unit("dm","length", (float) 0.1,context);
    }

    @After
    public void destroy() throws Exception {
        System.out.println("Tearing down ...");
        unit = null;
    }

    @Test
    public void testInitialization() throws Exception {
        assertNotNull(unit);

        assertEquals("unit name is dm", "dm", unit.getUnit());
        assertEquals("unit dimension is length", "length", unit.getDimension());
        assertEquals("unit factor is 0.1",0.1f,unit.getFactor(),0.0001);

    }


    @Test
    public void testSetId() throws Exception {
        unit.setId(42);

        assertEquals("id should be 42",42,unit.getId());
    }

    @Test
    public void testSetUnit() throws Exception {
        unit.setUnit("g");

        assertEquals("unit name should be g","g",unit.getUnit());
    }

    @Test
    public void testSetDimension() throws Exception {
        unit.setDimension("mass");

        assertEquals("unit dimension should be mass", "mass", unit.getDimension());
    }

    @Test
    public void testSetFactor() throws Exception {
        unit.setFactor(0.001f);

        assertEquals("unit factor should be 0.0001f",0.001f,unit.getFactor(),0.0001);
    }

    @Test
    public void testToString() throws Exception {
        assertThat(unit.toString(),containsString("dm"));
        assertThat(unit.toString(),containsString("length"));
        assertThat(unit.toString(),containsString("0.1"));
    }

}
