package com.ooredoo.bizstore.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.BaseAdapter;

import com.ooredoo.bizstore.asynctasks.BaseAdapterBitmapDownloadTask;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.DiskCacheTask;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Mall;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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

    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 100; // 100MB

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

    /**
     * Save the bitmap to disk cache. Due to disk access this method shouldn't be called from UI Thread.
     * @param key the key for the bitmap to save against i.e img_url
     * @param value bitmap
     */
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

                            value.compress(Bitmap.CompressFormat.JPEG, 80, out);

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
            Logger.print("mDiskcachestarting: "+mDiskCacheStarting);
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

    List<GenericDeal> genericDeals = new ArrayList<>();

    public void requestRemove(List<GenericDeal> genericDeals)
    {
        this.genericDeals.clear();
        this.genericDeals.addAll(genericDeals);

        DiskCacheTask diskCacheTask = new DiskCacheTask(diskCache);
        diskCacheTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, DiskCacheTask.REMOVE);
    }

    public void remove(List<GenericDeal> genericDeals)
    {
        synchronized (mDiskCacheLock)
        {
            mDiskCacheStarting = true;

            /*if (mDiskLruCache != null && !mDiskLruCache.isClosed())
            {
                for(GenericDeal genericDeal : genericDeals)
                {
                    try
                    {
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + genericDeal.image.bannerUrl));
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + genericDeal.image.detailBannerUrl));
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + genericDeal.image.featured));
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + genericDeal.image.gridBannerUrl));
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + genericDeal.image.promotionalUrl));
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + genericDeal.image.logoUrl));
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + genericDeal.businessLogo));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                }
            }*/

            mDiskCacheStarting = false;
        }

        Logger.print("remove: Disk");
    }

    public void removeBrands(List<Brand> brands)
    {
        /*synchronized (mDiskCacheLock)
        {
            mDiskCacheStarting = true;

            if (mDiskLruCache != null && !mDiskLruCache.isClosed())
            {
                for(Brand brand : brands)
                {
                    try
                    {
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + brand.image.bannerUrl));
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + brand.image.detailBannerUrl));
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + brand.image.featured));
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + brand.image.gridBannerUrl));
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + brand.image.promotionalUrl));
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + brand.image.logoUrl));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            mDiskCacheStarting = false;
        }

        Logger.print("remove: Done");*/
    }

    public void removeMalls(List<Mall> malls)
    {
        /*synchronized (mDiskCacheLock)
        {
            mDiskCacheStarting = true;

            if (mDiskLruCache != null && !mDiskLruCache.isClosed())
            {
                for(Mall mall : malls)
                {
                    try
                    {
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + mall.image.bannerUrl));
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + mall.image.detailBannerUrl));
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + mall.image.featured));
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + mall.image.gridBannerUrl));
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + mall.image.promotionalUrl));
                        mDiskLruCache.remove(CryptoUtils.encryptToMD5(BaseAsyncTask.IMAGE_BASE_URL + mall.image.logoUrl));
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            mDiskCacheStarting = false;
        }

        Logger.print("remove: Done");*/
    }

    /**
     * Do not call this method unless you need to explicitly clear disk cache
     */
    public void requestTearDown()
    {
        new DiskCacheTask(this).execute(DiskCacheTask.TEAR_DOWN);
    }

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