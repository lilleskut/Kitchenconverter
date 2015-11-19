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
    private MyRational r;

    @Before
    public void init() {
        r1 = new MyRational(6,8);
        r2 = new MyRational(1,3);
        r3 = new MyRational(8,5);
    }

    @Test
    public void testEquals() {
        assertEquals(r1,r1);
        assertEquals(r1, new MyRational(3,4));
        assertTrue(!r2.equals(r1));
    }

    @Test
    public void testOperations() {
        assertEquals(r1.multiply(r2), new MyRational(1,4));
        assertEquals(r1.divide(r2), new MyRational(9, 4));
    }

    @Test
    public void testtoStringConversions() {
        assertEquals(r1.toDecimalsString(),"0.75");
        assertEquals(r2.toFractionString(),"1/3");
        assertEquals(r3.toDecimalsString(),"1.6");
        assertEquals(r3.toFractionString(), "1 3/5");
    }

    @Test
    public void testFromConversions() {
        assertEquals(r1,new MyRational(0.75d));
        assertEquals(r1,new MyRational("0.750000"));
        assertEquals(r3,new MyRational("1 3/5"));
    }

    @Test
    public void testSetRational() {
        r1.setRationalFromDouble(2.3d);

        assertEquals("rational should be 2.3",new MyRational(2.3d),r1);
    }

}
