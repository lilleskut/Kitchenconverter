package com.example.jens.kitchenconverter;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class MyRationalTest {

    private MyRational r1;
    private MyRational r2;
    private MyRational r3;
    private MyRational r4;

    @Before
    public void init() {
        r1 = new MyRational(6,8);
        r2 = new MyRational(1,3);
        r3 = new MyRational(8,5);
        r4 = new MyRational(0,1);
    }

    @Test
    public void testEquals() {
        assertEquals(r1,r1);
        assertEquals(r1, new MyRational(3,4));
        assertEquals(r4, new MyRational(0d));
        assertTrue(!r2.equals(r1));
    }

    @Test
    public void testOperations() {
        assertEquals(r1.multiply(r2), new MyRational(1,4));
        assertEquals(r1.divide(r2), new MyRational(9, 4));
    }

    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    @Test
    public void testDivideByZero() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(equalTo("Divisor is 0"));
        r1.divide(r4);
    }


    @Test
    public void testConstructors() {
        assertEquals(r1,new MyRational(0.75d));
        assertEquals(r1,new MyRational("0.750000"));
        assertEquals(r3,new MyRational("1 3/5"));
    }

    @Test
    public void testSetRational() {
        r1.setRationalFromDouble(2.3d);
        assertEquals("rational should be 2.3", new MyRational(2.3d), r1);
        r2.setRationalFromString("2/6");
        assertEquals("rational should be 1/3", new MyRational(1,3), r2);
    }

    @Test
    public void testtoStringConversions() {
        assertEquals(r1.toDecimalsString(), "0.75");
        assertEquals(r2.toFractionString(),"1/3");
        assertEquals(r3.toDecimalsString(), "1.6");
        assertEquals(r3.toFractionString(), "1 3/5");
    }

    @Test
    public void testRegExpression() {
        assertTrue(MyRational.validFraction("2"));
        assertTrue(MyRational.validFraction("3.728341"));
        assertTrue(MyRational.validFraction("2/7"));
        assertTrue(MyRational.validFraction("9 3/8"));

        assertFalse(MyRational.validFraction("4."));
        assertFalse(MyRational.validFraction("-9"));
        assertFalse(MyRational.validFraction("1 / 2 / 9"));
        assertFalse(MyRational.validFraction("3 / 1.3"));
        assertFalse(MyRational.validFraction("4.2 / 3"));
        assertFalse(MyRational.validFraction("3a"));
    }
}
