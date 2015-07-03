package com.ooredoo.bizstore.ui.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.NotificationsAdapter;
import com.ooredoo.bizstore.model.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pehlaj Rai
 * @since 6/24/2015.
 */
public class NotificationsActivity extends BaseActivity implements View.OnClickListener {

    public List<Notification> notifications = new ArrayList<>();

    private ListView mListView;
    private NotificationsAdapter mAdapter;

    public NotificationsActivity() {
        super();
        layoutResId = R.layout.activity_my_notifications;
    }

    @Override
    public void init() {
        setupToolbar();
        mListView = (ListView) findViewById(R.id.lv_notifications);

        initNotificationsData();

        mAdapter = new NotificationsAdapter(this, R.layout.list_item_notification, notifications);
        mListView.setAdapter(mAdapter);

        findViewById(R.id.rl_select_all).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.rl_select_all) {
            CheckBox cbSelectAll = (CheckBox) findViewById(R.id.cb_select_all);
            boolean checkAll = !cbSelectAll.isChecked();
            cbSelectAll.setChecked(checkAll);
            for(Notification notification : notifications) {
                notification.enabled = checkAll;
                saveNotification(notification);
            }
            mAdapter.updateItems(notifications);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void initNotificationsData() {
        notifications = new ArrayList<>();
        notifications.add(new Notification(1, false, R.drawable.ic_top_deals, getString(R.string.top_deals)));
        notifications.add(new Notification(2, false, R.drawable.ic_food_dining, getString(R.string.food_dining)));
        notifications.add(new Notification(3, false, R.drawable.ic_shopping, getString(R.string.shopping)));
        notifications.add(new Notification(4, false, R.drawable.ic_electronics, getString(R.string.electronics)));
        notifications.add(new Notification(5, false, R.drawable.ic_hotels, getString(R.string.hotels_spa)));
        notifications.add(new Notification(6, false, R.drawable.ic_malls, getString(R.string.markets_malls)));
        notifications.add(new Notification(7, false, R.drawable.ic_automotive, getString(R.string.automotive)));
        notifications.add(new Notification(8, false, R.drawable.ic_travel, getString(R.string.travel_tours)));
        notifications.add(new Notification(9, false, R.drawable.ic_entertainment, getString(R.string.entertainment)));
        notifications.add(new Notification(10, false, R.drawable.ic_jewellery, getString(R.string.jewelry_exchange)));
        notifications.add(new Notification(11, false, R.drawable.ic_sports, getString(R.string.sports_fitness)));
    }

    public void saveNotification(Notification notification) {
        if(notification != null && notification.id > 0) {
            List<Notification> notifications = new Select().all().from(Notification.class).where("notificationId=" + notification.id).execute();
            if(notifications == null || notifications.size() == 0) {
                Log.i("SAVE", "NEW---" + notification.toString());
                notification.save();
            } else {
                Notification n = notifications.get(0);
                n.id = notification.id;
                n.icon = notification.icon;
                n.title = notification.title;
                n.enabled = notification.enabled;
                Log.i("UPDATE", "EXISTING---" + n.toString());
                n.save();
            }
        }
    }
}