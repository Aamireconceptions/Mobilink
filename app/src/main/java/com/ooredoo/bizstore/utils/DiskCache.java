package com.ooredoo.bizstore.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.ooredoo.bizstore.asynctasks.DiskCacheTask;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Babar on 12-Aug-15.
 */
public class DiskCache
{

    private Context context;

    private static DiskCache diskCache;

    public DiskLruCache mDiskLruCache;

    private final Object mDiskCacheLock = new Object();

    private BitmapProcessor bitmapProcessor;



    private static final int DISK_CACHE_INDEX = 0;

    public static boolean mDiskCacheStarting = true;

    private static final String DISK_CACHE_SUBDIR = "ooredoo_thumbnails";

    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 50; // 50MB

    public static DiskCache getInstance()
    {
        if(diskCache == null)
        {
            diskCache = new DiskCache();
        }

        return diskCache;
    }

    private DiskCache() {}

    public void requestInit(Context context)
    {
        this.context = context;

        bitmapProcessor = new BitmapProcessor();

        new DiskCacheTask(this).execute(DiskCacheTask.INIT);
    }

    public void init() throws IOException {
        synchronized (mDiskCacheLock)
        {
            if(mDiskLruCache == null || mDiskLruCache.isClosed())
            {
                File cacheDir = FileUtils.getDiskCacheDir(context, DISK_CACHE_SUBDIR);

                if(!cacheDir.exists())
                {
                    cacheDir.mkdir();
                }

                if(FileUtils.getUsableSpace(cacheDir) > DISK_CACHE_SIZE)
                {
                    mDiskLruCache = DiskLruCache.open(cacheDir, 1, 1, DISK_CACHE_SIZE);
                }
                else
                {
                    Logger.print("InitDiskCache failed: NOT enough space on disk");
                }
            }

            mDiskCacheStarting = false; // Finished initialization
            mDiskCacheLock.notifyAll(); // Wake any waiting threads
        }
    }

    public void addBitmapToDiskCache(final String key, final Bitmap value) {
        if (key == null || value == null) {
            return;
        }

        synchronized (mDiskCacheLock)
        {
            if (mDiskLruCache != null) {
                OutputStream out = null;

                String encryptedKey = CryptoUtils.encryptToMD5(key);

                Logger.print("addBitmapToDiskCache encryptToMD5: " + encryptedKey);

                try {
                    DiskLruCache.Snapshot snapshot = mDiskLruCache.get(encryptedKey);

                    if (snapshot == null) {
                        final DiskLruCache.Editor editor = mDiskLruCache.edit(encryptedKey);

                        if (editor != null) {
                            out = editor.newOutputStream(DISK_CACHE_INDEX);

                            value.compress(Bitmap.CompressFormat.JPEG, 100, out);

                            editor.commit();
                            out.close();
                        }
                    } else {
                        snapshot.getInputStream(DISK_CACHE_INDEX).close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Get from disk cache.
     *
     * @param key Unique identifier for which item to get
     * @return The bitmap if found in cache, null otherwise
     */
    public Bitmap getBitmapFromDiskCache(final String key)
    {
        Bitmap bitmap = null;

        String encryptedKey = CryptoUtils.encryptToMD5(key);

        Logger.print("getBitmapFromDiskCache encryptToMD5: " + encryptedKey);

        synchronized (mDiskCacheLock)
        {
            // Wait while disk cache is started from background thread
            while (mDiskCacheStarting)
            {
                try
                {
                    mDiskCacheLock.wait();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            if(mDiskLruCache != null)
            {
                InputStream inputStream = null;

                try
                {
                    final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(encryptedKey);

                    if(snapshot != null)
                    {
                        Logger.print("Disk cache hit");

                        inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);

                        if(inputStream != null)
                        {
                            FileDescriptor fd = ((FileInputStream) inputStream).getFD();

                            // Decode bitmap, but we don't want to sample so give
                            // MAX_VALUE as the target dimensions

                            bitmap = bitmapProcessor.decodeSampledBitmapFromDescriptor(fd);
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if(inputStream != null)
                    {
                        try
                        {
                            inputStream.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
            Logger.print("dCache getBitmapFromDiskCache synchronized completed");
        }

        Logger.print("dCache getBitmapFromDiskCache returning Bitmap");
        return bitmap;
    }

    public void requestFlush()
    {
        new DiskCacheTask(this).execute(DiskCacheTask.FLUSH);
    }

    /**
     * Flushes the disk cache associated with this ImageCache object. Note that this includes
     * disk access so this should not be executed on the main/UI thread.
     */
    public void flush()
    {
        synchronized (mDiskCacheLock)
        {
            if(mDiskLruCache != null)
            {
                try
                {
                    mDiskLruCache.flush();

                    Logger.print("flush: disk cache flushed");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void requestClose()
    {
        new DiskCacheTask(this).execute(DiskCacheTask.CLOSE);
    }

    /**
     * Closes the disk cache associated with this ImageCache object. Note that this includes
     * disk access so this should not be executed on the main/UI thread.
     */
    public void close()
    {
        synchronized (mDiskCacheLock)
        {
            if(mDiskLruCache != null)
            {
                if(!mDiskLruCache.isClosed())
                {
                    try
                    {
                        mDiskLruCache.close();
                        mDiskLruCache = null;

                        Logger.print("disk cache closed");
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void requestTearDown()
    {
        new DiskCacheTask(this).execute(DiskCacheTask.TEAR_DOWN);
    }

    /**
     * Do not call this method unless you need to explicitly clear disk cache
     */
    public final void tearDown()
    {
        synchronized (mDiskCacheLock)
        {
            mDiskCacheStarting = true;

            if(mDiskLruCache != null && !mDiskLruCache.isClosed())
            {
                try
                {
                    mDiskLruCache.delete();

                    Logger.print("disk cache cleared");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                mDiskLruCache = null;
            }
        }
    }
}