package com.ooredoo.bizstore.asynctasks;

import android.content.Context;

/**
 * Created by Babar on 22-Jun-15.
 */
public class HomeAsyncTask extends BaseAsyncTask<Void, Void, String>
{
    private Context context;

    public HomeAsyncTask(Context context)
    {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params)
    {
        return null;
    }
}
