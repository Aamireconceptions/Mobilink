package com.ooredoo.bizstore.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;

/**
 * Created by Babar on 31-Jul-15.
 */
public class NotificationUtils
{
    public static void showNotification(Context context, String title, String desc, int id, Bitmap bitmap)
    {
        Notification.Builder builder =
                new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmap)
                .setContentTitle(title)
                .setTicker(desc)
                .setContentText(desc);

        if(bitmap != null)
        {
            Notification.BigPictureStyle bigPictureStyle = new Notification.BigPictureStyle();
            bigPictureStyle.bigPicture(bitmap);
            bigPictureStyle.setBigContentTitle(desc);

            builder.setStyle(bigPictureStyle);
        }

        Intent intent = new Intent(context, DealDetailActivity.class);
        intent.putExtra(AppConstant.ID, id);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself)
        taskStackBuilder.addParentStack(DealDetailActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(id,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.


        Notification notification = builder.build();

        //notification.defaults |= Notification.DEFAULT_ALL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(id, notification);
    }
}
