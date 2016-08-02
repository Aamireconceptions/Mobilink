package com.ooredoo.bizstore.utils;

import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by Babar on 02-Aug-16.
 */
public class IntentUtils
{
    public static Intent getPdfIntent(File file)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");

        return intent;
    }
}