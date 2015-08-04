package com.ooredoo.bizstore.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Babar on 31-Jul-15.
 */
public class MyInstanceIDListenerService extends Service
{

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
