package com.ooredoo.bizstore;

import com.activeandroid.ActiveAndroid;
import com.ooredoo.bizstore.model.User;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.SharedPrefUtils;

import java.util.HashMap;

import static com.ooredoo.bizstore.utils.CryptoUtils.encodeToBase64;

/**
 * @author pehlaj.rai
 * @since 6/10/2015.
 */

public class BizStore extends com.activeandroid.app.Application {

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

    public void onCreate() {

        Logger.setEnabled(true);

        ActiveAndroid.initialize(this);

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

    public static HashMap<String, String> getUserCredentials() {
        HashMap<String, String> params = new HashMap<>();
        params.put("msisdn", user.username);
        params.put("password", encodeToBase64(user.password));
        return params;
    }

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