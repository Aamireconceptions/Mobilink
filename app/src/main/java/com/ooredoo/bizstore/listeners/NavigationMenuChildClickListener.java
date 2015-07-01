package com.ooredoo.bizstore.listeners;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.widget.ExpandableListView;

import com.ooredoo.bizstore.adapters.ExpandableListAdapter;
import com.ooredoo.bizstore.ui.activities.AboutUsActivity;
import com.ooredoo.bizstore.ui.activities.HelpActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.MyAccountActivity;
import com.ooredoo.bizstore.ui.activities.NotificationsActivity;
import com.ooredoo.bizstore.utils.DialogUtils;

/**
 * Created by Babar on 01-Jul-15.
 */
public class NavigationMenuChildClickListener implements ExpandableListView.OnChildClickListener
{
    private HomeActivity activity;

    private ExpandableListAdapter adapter;

    public NavigationMenuChildClickListener(HomeActivity activity, ExpandableListAdapter adapter)
    {
        this.activity = activity;

        this.adapter = adapter;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
    {
        switch (groupPosition)
        {
            case 0:

                activity.selectTab( childPosition + 1);

                break;

            case 1:

                switch (childPosition)
                {
                    case 0:

                        startActivity(MyAccountActivity.class);

                        break;

                    case 1:

                        startActivity(NotificationsActivity.class);

                        break;

                    case 2:

                        DialogUtils.showRateAppDialog(activity);

                        break;

                    case 3:

                        startActivity(HelpActivity.class);

                        break;

                    case 4:

                        startActivity(AboutUsActivity.class);

                        break;

                    case 5:

                        DialogUtils.showUnSubscribeDialog(activity);
                }


                break;
        }

        activity.drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }

    private void startActivity(Class cls)
    {
        Intent intent = new Intent(activity, cls);

        activity.startActivity(intent);
    }
}
