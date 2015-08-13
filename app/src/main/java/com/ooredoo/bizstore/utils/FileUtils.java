package com.ooredoo.bizstore.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * Created by Babar on 12-Aug-15.
 */
public class FileUtils
{
    // Creates a unique subdirectory of the designated app cache directory. Tries to use external
    // but if not mounted, falls back on internal storage.
    public static File getDiskCacheDir(Context context, String uniqueName)
    {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir

        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                !Environment.isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
                        context.getCacheDir().getPath();

        Logger.print("getDiskCacheDir name:" + cachePath + uniqueName);

        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * Check how much usable space is available at a given path.
     *
     * @param path The path to check
     * @return The space available in bytes
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static long getUsableSpace(File path)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
        {
            return path.getUsableSpace();
        }

        final StatFs stats = new StatFs(path.getPath());

        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }
}