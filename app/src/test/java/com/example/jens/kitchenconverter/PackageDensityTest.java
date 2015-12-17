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
public class PackageDensityTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS) private Context context;
    private PackageDensity packageDensity;

    @Before
    public void init() throws Exception {
        System.out.println("Setting up ...");
        when(context.getResources().getStringArray(R.array.dimensions_array)).thenReturn(new String [] {"length", "mass", "volume"});
        packageDensity = new PackageDensity("water","bottle", 1.5d, context);
    }

    @After
    public void destroy() throws Exception {
        System.out.println("Tearing down ...");
        packageDensity = null;
    }

    @Test
    public void testInitialization() throws Exception {
        assertNotNull(packageDensity);

        assertEquals("packageDensity substance name is water", "water", packageDensity.getSubstance());
        assertEquals("packageDensity package type is bottle", "bottle", packageDensity.getPackageName());
        assertEquals("packageDensity density is 1.5", 1.5d, packageDensity.getPackageDensity(),0.0001);
    }

    @Test
    public void testToString() throws Exception {

        assertThat(packageDensity.toString(), containsString("water"));
        assertThat(packageDensity.toString(), containsString("bottle"));
        assertThat(packageDensity.toString(), containsString("1.5"));
    }

    @Test
    public void testSetId() throws Exception {
        packageDensity.setId(42);

        assertEquals("id should be 42",42,packageDensity.getId());
    }

    @Test
    public void testSetSubstance() throws Exception {
        packageDensity.setSubstance("sugar");

        assertEquals("packageDensity substance should be sugar","sugar",packageDensity.getSubstance());
    }

    @Test
    public void testSetPackageName() throws Exception {
        packageDensity.setPackageName("box");

        assertEquals("packageDensity package type should be box","box",packageDensity.getPackageName());
    }

    @Test
    public void testSetPackageDensity() throws Exception {
        packageDensity.setPackageDensity(1.0d);

        assertEquals("packageDensity density should be 1", 1.0d, packageDensity.getPackageDensity(), 0.0001);
    }


    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    @Test
    public void testSetSubstanceException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(equalTo("PackageDensity substance is null"));
        packageDensity.setSubstance(null);
    }
    @Test
    public void testSetPackageNameException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(equalTo("PackageDensity package type is null"));
        packageDensity.setPackageName(null);
    }

    @Test
    public void testSetPackageDensityException() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(equalTo("Package density is negative"));
        packageDensity.setPackageDensity(-1.0d);
    }



}
