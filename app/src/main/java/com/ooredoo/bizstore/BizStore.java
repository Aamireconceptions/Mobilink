package com.ooredoo.bizstore;

import android.app.Application;

import com.ooredoo.bizstore.model.User;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;

/**
 * @author pehlaj.rai
 * @since 6/10/2015.
 */

public class BizStore extends Application {

    private static final User user = new User();

    private static String language = "en";

    private final static String DEFAULT = "DEFAULT";
    private final static String MONOSPACE = "MONOSPACE";
    private final static String SERIF = "SERIF";
    private final static String SANS_SERIF = "SANS_SERIF";

    private final static String DEFAULT_FONT = "fonts/Futura/FuturaLT-Book.ttf";
    private final static String MONOSPACE_FONT = "fonts/Futura/FuturaLT.ttf";
    private final static String SERIF_FONT = "fonts/Opifico/Opificio_Bold.ttf";
    private final static String SANS_SERIF_FONT = "fonts/Opifico/Opificio.ttf";

    public void onCreate()
    {
        Logger.setEnabled(true);

        overrideDefaultFonts();

       /* setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread thread, Throwable e)
            {
                e.printStackTrace();
            }
        });*/
    }

    private void overrideDefaultFonts()
    {
        FontUtils.setDefaultFont(this, DEFAULT, DEFAULT_FONT);
        FontUtils.setDefaultFont(this, MONOSPACE, MONOSPACE_FONT);
        FontUtils.setDefaultFont(this, SERIF, SERIF_FONT);
        FontUtils.setDefaultFont(this, SANS_SERIF, SANS_SERIF_FONT);
    }

  /*  public static List<NameValuePair> getUserCredentials() {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("msisdn", user.username));
        params.add(new BasicNameValuePair("password", encryptVal(user.password)));
        return params;
    }*/

    public void setUserName(String userName) {
        user.username = userName;
    }

    public void setPassword(String password) {
        user.password = password;
    }

    public static User getUser() {
        return user;
    }

    public static String getLanguage() {
        return language;
    }

    public static void setLanguage(String language) {
        BizStore.language = language;
    }

}