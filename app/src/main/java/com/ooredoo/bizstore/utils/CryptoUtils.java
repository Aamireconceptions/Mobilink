package com.ooredoo.bizstore.utils;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;

import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

/**
 * @author Babar
 * @since 29-Jun-15.
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

    public static String encodeToBase64(String str) {
        String tmp = "";
        if(isNotNullOrEmpty(str)) {
            try {
                tmp = new String(Base64.encode(str.getBytes(), Base64.DEFAULT)).trim();
            } catch(Throwable e) {
                e.printStackTrace();
            }
        }
        return tmp;
    }
}
