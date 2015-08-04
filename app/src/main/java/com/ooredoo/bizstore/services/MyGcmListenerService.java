package com.ooredoo.bizstore.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.gcm.GcmListenerService;
import com.ooredoo.bizstore.utils.Logger;

/**
 * Created by Babar on 31-Jul-15.
 */
public class MyGcmListenerService extends GcmListenerService
{
    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        Logger.print("MyGCMListenerService onMessageReceived" );

        String message = data.getString("message");

        Logger.print("From: "+from);
        Logger.print("Message: "+message);
    }
}
