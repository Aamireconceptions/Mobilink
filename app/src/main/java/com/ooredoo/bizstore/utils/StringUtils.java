package com.ooredoo.bizstore.utils;

/**
 * @author pehlaj.rai
 * @since 6/18/2015.
 */
public class StringUtils {

    public static boolean isNotNullOrEmpty(String str) {
        return (str != null && str.trim().length() > 0) && !str.equalsIgnoreCase("null");
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

}
