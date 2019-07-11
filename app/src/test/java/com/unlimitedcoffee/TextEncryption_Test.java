package com.unlimitedcoffee;
/* test class in development - em 7/7 */

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import static org.junit.Assert.assertEquals;


public class TextEncryption_Test {
    /**
     * Test of escapeBr method, of class HtmlEscape.
     */
    String testStr = "coffeetime";
    String result;
    SecretKey key;
    @Before
    public void executedBeforeAll() throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
        final byte[] KEY = Hide.getKey();
        DESKeySpec keySpec = new DESKeySpec(KEY);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        key = keyFactory.generateSecret(keySpec);
    }

    @Test
    public void testEncrypt() {
        System.out.println("TextEncryption_JUnit4Test: encrypt()");
        String input = testStr;
        String expected = "coffeetime";
        String result = TextEncryption.encrypt(input);
        System.out.println(result);
        assertEquals(expected, result);
    }

    @Test
    public void testDecrypt() {
        System.out.println("TextEncryption_JUnit4Test: decrypt()");
        String original = null;
        String expected = "";
        String output = TextEncryption.decrypt(result);
        assertEquals(expected, output);
    }
    /**
     * Test of escapeTags method, of class HtmlEscape.
     */
    @Ignore
    @Test
    public void testIsBase64() {
        System.out.println("\"TextEncryption_JUnit4Test: isBase64()");
        String original = "<> \"";
        String expResult = "&lt;&gt; &quot;";
        String result = Utilities.escapeTags(original);
        assertEquals(expResult, result);
    }
}