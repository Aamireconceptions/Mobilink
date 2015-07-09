package com.ooredoo.bizstore;

/**
 * @author Pehlaj Rai
 * @since 6/22/2015.
 */
public class AppConstant {

    public static final String ID = "ID";
    public static final String TYPE_ID = "TYPE_ID";
    public static final String BUSINESS_TYPE = "BUSINESS_TYPE";
    public static final String CATEGORY = "CATEGORIES";

    public static final String DIALER_PREFIX = "tel:";

    public static final String PERCENT_OFF = " % OFF";

    public static final String INTERNET_CONN_ERR = "No Internet connection";

    public final static String ACTION_DEAL_DETAIL = "com.ooredoo.bizstore.deal_detail";
    public final static String ACTION_BUSINESS_DETAIL = "com.ooredoo.bizstore.business_detail";

    public static int NOTIFICATION_COUNT = 11;

    public static int DEAL = 1;
    public static int BUSINESS = 2;

    public static short SEARCH_DEALS = 1;
    public static short SEARCH_BUSINESS = 2;
    public static short SEARCH_DEALS_AND_BUSINESS = 3;

    public static String[] TAB_NAMES = { "Home", "Top Deals", "Food", "Shopping", "Electronics", "Hotels & Spas", "Malls", "Automotive", "Travel", "Entertainment", "Jewellery", "Sports & Fitness" };

    public static String[] DEAL_CATEGORIES = { "Promo", "Featured", "Top", "Shopping", "Electronics", "Hotels", "Malls", "Automotive", "Travel", "Entertainment", "Jewellery", "Sports" };

    public static float MAX_ALPHA = 1.0f;

    public static String PROFILE_PIC_URL = "/storage/emulated/0/images/obs_user_dp.jpg";

    public static final int MSISDN_MIN_LEN = 7; //TODO CHANGE MINIMUM LENGTH for Msisdn
    public static final int VERIFICATION_CODE_MIN_LEN = 4; //TODO CHANGE MINIMUM LENGTH for Msisdn Verification Code

    public static final String MSISDN_ERR_MSG = "Please provide valid phone number";
    public static final String MSISDN_VERIFICATION_MSG = "Please provide valid verification code";
}
