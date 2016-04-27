package com.ooredoo.bizstore;

import com.activeandroid.ActiveAndroid;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.ooredoo.bizstore.model.User;
import com.ooredoo.bizstore.utils.Logger;

import java.util.HashMap;

import static com.ooredoo.bizstore.utils.CryptoUtils.encodeToBase64;
import static com.ooredoo.bizstore.utils.FontUtils.setDefaultFont;

/**
 * @author pehlaj.rai
 * @since 6/10/2015.
 */

public class BizStore extends com.activeandroid.app.Application {

    private static final User user = new User();

    public static boolean forceStopTasks = false;

    private static String language = "en";

    public static int lastTab = 0;
    //public static String username = "123445";
     // ooredoo num "50356956"
    public static String username = BuildConfig.FLAVOR.equals("ooredoo") ? "33165670"
            : BuildConfig.FLAVOR.equals("telenor") ? "3441182798"
            : "03055571369";

    //public static String password = "d5pF55dZ";

    public static String password = BuildConfig.FLAVOR.equals("ooredoo") ? "751108"
            : BuildConfig.FLAVOR.equals("telenor") ? "DgHcXN"
            : "679891";

    private Tracker tracker;

    private final static String DEFAULT = "DEFAULT";
    private final static String MONOSPACE = "MONOSPACE";
    private final static String SERIF = "SERIF";
    private final static String SANS_SERIF = "SANS_SERIF";

    public static String DEFAULT_FONT = BuildConfig.FLAVOR.equals("ooredoo")
    ? "fonts/Futura/FuturaLT-Book.ttf" : BuildConfig.FLAVOR.equals("telenor")
            ? "fonts/Telenor.otf" : "fonts/OpenSans-Regular.ttf";
    public static String MONOSPACE_FONT = BuildConfig.FLAVOR.equals("ooredoo")
    ? "fonts/Futura/FuturaLT.ttf" : BuildConfig.FLAVOR.equals("telenor")
            ? "fonts/Telenor.otf" : "fonts/OpenSans-Regular.ttf";
    public static String SERIF_FONT = BuildConfig.FLAVOR.equals("ooredoo")
    ? "fonts/Opifico/Opificio_Bold.ttf" : BuildConfig.FLAVOR.equals("telenor")
            ? "fonts/Telenor.otf" : "fonts/OpenSans-Regular.ttf";
    public static String SANS_SERIF_FONT = BuildConfig.FLAVOR.equals("ooredoo")
    ? "fonts/Opifico/Opificio.ttf" : BuildConfig.FLAVOR.equals("telenor")
            ? "fonts/Telenor.otf" : "fonts/OpenSans-Regular.ttf";

    public final static String ARABIC_DEFAULT_FONT = "fonts/Arabic/GE SS Unique Light.otf";

    public void onCreate() {
        super.onCreate();
        Logger.setEnabled(true);

        ActiveAndroid.initialize(this);

       // overrideDefaultFonts();

       /* setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread thread, Throwable e)
            {
                e.printStackTrace();
            }
        });*/
    }

    public void overrideDefaultFonts()
    {
        setDefaultFont(this, DEFAULT, language.equals("en") ? DEFAULT_FONT : ARABIC_DEFAULT_FONT);
        setDefaultFont(this, MONOSPACE, language.equals("en") ? MONOSPACE_FONT : ARABIC_DEFAULT_FONT);
        setDefaultFont(this, SERIF, language.equals("en") ? SERIF_FONT : ARABIC_DEFAULT_FONT);
        setDefaultFont(this, SANS_SERIF, language.equals("en") ? SANS_SERIF_FONT : ARABIC_DEFAULT_FONT);
    }

    public static HashMap<String, String> getUserCredentials() {
        HashMap<String, String> params = new HashMap<>();
        params.put("msisdn", user.username);
        params.put("password", encodeToBase64(user.password));
        return params;
    }

    synchronized public Tracker getDefaultTracker()
    {
        if(tracker == null)
        {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(R.xml.global_tracker);
        }

        return tracker;
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