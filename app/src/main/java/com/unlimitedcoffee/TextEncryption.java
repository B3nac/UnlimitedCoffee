package com.unlimitedcoffee;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public abstract class TextEncryption {
    private static final byte[] KEY = Hide.getKey();

    public static String encrypt(String value) {
        try {
            System.out.println(KEY);
            DESKeySpec keySpec = new DESKeySpec(KEY);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            byte[] clearText = value.getBytes("UTF8");
            // Implement this in a thread safe way, or switch to AES.
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            String encrypedText = Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);
            Log.d("Oh snap!", "Encrypted: " + value + " -> " + encrypedText);
            return encrypedText;

        } catch (InvalidKeyException | UnsupportedEncodingException | InvalidKeySpecException | NoSuchAlgorithmException | BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String decrypt(String value) {
        if (isBase64(value)) {
            try {

                DESKeySpec keySpec = new DESKeySpec(KEY);
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
                SecretKey key = keyFactory.generateSecret(keySpec);

                byte[] encrypedPwdBytes = Base64.decode(value, Base64.DEFAULT);
                // Implement this in a thread safe way, or switch to AES.
                Cipher cipher = Cipher.getInstance("DES");
                cipher.init(Cipher.DECRYPT_MODE, key);
                byte[] decrypedValueBytes = (cipher.doFinal(encrypedPwdBytes));

                String decrypedText = new String(decrypedValueBytes);
                Log.d("Oh snap!", "Decrypted: " + value + " -> " + decrypedText);
                return decrypedText;

            } catch (InvalidKeyException | InvalidKeySpecException | NoSuchAlgorithmException | BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Not a string!");
        }
        return value;
    }

    public static boolean isBase64(String value) {
        if (TextUtils.isEmpty(value))
            return false;
        try {
            Base64.decode(value, Base64.DEFAULT);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}

