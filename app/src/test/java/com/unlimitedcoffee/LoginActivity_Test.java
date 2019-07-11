package com.unlimitedcoffee;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;


public class LoginActivity_Test {
    /**
     * Test of escapeBr method, of class HtmlEscape.
     */
    @Test
    public void testEscapeBr() {
        System.out.println("HtmlEscapeJUnit4Test: escapeBr()");
        String original = "\n \r";
        String expResult = "<br/> ";
        String result = Utilities.escapeBr(original);
        assertEquals(expResult, result);
    }
    @Test
    public void testEscapeBrNull() {
        System.out.println("HtmlEscapeJUnit4Test: escapeBr() - null input");
        String original = null;
        String expResult = "";
        String result = Utilities.escapeBr(original);
        assertEquals(expResult, result);
    }
    /**
     * Test of escapeTags method, of class HtmlEscape.
     */
    @Test
    public void testEscapeTags() {
        System.out.println("HtmlEscapeJUnit4Test: escapeTags()");
        String original = "<> \"";
        String expResult = "&lt;&gt; &quot;";
        String result = Utilities.escapeTags(original);
        assertEquals(expResult, result);
    }
    @Test
    public void testEscapeTagsNull() {
        System.out.println("HtmlEscapeJUnit4Test: escapeTags() - null input");
        String original = null;
        String expResult = "";
        String result = Utilities.escapeTags(original);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetTimeStr() {
        System.out.println("UtilitiesJUnit4Test: getTimeStr()");
        Date currentTime = Calendar.getInstance().getTime();
        String returnTime = Utilities.getTimeStr();
        String expectedTime = currentTime.toString();
        assertEquals(expectedTime, returnTime);
    }
}