package com.example.jens.kitchenconverter;


import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.equalTo;
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
        unit = new Unit("dm","length", 0.1,false,context);
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
        assertEquals("unit factor is 0.1",0.1d,unit.getFactor(),0.0001);

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

    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    @Test
    public void testSetUnitException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(equalTo("Unit name is null"));
        unit.setUnit(null);
    }

    @Test
    public void testSetDimension() throws Exception {
        unit.setDimension("mass");

        assertEquals("unit dimension should be mass", "mass", unit.getDimension());
    }

    @Test
    public void testSetDimensionExceptionNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(equalTo("Dimension is null"));
        unit.setDimension(null);
    }

    @Test
    public void testSetDimensionExceptionArray() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(equalTo("Dimension is not one of the permittable dimension names"));
        unit.setDimension("foo");
    }

    @Test
    public void testSetFactor() throws Exception {
        unit.setFactor(0.001d);

        assertEquals("unit factor should be 0.0001f", 0.001f, unit.getFactor(), 0.0001);
    }

    @Test
    public void testSetFactorException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(equalTo("Factor is null"));
        unit.setFactor(null);
    }

    @Test
    public void testToString() throws Exception {

        assertThat(unit.toString(),containsString("dm"));
        assertThat(unit.toString(),containsString("length"));
        assertThat(unit.toString(),containsString("0.1"));
    }

}
