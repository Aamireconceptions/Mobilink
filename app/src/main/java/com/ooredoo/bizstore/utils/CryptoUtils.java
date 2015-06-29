package com.ooredoo.bizstore.utils;

import java.security.NoSuchAlgorithmException;

/**
 * Created by Babar on 29-Jun-15.
 */
public class CryptoUtils
{
    private static final String MD5_ALGO = "MD5";


    public static String encryptToMD5(String text)
    {
        try
        {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance(MD5_ALGO);

            byte[] array = md.digest(text.getBytes());

            StringBuffer sb = new StringBuffer();

            for(int i = 0; i < array.length; i++)
            {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }

            Logger.print("encryptToMD5: "+sb.toString());

            return sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return null;
    }

}
