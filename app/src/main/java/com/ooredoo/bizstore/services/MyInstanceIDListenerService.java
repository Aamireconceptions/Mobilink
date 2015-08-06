package com.ooredoo.bizstore.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.ooredoo.bizstore.utils.GcmPreferences;
import com.ooredoo.bizstore.utils.SharedPrefUtils;

/**
 * Created by Babar on 31-Jul-15.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService
{
    @Override
    public void onTokenRefresh()
    {
        super.onTokenRefresh();

        new GcmPreferences(this).onTokenRefreshed();
    }
}
