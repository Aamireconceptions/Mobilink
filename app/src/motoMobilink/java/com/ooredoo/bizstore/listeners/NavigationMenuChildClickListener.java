package com.ooredoo.bizstore.listeners;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.widget.ExpandableListView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.ui.activities.AboutUsActivity_;
import com.ooredoo.bizstore.ui.activities.HelpActivity_;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.MyAccountActivity_;
import com.ooredoo.bizstore.ui.activities.NotificationsActivity_;
import com.ooredoo.bizstore.utils.DialogUtils;

/**
 * @author Babar
 * @since 01-Jul-15.
 */
public class NavigationMenuChildClickListener implements ExpandableListView.OnChildClickListener {
    private HomeActivity activity;

    public NavigationMenuChildClickListener(HomeActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        switch(groupPosition) {
            case 0:

                activity.selectTab(childPosition + 1);

                break;

            case 1:

                switch(childPosition) {
                    case 0:

                        if(!BizStore.username.isEmpty())
                        startActivity(MyAccountActivity_.class);

                        break;

                   /* case 1:

                        activity.startActivityForResult(new Intent(activity, CitySelectionActivity.class),
                                101);

                        break;
*/
                    case 1:
                        startActivity(NotificationsActivity_.class);

                        break;

                    case 2:

                        startActivity(HelpActivity_.class);

                        break;

                    case 3:

                        startActivity(AboutUsActivity_.class);

                        break;

                    case 4:
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://econceptions.mobi/jazzdiscountbazarapp/privacypolicy"));
                        activity.startActivity(intent);
                        break;

                    case 5:

                        DialogUtils.showUnSubscribeDialog(activity);
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
}