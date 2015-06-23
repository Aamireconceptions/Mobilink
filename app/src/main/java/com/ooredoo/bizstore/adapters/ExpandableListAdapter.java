package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.NavigationItem;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.util.HashMap;
import java.util.List;

import static com.ooredoo.bizstore.utils.DialogUtils.showUnSubscribeDialog;

/**
 * @author  Babar
 * @since 12-Jun-15.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter
{
    private Context context;

    private List<NavigationItem> groupList;

    private HashMap<String, List<NavigationItem>> childList;

    public ExpandableListAdapter(Context context, List<NavigationItem> groupList,
                                 HashMap<String, List<NavigationItem>> childList)
    {
        this.context = context;

        this.groupList = groupList;

        this.childList = childList;
    }

    @Override
    public int getGroupCount()
    {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        List<NavigationItem> childList = this.childList.get(groupList.get(groupPosition).getItemName());

        return childList.size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        List<NavigationItem> childList = this.childList.get(groupList.get(groupPosition).getItemName());

        NavigationItem navigationItem = childList.get(childPosition);

        return navigationItem;
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_navigation_group, parent, false);
        }

        NavigationItem navigationItem = (NavigationItem) getGroup(groupPosition);

        String name = navigationItem.getItemName();
        int resId = navigationItem.getResId();

        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        tvName.setText(name);
        if(HomeActivity.rtl) {
            tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, resId, 0);
        } else {
            tvName.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
        }
        tvName.setTextDirection(HomeActivity.rtl ? View.TEXT_DIRECTION_RTL : View.TEXT_DIRECTION_LTR);
        tvName.setLayoutDirection(HomeActivity.rtl ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_navigation_child, parent, false);
        }

        NavigationItem navigationItem = (NavigationItem) getChild(groupPosition, childPosition);

        String name = navigationItem.getItemName();
        int resId = navigationItem.getResId();

        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        tvName.setText(name);
        if(HomeActivity.rtl) {
            tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, resId, 0);
        } else {
            tvName.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
        }

        tvName.setTextDirection(HomeActivity.rtl ? View.TEXT_DIRECTION_RTL : View.TEXT_DIRECTION_LTR);
        tvName.setLayoutDirection(HomeActivity.rtl ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.print("NAVIGATION: " + groupPosition + ", " + childPosition);
                if(groupPosition == 0) {
                    HomeActivity homeActivity = (HomeActivity) context;
                    homeActivity.showHideDrawer(GravityCompat.START, false);
                    homeActivity.selectTab(childPosition + 1); //Skip Home Tab
                } else if(groupPosition == 1) {
                    switch(childPosition) {
                        case 0:
                            //TODO show 'My Account' screen
                            break;
                        case 1:
                            //TODO show 'My Notifications' screen
                            break;
                        case 2:
                            //TODO show 'Rate Us On Playstore' dialog
                            break;
                        case 3:
                            //TODO show 'Help' screen
                            break;
                        case 4:
                            //TODO show 'About' screen
                            break;
                        case 5:
                            showUnSubscribeDialog((Activity) context);
                            break;
                    }
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }
}