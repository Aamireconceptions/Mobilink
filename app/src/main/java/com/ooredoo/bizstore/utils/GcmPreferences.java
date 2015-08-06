package com.ooredoo.bizstore.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Babar on 06-Aug-15.
 */
public class GcmPreferences
{
    private final static String SHARED_PREFERENCES_NAME = "Gcm_preferences";

    private SharedPreferences.Editor editor;

    private final static String GCM_TOKEN = "gcm_token";

    private SharedPreferences sharedPreferences;

    public GcmPreferences(Context context)
    {
        sharedPreferences = (SharedPreferences) context.getSharedPreferences(SHARED_PREFERENCES_NAME,
                                                                             Context.MODE_PRIVATE);

        editor = sharedPreferences.edit();
    }

    public void setDeviceGCMToken(String token)
    {
        editor.putString(GCM_TOKEN, token);
        editor.commit();
    }

    public String getDeviceGCMToken()
    {
        return sharedPreferences.getString(GCM_TOKEN, null);
    }

    public void saveUserGCMToken(String key, String value)
    {
        editor.putString(key, value);
        editor.commit();
    }

    public String getUserGCMToken(String key)
    {
        return sharedPreferences.getString(key, null);
    }


    public void onTokenRefreshed()
    {
        editor.clear().commit();
    }
}
