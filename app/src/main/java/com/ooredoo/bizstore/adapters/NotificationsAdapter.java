package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Notification;
import com.ooredoo.bizstore.ui.activities.NotificationsActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.util.List;

public class NotificationsAdapter extends ArrayAdapter<Notification> {

    Activity mActivity;
    int layoutResID;
    List<Notification> notifications;

    public NotificationsAdapter(Activity activity, int layoutResourceID, List<Notification> notifications) {
        super(activity, layoutResourceID, notifications);
        this.mActivity = activity;
        this.notifications = notifications;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Notification notification = this.notifications.get(position);

        final Holder holder;
        View view = convertView;

        final LayoutInflater inflater = mActivity.getLayoutInflater();

        if(view == null) {
            holder = new Holder();
        } else {
            holder = (Holder) view.getTag();
        }

        if(view == null) {
            view = inflater.inflate(layoutResID, parent, false);

            holder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
            holder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            holder.ivCategory = (ImageView) view.findViewById(R.id.iv_category);

            view.setClickable(true);
            view.setFocusable(true);

            view.setTag(holder);
        }

        List<Notification> n = new Select().all().from(Notification.class).where("notificationId=" + notification.id).execute();
        if(n != null && n.size() > 0) {
            notification.enabled = n.get(0).enabled;
            Logger.logI("ENABLED: " + notification.id, position + " - " + notification.enabled);
        }

        holder.checkBox.setOnCheckedChangeListener(new CheckBoxChangeListener(position));

        holder.tvTitle.setText(notification.title);
        holder.checkBox.setChecked(notification.enabled);
        holder.ivCategory.setImageResource(notification.icon);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCheckBox(holder.checkBox, notification);
            }
        });

        /*holder.ivCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCheckBox(holder.checkBox, notification);
            }
        });*/

        return view;
    }

    private void toggleCheckBox(CheckBox checkBox, Notification notification) {
        boolean isChecked = checkBox.isChecked();
        checkBox.setChecked(!isChecked);
        notification.enabled = !isChecked;
        ((NotificationsActivity) mActivity).saveNotification(notification);
    }

    public void updateItems(List<Notification> notifications) {
        this.notifications = notifications;
    }

    private class CheckBoxChangeListener implements CompoundButton.OnCheckedChangeListener {
        int position;

        public CheckBoxChangeListener(int position) {
            this.position = position;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Notification notification = getItem(position);
            notification.enabled = isChecked;
            ((NotificationsActivity) mActivity).saveNotification(notification);
        }
    }

    private static class Holder {
        TextView tvTitle;
        CheckBox checkBox;
        ImageView ivCategory;
    }
}