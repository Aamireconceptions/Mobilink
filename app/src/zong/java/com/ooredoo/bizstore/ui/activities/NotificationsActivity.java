package com.ooredoo.bizstore.ui.activities;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;

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

    RelativeLayout rlSelectAll;

    @Override
    public void init() {
        setupToolbar();
        mListView = (ListView) findViewById(R.id.lv_notifications);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mListView.setDrawSelectorOnTop(true);
        }

        initNotificationsData();

        mListView.setItemsCanFocus(true);

        mAdapter = new NotificationsAdapter(this, R.layout.list_item_notification, notifications);
        mListView.setAdapter(mAdapter);

        rlSelectAll = (RelativeLayout) findViewById(R.id.rl_select_all);
        rlSelectAll.setOnClickListener(this);

        cbSelectAll = (CheckBox) findViewById(R.id.cb_select_all);
        cbSelectAll.setChecked(true);
        List<Notification> n = new Select().all().from(Notification.class).execute();
        if(n != null && n.size() > 0) {
            for(Notification notification : n) {
                if (!notification.enabled) {
                    cbSelectAll.setChecked(false);
                }
            }

        }
    }

    CheckBox cbSelectAll;
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.rl_select_all) {

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
        actionBar.setTitle(R.string.my_notifications);
    }

    public void initNotificationsData() {
        notifications = new ArrayList<>();
        notifications.add(new Notification(1, true, R.drawable.ic_top_deals, getString(R.string.top_deals), "top_deals"));
        notifications.add(new Notification(2, true, R.drawable.ic_food_dining, getString(R.string.food_dining), "food"));
        notifications.add(new Notification(3, true, R.drawable.ic_shopping, getString(R.string.shopping), "shopping"));
       // notifications.add(new Notification(4, true, R.drawable.ic_electronics, getString(R.string.ladies_section), "ladies"));
        notifications.add(new Notification(5, true, R.drawable.ic_hotels, getString(R.string.health_fitness), "health"));
      //  notifications.add(new Notification(6, true, R.drawable.ic_malls, getString(R.string.education), "education"));
        notifications.add(new Notification(7, true, R.drawable.ic_automotive, getString(R.string.entertainment), "entertainment"));
        notifications.add(new Notification(8, true, R.drawable.ic_new_deals, getString(R.string.new_arrivals), "new_arrivals"));
    }

    public void saveNotification(Notification notification) {

       /* if(!notification.enabled)
        {
            cbSelectAll.setChecked(false);
        }*/

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
                n.category = notification.category;
                Log.i("UPDATE", "EXISTING---" + n.toString());
                n.save();
            }
        }

        cbSelectAll.setChecked(true);

        List<Notification> n = new Select().all().from(Notification.class).execute();
        if(n != null && n.size() > 0) {
            for(Notification notification1 : n) {
                if (!notification1.enabled) {
                    cbSelectAll.setChecked(false);
                }
            }

        }
    }
}