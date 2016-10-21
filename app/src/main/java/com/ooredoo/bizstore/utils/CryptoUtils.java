package com.ooredoo.bizstore.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;
import static com.ooredoo.bizstore.utils.StringUtils.isNullOrEmpty;

/**
 * @author Babar
 * @since 29-Jun-15.
 */
public class CryptoUtils
{
    private static final String MD5_ALGO = "MD5";


    // Hashing technique
    public static String encryptToMD5(String text)
    {
        try
        {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance(MD5_ALGO);
            md.update(text.getBytes());

            byte[] bytes = md.digest();

            String hex = bytesToHexString(bytes);

            return hex;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();

            return String.valueOf(text.hashCode());
        }
    }

    private static String bytesToHexString(byte[] bytes)
    {
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < bytes.length; i++)
        {
            String hex = Integer.toHexString(0xFF & bytes[i]);

            if(hex.length() == 1)
            {
                sb.append('0');
            }

            sb.append(hex);
        }

        return sb.toString();
    }

    // encoding technique
    public static String encodeToBase64(String str) {
        String tmp = "";
        if(!str.isEmpty()) {
            try {
                tmp = new String(Base64.encode(str.getBytes("UTF-8"), Base64.NO_WRAP));
            } catch(Throwable e) {
                e.printStackTrace();
            }
        }


        return tmp;
    }

    public static String decodeBase64(String str)
    {
        String tmp = "";
        if(!str.isEmpty())
        {
            try
            {
                tmp = new String(Base64.decode(str.getBytes(), Base64.DEFAULT)).trim();
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        }

        return tmp;
    }

    public static final String key = "2QcS*@HD#@bCdC5^g27S#@2D&^ABc0o!"; //// 256 bit key
    private static final String initVector = "kjwnefewg@ngsmd_"; // 16 bytes IV

    public static String encryptToAES(String value)
    {
        try
        {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec sKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));

            System.out.println("Encrypted String: " + Base64.encodeToString(encrypted, Base64.DEFAULT));

            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String decryptAES(String encryptedValue)
    {
        try
        {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec sKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decode(encryptedValue, Base64.DEFAULT));

            System.out.println("Decrypted String: " + new String(original));

            return new String(original);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return null;
    }

}