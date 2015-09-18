package com.ooredoo.bizstore;

import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;

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

    public final static String ACTION_DEAL_DETAIL = "com.ooredoo.bizstore.deal_detail";
    public final static String ACTION_BUSINESS_DETAIL = "com.ooredoo.bizstore.business_detail";

    public static int NOTIFICATION_COUNT = 11;

    public static int DEAL = 1;
    public static int BUSINESS = 2;

    public static String[] DEAL_CATEGORIES = { "Promo", "Featured", "Top", "Shopping", "Electronics", "Hotels", "Malls", "Automotive", "Travel", "Entertainment", "Jewellery", "Sports" };

    public static float MAX_ALPHA = 1.0f;

    public static String PROFILE_PIC_URL = BaseAsyncTask.SERVER_URL + "/ooredoo/uploads/user/" + BizStore.username + ".jpg";

    public static final int MSISDN_MIN_LEN = 7; //TODO CHANGE MINIMUM LENGTH for Msisdn
    public static final int VERIFICATION_CODE_MIN_LEN = 4; //TODO CHANGE MINIMUM LENGTH for Msisdn Verification Code

}
