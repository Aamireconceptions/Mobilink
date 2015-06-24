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

    public List<Notification> notificationItems = new ArrayList<>();
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

        notificationItems = getNotificationItems();

        mAdapter = new NotificationsAdapter(this, R.layout.list_item_notification, notificationItems);
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
            for(Notification notification : notificationItems) {
                notification.enabled = checkAll;
            }
            mAdapter.updateItems(notificationItems);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public List<Notification> getNotificationItems() {
        List<Notification> list = new ArrayList<>();
        list.add(new Notification(0, R.drawable.ic_top_deals, getString(R.string.top_deals), true));
        list.add(new Notification(0, R.drawable.ic_food_dining, getString(R.string.food_dining), false));
        list.add(new Notification(0, R.drawable.ic_shopping, getString(R.string.shopping), true));
        list.add(new Notification(0, R.drawable.ic_electronics, getString(R.string.electronics), false));
        list.add(new Notification(0, R.drawable.ic_hotels, getString(R.string.hotels_spa), false));
        list.add(new Notification(0, R.drawable.ic_malls, getString(R.string.markets_malls), false));
        list.add(new Notification(0, R.drawable.ic_automotive, getString(R.string.automotive), false));
        list.add(new Notification(0, R.drawable.ic_travel, getString(R.string.travel_tours), false));
        list.add(new Notification(0, R.drawable.ic_entertainment, getString(R.string.entertainment), false));
        list.add(new Notification(0, R.drawable.ic_jewelry, getString(R.string.jewelry_exchange), false));
        list.add(new Notification(0, R.drawable.ic_sports, getString(R.string.sports_fitness), false));
        return list;
    }
}