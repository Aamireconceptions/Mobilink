package com.ooredoo.bizstore;

import android.app.Application;
import android.os.Build;

import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;

import static java.lang.Thread.UncaughtExceptionHandler;
import static java.lang.Thread.setDefaultUncaughtExceptionHandler;

/**
 * @author pehlaj.rai
 * @since 6/10/2015.
 */

public class BizStore extends Application
{
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
}