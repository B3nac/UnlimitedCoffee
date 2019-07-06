package com.unlimitedcoffee;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import android.util.Base64;

public class Hide_Test {

    @Test
    public void testGetKey() {
        System.out.println("Hide_JUnit4Test: getKey()");
        byte [] expected = Base64.decode("dDNzdDFuZzE=", Base64.DEFAULT);
        byte [] result = Hide.getKey();
        assertEquals(expected, result);
    }

    @Test
    public void testGetDatabaseName() {
        System.out.println("Hide_JUnit4Test: getKey()");
        byte [] expected = Base64.decode("VXNlck1hbmFnZW1lbnQuZGI=", Base64.DEFAULT);
        byte [] result = Hide.getDatabaseName();
        assertEquals(expected, result);
    }


}