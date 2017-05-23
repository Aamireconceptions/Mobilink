package com.ooredoo.bizstore.asynctasks;

import com.ooredoo.bizstore.utils.DiskCache;

import java.io.IOException;

/**
 * A helper class for all disk cache requests that run on background
 * Created by Babar on 12-Aug-15.
 */
public class DiskCacheTask extends BaseAsyncTask<Integer, Void, Void>
{
    private DiskCache diskCache;

    public static final int INIT = 1;

    public static final int FLUSH = 2;

    public static final int CLOSE = 3;

    public static final int TEAR_DOWN = 4;

    public static final int REMOVE = 5;

    public DiskCacheTask(DiskCache diskCache)
    {
        this.diskCache = diskCache;
    }

    @Override
    protected Void doInBackground(Integer... params)
    {
        switch (params[0])
        {
            case INIT:

                try
                {
                    diskCache.init();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                break;

            case FLUSH:

                diskCache.flush();

                break;

            case CLOSE:

                diskCache.close();

                break;

            case TEAR_DOWN:

                diskCache.tearDown();

                break;

            case REMOVE:

                //diskCache.remove();

                break;
        }

        return null;
    }
}