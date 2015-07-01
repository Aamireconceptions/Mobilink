package com.ooredoo.bizstore.ui.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;

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
                notification.save();
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
        notifications.add(new Notification(1, R.drawable.ic_top_deals, getString(R.string.top_deals), true));
        notifications.add(new Notification(2, R.drawable.ic_food_dining, getString(R.string.food_dining), false));
        notifications.add(new Notification(3, R.drawable.ic_shopping, getString(R.string.shopping), true));
        notifications.add(new Notification(4, R.drawable.ic_electronics, getString(R.string.electronics), false));
        notifications.add(new Notification(5, R.drawable.ic_hotels, getString(R.string.hotels_spa), false));
        notifications.add(new Notification(6, R.drawable.ic_malls, getString(R.string.markets_malls), false));
        notifications.add(new Notification(7, R.drawable.ic_automotive, getString(R.string.automotive), false));
        notifications.add(new Notification(8, R.drawable.ic_travel, getString(R.string.travel_tours), false));
        notifications.add(new Notification(9, R.drawable.ic_entertainment, getString(R.string.entertainment), false));
        notifications.add(new Notification(10, R.drawable.ic_jewelry, getString(R.string.jewelry_exchange), false));
        notifications.add(new Notification(11, R.drawable.ic_sports, getString(R.string.sports_fitness), false));

        for(int i = 0; i < notifications.size(); i++) {
            Notification n = notifications.get(i);
            Notification notification = Notification.load(Notification.class, n.notificationId);
            if(notification != null) {
                n.enabled = notification.enabled;
            }
        }
    }

}