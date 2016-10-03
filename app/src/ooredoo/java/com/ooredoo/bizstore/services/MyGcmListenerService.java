package com.ooredoo.bizstore.services;

import android.os.Bundle;

import com.activeandroid.query.Select;
import com.google.android.gms.gcm.GcmListenerService;
import com.ooredoo.bizstore.asynctasks.BaseAsyncTask;
import com.ooredoo.bizstore.asynctasks.BitmapNotificationTask;
import com.ooredoo.bizstore.model.Notification;
import com.ooredoo.bizstore.utils.Converter;
import com.ooredoo.bizstore.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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
        Logger.print("Message: " + message);

        try {
            JSONObject jsonObject = new JSONObject(message);

            int id = jsonObject.getInt("id");

            Logger.print("notification id:"+id);
            String title = jsonObject.getString("title");

            String desc = jsonObject.getString("description");

            if(title.isEmpty())
            {
                return;
            }

            String category = jsonObject.getString("category");

            List<Notification> notifications = new Select().from(Notification.class).execute();

            Logger.print("GCM: category: "+category);
            for(Notification notification : notifications) {

                if (notification.enabled) {

                    Logger.print("GCM my preference: " + notification.category);

                    if (category.contains(notification.category)) {
                        String imgUrl = BaseAsyncTask.SERVER_URL + jsonObject.getString("url");

                        String reqWidth = String.valueOf((int) Converter.convertDpToPixels(256));

                        String reqHeight = String.valueOf((int) Converter.convertDpToPixels(256));

                        Logger.print("gcm width:" + reqWidth);

                        BitmapNotificationTask bitmapNotificationTask = new BitmapNotificationTask(this, id,
                                title, desc);
                        bitmapNotificationTask.execute(imgUrl, reqWidth, reqHeight);

                        return;
                    }
                }
            }

            if(notifications.size() < 1)
            {
                String imgUrl = BaseAsyncTask.SERVER_URL + jsonObject.getString("url");

                String reqWidth = String.valueOf((int) Converter.convertDpToPixels(350));

                String reqHeight = String.valueOf((int) Converter.convertDpToPixels(256));

                Logger.print("gcm width:" + reqWidth);

                BitmapNotificationTask bitmapNotificationTask = new BitmapNotificationTask(this, id,
                        title, desc);
                bitmapNotificationTask.execute(imgUrl, reqWidth, reqHeight);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}