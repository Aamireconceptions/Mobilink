package com.ooredoo.bizstore.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import static android.content.Context.MODE_PRIVATE;
import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;
import static com.ooredoo.bizstore.utils.StringUtils.isNullOrEmpty;
import static java.lang.System.currentTimeMillis;

/**
 * @author Pehlaj Rai
 * @since 01/27/2015.
 */

public class SharedPrefUtils {

    public static final String MyPREFERENCES = "OrdBsPrefs";

    public static final String LOGIN_STATUS = "OrdBs_LG_STATUS";

    public static final String NAME = "OrdBs_LG_NAME";

    public static final String USERNAME = "OrdBs_LG_UZRNM";
    public static final String PASSWORD = "OrdBs_LG_PSWRD";

    public static final String EMPTY = "";
    public static final String SPRTR = ":::";
    public static final String DOUBLE_SPRTR = "::::::";

    public static SharedPreferences sharedPreferences;

    public static boolean getBooleanVal(Activity activity, final String KEY) {
        try {
            sharedPreferences = getSharedPreferences(activity);
            if(sharedPreferences != null) {
                if(sharedPreferences.contains(KEY)) {
                    return sharedPreferences.getBoolean(KEY, false);
                }
            }
        } catch(Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void updateVal(Activity activity, final String KEY, final boolean VAL) {
        try {
            sharedPreferences = getSharedPreferences(activity);
            if(sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(editor != null) {
                    editor.putBoolean(KEY, VAL);
                    editor.commit();
                }
            }
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }

    public static void updateVal(Activity activity, final String KEY, final long VAL) {
        try {
            if(activity != null) {
                sharedPreferences = getSharedPreferences(activity);
                if(sharedPreferences != null) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(editor != null) {
                        editor.putLong(KEY, VAL);
                        editor.commit();
                    }
                }
            }
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }

    public static void updateVal(Activity activity, final String KEY, final int VAL) {
        try {
            if(activity != null) {
                sharedPreferences = getSharedPreferences(activity);
                if(sharedPreferences != null) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(editor != null) {
                        editor.putInt(KEY, VAL);
                        editor.commit();
                    }
                }
            }
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }

    public static void updateVal(Activity activity, final String KEY, final String VAL) {
        try {
            if(activity != null) {
                sharedPreferences = getSharedPreferences(activity);
                if(VAL != null) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(editor != null) {
                        editor.putString(KEY, VAL);
                        editor.commit();
                    }
                }
            }
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }

    public static String getStringVal(Activity activity, final String KEY) {
        String tmp = EMPTY;
        try {
            if(activity != null) {
                sharedPreferences = getSharedPreferences(activity);
                if(sharedPreferences != null) {
                    if(sharedPreferences.contains(KEY)) {
                        tmp = sharedPreferences.getString(KEY, EMPTY);
                    }
                }
            }
        } catch(Throwable e) {
            e.printStackTrace();
        }
        return tmp;
    }

    public static SharedPreferences getSharedPreferences(Activity activity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return activity.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
        }
        return activity.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
    }

    public static long getLongVal(Activity activity, final String KEY) {
        try {
            if(activity != null) {
                sharedPreferences = getSharedPreferences(activity);
                if(sharedPreferences != null) {
                    if(sharedPreferences.contains(KEY)) {
                        return sharedPreferences.getLong(KEY, 0);
                    }
                }
            }
        } catch(Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void saveKeyToSharedPreferences(Activity activity, final String KEY, final String VAL) {
        try {
            if(activity != null && isNotNullOrEmpty(VAL)) {
                sharedPreferences = getSharedPreferences(activity);
                if(sharedPreferences != null) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    boolean doSave = true;
                    if(editor != null) {
                        String tmp = sharedPreferences.getString(KEY, EMPTY);
                        if(isNullOrEmpty(tmp) || tmp.equals(EMPTY)) {
                            tmp = VAL;
                        } else if(!tmp.contains(VAL)) {
                            tmp = tmp + SPRTR + VAL;
                        } else {
                            doSave = false;
                        }
                        if(doSave) {
                            editor.putString(KEY, tmp);
                            editor.commit();
                        }
                    }
                }
            }
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }

    public static void removeListItem(Activity activity, final String KEY, final String VAL) {
        try {
            if(activity != null && isNotNullOrEmpty(VAL)) {
                sharedPreferences = getSharedPreferences(activity);
                if(sharedPreferences != null) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(editor != null) {
                        String tmp = sharedPreferences.getString(KEY, EMPTY);
                        if(isNullOrEmpty(tmp) || tmp.equals(EMPTY)) {
                            return;
                        }
                        if(tmp.equalsIgnoreCase(VAL)) {
                            updateVal(activity, KEY, EMPTY);
                        } else if(tmp.contains(VAL)) {
                            if(tmp.contains(SPRTR)) {
                                tmp = tmp.replace(VAL, EMPTY);
                                if(tmp.endsWith(SPRTR)) {
                                    tmp = tmp.substring(0, tmp.length() - 3);
                                }
                                if(tmp.startsWith(SPRTR)) {
                                    tmp = tmp.substring(3);
                                }
                                if(tmp.contains(DOUBLE_SPRTR)) {
                                    tmp = tmp.replace(DOUBLE_SPRTR, SPRTR);
                                }
                                updateVal(activity, KEY, tmp);
                            } else {
                                updateVal(activity, KEY, EMPTY);
                            }
                        }
                    }
                }
            }
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }

    public static boolean checkIfUpdateData(Activity activity, String KEY) {
        long tmp = getLongVal(activity, KEY);
        return tmp == 0 || (currentTimeMillis() - tmp) > 10 * 60 * 1000;
    }
}
