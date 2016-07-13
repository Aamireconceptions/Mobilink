package com.ooredoo.bizstore.listeners;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.widget.ExpandableListView;

import com.ooredoo.bizstore.adapters.ExpandableListAdapter;
import com.ooredoo.bizstore.ui.activities.AboutUsActivity;
import com.ooredoo.bizstore.ui.activities.HelpActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.MainActivity;
import com.ooredoo.bizstore.ui.activities.MyAccountActivity;
import com.ooredoo.bizstore.ui.activities.NotificationsActivity;
import com.ooredoo.bizstore.utils.DialogUtils;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.LOGIN_STATUS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;

/**
 * @author Babar
 * @since 01-Jul-15.
 */
public class NavigationMenuChildClickListener implements ExpandableListView.OnChildClickListener {
    private HomeActivity activity;

    private ExpandableListAdapter adapter;

    public NavigationMenuChildClickListener(HomeActivity activity, ExpandableListAdapter adapter) {
        this.activity = activity;

        this.adapter = adapter;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        switch(groupPosition) {
            case 0:

                activity.selectTab(childPosition + 2);

                break;

            case 1:

                switch(childPosition) {
                    case 0:

                        startActivity(MyAccountActivity.class);

                        break;

                    case 1:

                        startActivity(NotificationsActivity.class);

                        break;

                    case 2:
                        rateUsOnPlayStore();

                        break;

                    case 3:

                        startActivity(HelpActivity.class);

                        break;

                    case 4:

                        startActivity(AboutUsActivity.class);

                        break;

                    case 5:

                        DialogUtils.showUnSubscribeDialog(activity);

                        break;

                    case 6:

                        updateVal(activity, LOGIN_STATUS, false);

                        activity.finish();

                        activity.startActivity(new Intent(activity, MainActivity.class));

                        break;
                }

                break;
        }

        activity.drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }

    private void startActivity(Class cls) {
        Intent intent = new Intent(activity, cls);

        activity.startActivity(intent);
    }

    private void rateUsOnPlayStore() {
        Intent i = new Intent(Intent.ACTION_VIEW);

        i.setData(Uri.parse("market://details?id="+activity.getPackageName()));//TODO replace package name -> + activity.getPackageName()));
        activity.startActivity(i);
    }
}
