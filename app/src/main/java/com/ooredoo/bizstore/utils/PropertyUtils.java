package com.ooredoo.bizstore.utils;

import android.app.Activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Pehlaj Rai
 * @since 4/17/2015.
 */
public class PropertyUtils {

    private static final String PROP_FILE = "app.properties";

    private static final String SECRET_KEY = "KEY";

    private static final String ENCRYPTED_BASE_URL = "ENCRYPTED_BASE_URL";

    public static String getValue(Activity activity, String fileName, String KEY) {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = activity.getAssets().open(fileName);

            prop.load(input);

            if(prop.containsKey(KEY)) {
                return prop.get(KEY).toString();
            }

        } catch(IOException ex) {
            ex.printStackTrace();
        } finally {
            if(input != null) {
                try {
                    input.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static StringBuffer getSecretKey(Activity activity) {
        return new StringBuffer(getValue(activity, PROP_FILE, SECRET_KEY));
    }

    public static String getAppUrl(Activity activity) {
        return getValue(activity, PROP_FILE, ENCRYPTED_BASE_URL);
    }

}