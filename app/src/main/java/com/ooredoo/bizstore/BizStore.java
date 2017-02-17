package com.ooredoo.bizstore;

import android.content.Context;

import com.activeandroid.ActiveAndroid;

import com.activeandroid.Configuration;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.ooredoo.bizstore.model.Notification;
import com.ooredoo.bizstore.model.SearchItem;
import com.ooredoo.bizstore.model.User;
import com.ooredoo.bizstore.utils.CryptoUtils;
import com.ooredoo.bizstore.utils.Logger;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.util.HashMap;


import static com.ooredoo.bizstore.utils.CryptoUtils.encodeToBase64;
import static com.ooredoo.bizstore.utils.FontUtils.setDefaultFont;

/**
 * @author pehlaj.rai
 * @since 6/10/2015.
 */

public class BizStore extends com.activeandroid.app.Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "OKSJJQBBfK1BtHGGSHVihvo1I";
    private static final String TWITTER_SECRET = "TBrDBDmYUlVKEm8GHbO894trmyq2G77fFIyRBP6F88SuVfmKlA";


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

    public static String DEFAULT_FONT = BuildConfig.FLAVOR.equals("ooredoo")
    ? "fonts/Futura/FuturaLT-Book.ttf" : BuildConfig.FLAVOR.equals("telenor")
            ? "fonts/Telenor.otf" : BuildConfig.FLAVOR.equals("mobilink")
            ? "fonts/lato_regular.ttf" : BuildConfig.FLAVOR.equals("zong")
            ? "fonts/lato_regular.ttf" : "fonts/OpenSans-Regular.ttf";
    public static String MONOSPACE_FONT = BuildConfig.FLAVOR.equals("ooredoo")
    ? "fonts/Futura/FuturaLT.ttf" : BuildConfig.FLAVOR.equals("telenor")
            ? "fonts/Telenor.otf" : BuildConfig.FLAVOR.equals("mobilink")
            ? "fonts/lato_regular.ttf": BuildConfig.FLAVOR.equals("zong")
            ? "fonts/lato_regular.ttf" : "fonts/OpenSans-Regular.ttf";
    public static String SERIF_FONT = BuildConfig.FLAVOR.equals("ooredoo")
    ? "fonts/Opifico/Opificio_Bold.ttf" : BuildConfig.FLAVOR.equals("telenor")
            ? "fonts/Telenor.otf" : BuildConfig.FLAVOR.equals("mobilink")
            ? "fonts/lato_regular.ttf": BuildConfig.FLAVOR.equals("zong")
            ? "fonts/lato_regular.ttf" : "fonts/OpenSans-Regular.ttf";
    public static String SANS_SERIF_FONT = BuildConfig.FLAVOR.equals("ooredoo")
    ? "fonts/Opifico/Opificio.ttf" : BuildConfig.FLAVOR.equals("telenor")
            ? "fonts/Telenor.otf" : BuildConfig.FLAVOR.equals("mobilink")
            ? "fonts/lato_regular.ttf": BuildConfig.FLAVOR.equals("zong")
            ? "fonts/lato_regular.ttf" : "fonts/OpenSans-Regular.ttf";

    public final static String ARABIC_DEFAULT_FONT = "fonts/Arabic/GE SS Unique Light.otf";

   /* @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }*/

    public void onCreate() {

        super.onCreate();
        /*Configuration dbConfiguration = new Configuration.Builder(this)
                .setDatabaseName("ooredoo_bizstore.db")
                .addModelClass(SearchItem.class)

                .create();*/

        ActiveAndroid.initialize(this);

        if(BuildConfig.FLAVOR.equals("ooredoo")) {
            TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
            Fabric.with(this, new Twitter(authConfig));
        }
        context = this;
        if(BuildConfig.FLAVOR.equals("mobilink") || BuildConfig.FLAVOR.equals("ooredoo"))
        {
            FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback()
                    {
                @Override
                public void onInitialized() {
                    if(AccessToken.getCurrentAccessToken() == null){
                        System.out.println("not logged in yet");
                    } else {
                        System.out.println("Logged in");
                    }
                }
            });

            // FacebookSdk.setIsDebugEnabled(true);
           //  FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
            if(BuildConfig.FLAVOR.equals("mobilink")) {
                AppEventsLogger.activateApp(this);
            }
        }

        if(BuildConfig.DEBUG)
        {
            GoogleAnalytics.getInstance(this).setDryRun(true);

            Logger.setEnabled(true);
        }
        else {
            Logger.setEnabled(false);
        }
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