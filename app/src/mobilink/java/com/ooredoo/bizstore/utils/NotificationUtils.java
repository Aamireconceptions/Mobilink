package com.ooredoo.bizstore.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.DealDetailActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.MallDetailActivity;

import java.io.File;

/**
 * Created by Babar on 31-Jul-15.
 */
public class NotificationUtils
{
    private Context context;

    public NotificationUtils(Context context)
    {
        this.context = context;
    }

    public static void showNotification(Context context, String title, String desc, int id,
                                        Bitmap[] bitmap, String type)
    {
        Notification.Builder builder =
                new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setTicker(desc)
                .setContentText(desc);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            builder.setColor(Color.parseColor("#555555"));
            builder.setSmallIcon(R.drawable.jazz_notification_icon);
            //builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        }

        if(bitmap != null)
        {
            if (bitmap[0] != null)
            {
                Notification.BigPictureStyle bigPictureStyle = new Notification.BigPictureStyle();
                bigPictureStyle.bigPicture(bitmap[0]);
                bigPictureStyle.setBigContentTitle(desc);

                builder.setStyle(bigPictureStyle);
            }

            if (bitmap[1] != null)
            {
                builder.setLargeIcon(bitmap[1]);
            }
        }

        Intent intent;
        if(type.equals("business")) {
             intent = new Intent(context, DealDetailActivity.class);
        }
        else
        {
            intent = new Intent(context, MallDetailActivity.class);
        }

        intent.putExtra(AppConstant.ID, id);
        intent.putExtra("notification", true);

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

    NotificationManager notificationManager;
    NotificationCompat.Builder downloadBuilder;
    Notification downloadNotification;

    public void showDownloadingNotification(int id, String title, String description)
    {
        int smallIcon = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
                ? R.mipmap.ic_launcher
                : R.drawable.jazz_notification_icon;

        notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        downloadBuilder = new NotificationCompat.Builder(context);
        downloadBuilder.setContentTitle(title);
        downloadBuilder.setColor(context.getResources().getColor(R.color.red));
        downloadBuilder.setSmallIcon(smallIcon);
        downloadBuilder.setTicker(title);
        downloadBuilder.setProgress(100, 0, true);
        downloadBuilder.setOngoing(true);

        downloadNotification = downloadBuilder.build();

        notificationManager.notify(id, downloadNotification);
    }

    public void updateNotificationProgress(float progress, final int id)
    {
        downloadBuilder.setProgress(100, (int) progress, false);

        downloadBuilder.setContentText(100 - (int) progress + "% remaining");
        downloadNotification = downloadBuilder.build();
        notificationManager.notify(id, downloadNotification);
    }

    public void onDownloadFinish(String msg, String subMsg, int id, File file)
    {
        downloadBuilder.setContentTitle(msg);
        downloadBuilder.setTicker(msg);
        downloadBuilder.setOngoing(false);
        downloadBuilder.setAutoCancel(true);
        downloadBuilder.setProgress(0, 0, false);

        if(subMsg != null)
        {
            Intent intent = IntentUtils.getPdfIntent(file);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 121, intent, 0);

            downloadBuilder.setContentIntent(pendingIntent);
            downloadBuilder.setContentText(subMsg);
        }

        downloadNotification = downloadBuilder.build();
        notificationManager.notify(id, downloadNotification);
    }
}