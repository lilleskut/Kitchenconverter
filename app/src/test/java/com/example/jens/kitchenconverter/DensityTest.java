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
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DensityTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS) private Context context;
    private Density density;


    @Before
    public void init() throws Exception {
        System.out.println("Setting up ...");
        when(context.getResources().getStringArray(R.array.dimensions_array)).thenReturn(new String [] {"length", "mass", "volume"});
        density = new Density("flour",0.7, context);
    }

    @After
    public void destroy() throws Exception {
        System.out.println("Tearing down ...");
        density = null;
    }

    @Test
    public void testInitialization() throws Exception {
        assertNotNull(density);

        assertEquals("density substance name is flour", "flour", density.getSubstance());
        assertEquals("density density is 0.7", 0.7d, density.getDensity(),0.0001);
    }

    @Test
    public void testSetId() throws Exception {
        density.setId(42);

        assertEquals("id should be 42",42,density.getId());
    }

    @Test
    public void testSetSubstance() throws Exception {
        density.setSubstance("sugar");

        assertEquals("density substance should be sugar","sugar",density.getSubstance());
    }

    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    @Test
    public void testSetSubstanceException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(equalTo("Density substance is null"));
        density.setSubstance(null);
    }

    @Test
    public void testSetDensity() throws Exception {
        density.setDensity(1.2d);

        assertEquals("density density should be 1.2", 1.2d, density.getDensity(),0.00001);
    }

    @Test
    public void testSetDensityException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(equalTo("Density is negative"));
        density.setDensity(-1.0d);
    }

    @Test
    public void testToString() throws Exception {

        assertThat(density.toString(), containsString("flour"));
        assertThat(density.toString(), containsString("0.7"));
    }

}
