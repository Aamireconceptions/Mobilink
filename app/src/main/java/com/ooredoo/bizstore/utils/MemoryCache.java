package com.ooredoo.bizstore.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.model.Mall;

import java.util.List;

/**
 * @author  Babar
 * @since 15-Jun-15.
 */
public class MemoryCache
{
    private static MemoryCache memoryCache;

   // private static final int cacheSize =

    private static LruCache<String, Bitmap> lruCache;

    private MemoryCache(){};

    public static MemoryCache getInstance()
    {
        if(memoryCache == null)
        {
            memoryCache = new MemoryCache();

            init();
        }

        return memoryCache;
    }

    private static void init()
    {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) Runtime.getRuntime().maxMemory();

        final int cacheSize = maxMemory / 6;

        Logger.logI("maxHeapMemory: ",""+maxMemory / 1024 / 1024 + " MB");
        Logger.logI("CacheSize: ",""+cacheSize / 1024/ 1024 + " MB");

        lruCache = new LruCache<String, Bitmap>(cacheSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap value)
            {
                // The cache size will be measured in kilobytes rather than
                // number of items.

                return value.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToCache(String key, Bitmap value)
    {
        if(getBitmapFromCache(key) == null)
        {
            lruCache.put(key, value);
        }
    }

    public void removeBitmapFromCache(String key)
    {
        lruCache.remove(key);
    }

    public Bitmap getBitmapFromCache(String key)
    {
        return lruCache.get(key);
    }

    public void remove(List<GenericDeal> genericDeals)
    {
        for(GenericDeal genericDeal : genericDeals)
        {
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + genericDeal.image.bannerUrl);
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + genericDeal.image.detailBannerUrl);
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + genericDeal.image.featured);
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + genericDeal.image.gridBannerUrl);
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + genericDeal.image.promotionalUrl);
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + genericDeal.image.logoUrl);
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + genericDeal.businessLogo);
        }

        Logger.print("remove Memory");
    }

    public void removeBrands(List<Brand> brands)
    {
       /* for(Brand brand : brands)
        {
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + brand.image.bannerUrl);
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + brand.image.detailBannerUrl);
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + brand.image.featured);
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + brand.image.gridBannerUrl);
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + brand.image.promotionalUrl);
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + brand.image.logoUrl);
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + brand.businessLogo);
        }*/
    }

    public void removeMalls(List<Mall> malls)
    {
        /*for(Mall mall : malls)
        {
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + mall.image.bannerUrl);
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + mall.image.detailBannerUrl);
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + mall.image.featured);
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + mall.image.gridBannerUrl);
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + mall.image.promotionalUrl);
            lruCache.remove(BaseAsyncTask.IMAGE_BASE_URL + mall.image.logoUrl);
        }*/
    }

    public void tearDown()
    {
        lruCache.evictAll();
    }


}
