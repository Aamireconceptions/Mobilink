package com.ooredoo.bizstore;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.ooredoo.bizstore.model.User;
import com.ooredoo.bizstore.utils.CryptoUtils;
import com.ooredoo.bizstore.utils.Logger;

import static com.ooredoo.bizstore.utils.FontUtils.setDefaultFont;

/**
 * @author pehlaj.rai
 * @since 6/10/2015.
 */

public class BizStore extends com.activeandroid.app.Application {

    public static Context context;

    private static final User user = new User();

    public static boolean forceStopTasks = false;

    private static String language = "en";

    public static int lastTab = -1;

    public static String username = "";

    public static String password = "";

    public static String secret = CryptoUtils.key;

    private Tracker tracker, ooredooTracker;

    private final static String DEFAULT = "DEFAULT";
    private final static String MONOSPACE = "MONOSPACE";
    private final static String SERIF = "SERIF";
    private final static String SANS_SERIF = "SANS_SERIF";

    public static String DEFAULT_FONT = "fonts/lato_regular.ttf";

    public static String MONOSPACE_FONT = "fonts/lato_regular.ttf";

    public static String SERIF_FONT = "fonts/lato_regular.ttf";

    public static String SANS_SERIF_FONT = "fonts/lato_regular.ttf";
  /*  public static String SANS_SERIF_FONT = BuildConfig.FLAVOR.equals("ooredoo")
    ? "fonts/Opifico/Opificio.ttf" : BuildConfig.FLAVOR.equals("telenor")
            ? "fonts/Telenor.otf" : BuildConfig.FLAVOR.equals("mobilink")
            ? "fonts/lato_regular.ttf" : BuildConfig.FLAVOR.equals("zong")
            ? "fonts/lato_regular.ttf" : "fonts/lato_regular.ttf";*/


    public final static String ARABIC_DEFAULT_FONT = "fonts/Arabic/GE SS Unique Light.otf";

    public void onCreate() {

        super.onCreate();
        try {
            ActiveAndroid.initialize(this);

            context = this;

            FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
                @Override
                public void onInitialized() {
                    if (AccessToken.getCurrentAccessToken() == null) {
                        System.out.println("not logged in yet");
                    } else {
                        System.out.println("Logged in");
                    }
                }
            });

            // FacebookSdk.setIsDebugEnabled(true);
            //FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
            AppEventsLogger.activateApp(this);

            if (BuildConfig.DEBUG) {
                GoogleAnalytics.getInstance(this).setDryRun(true);

                Logger.setEnabled(true);
            } else {
                Logger.setEnabled(false);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void overrideDefaultFonts()
    {
        setDefaultFont(this, DEFAULT, language.equals("en") ? DEFAULT_FONT : ARABIC_DEFAULT_FONT);
        setDefaultFont(this, MONOSPACE, language.equals("en") ? MONOSPACE_FONT : ARABIC_DEFAULT_FONT);
        setDefaultFont(this, SERIF, language.equals("en") ? SERIF_FONT : ARABIC_DEFAULT_FONT);
        setDefaultFont(this, SANS_SERIF, language.equals("en") ? SANS_SERIF_FONT : ARABIC_DEFAULT_FONT);
    }


    synchronized public Tracker getDefaultTracker()
    {
        if(tracker == null)
        {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(R.xml.global_tracker);

            // Enable Display Feature
            tracker.enableAdvertisingIdCollection(true);
        }

        return tracker;
    }

    synchronized public Tracker getOoredooTracker()
    {
        if(ooredooTracker == null)
        {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

            ooredooTracker = analytics.newTracker(R.xml.ooredoo_tracker);
        }

        return ooredooTracker;
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