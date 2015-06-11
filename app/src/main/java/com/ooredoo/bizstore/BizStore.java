package com.ooredoo.bizstore;

import android.app.Application;
import android.os.Build;

import com.ooredoo.bizstore.utils.FontUtils;

import static java.lang.Thread.UncaughtExceptionHandler;
import static java.lang.Thread.setDefaultUncaughtExceptionHandler;

/**
 * @author pehlaj.rai
 * @since 6/10/2015.
 */

public class BizStore extends Application
{
    private String deafultFont = "fonts/Futura/FuturaLT-Book.ttf";
    private String monospaceFont = "fonts/Futura/FuturaLT.ttf";
    private String serifFont = "fonts/Opifico/Opificio_Bold.ttf";
    private String sans_serif = "fonts/Opifico/Opificio.ttf";

    public void onCreate()
    {
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
        FontUtils.setDefaultFont(this, "DEFAULT", deafultFont);
        FontUtils.setDefaultFont(this, "MONOSPACE", monospaceFont);
        FontUtils.setDefaultFont(this, "SERIF", serifFont);
        FontUtils.setDefaultFont(this, "SANS_SERIF", sans_serif);
    }

}