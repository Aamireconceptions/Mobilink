package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.listeners.CustomExpandableListViewOnChildClickListener;
import com.ooredoo.bizstore.model.NavigationItem;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.NavigationMenuUtils;
import com.ooredoo.bizstore.views.CustomExpandableListView;

import java.util.HashMap;
import java.util.List;

/**
 * @author  Babar
 * @since 12-Jun-15.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter
{
    private NavigationMenuUtils navigationMenuUtils;

    private Context context;

    private List<NavigationItem> groupList;

    private HashMap<String, List<NavigationItem>> childList;

    private View navigationHeader;

    private int direction;

    private ExpandableListView lastExpandableListView;

    private CustomExpandableListViewAdapter lastAdapter;

    public ExpandableListAdapter(NavigationMenuUtils navigationMenuUtils, Context context,
                                 List<NavigationItem> groupList,
                                 HashMap<String, List<NavigationItem>> childList, View navigationHeader)
    {
        this.navigationMenuUtils = navigationMenuUtils;

        this.context = context;

        this.groupList = groupList;

        this.childList = childList;

        this.navigationHeader = navigationHeader;

        direction = BizStore.getLanguage().equals("en") ? View.LAYOUT_DIRECTION_LTR : View.LAYOUT_DIRECTION_RTL;
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

        ImageView ivIndicator = (ImageView) convertView.findViewById(R.id.indicator);

        if(isExpanded) {
            ivIndicator.setImageResource(R.drawable.ic_group_collapse);
        } else {
            ivIndicator.setImageResource(R.drawable.ic_group_expand);
        }

        String name = navigationItem.getItemName();
        int resId = navigationItem.getResId();

        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        FontUtils.setFontWithStyle(context, tvName, Typeface.BOLD);
        tvName.setLayoutDirection(direction);
        tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(resId, 0, 0, 0);
        tvName.setText(name);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, final boolean isLastChild,
                             View convertView, final ViewGroup parent) {

        NavigationItem navigationItem1 = (NavigationItem) getChild(groupPosition, childPosition);

        String name1 = navigationItem1.getItemName();

        // Logger.print("getChildView: groupPos: " + groupPosition + ", childPos: " + childPosition + " " + name1);

        if((groupPosition == 0 && childPosition == 0 || childPosition == 7) || groupPosition == 1) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_navigation_child, parent, false);

            NavigationItem navigationItem = (NavigationItem) getChild(groupPosition, childPosition);

            String name = navigationItem.getItemName();
            int resId = navigationItem.getResId();

            TextView tvName = (TextView) convertView.findViewById(R.id.name);
            tvName.setLayoutDirection(direction);
            tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(resId, 0, 0, 0);
            tvName.setText(name);

            return convertView;
        } else {

            Logger.print("Positan: "+childPosition);
            NavigationItem navigationItem = navigationMenuUtils.subGroupList.get(childPosition);

            final CustomExpandableListViewAdapter adapter = new CustomExpandableListViewAdapter(context,
                    navigationItem,
                    navigationMenuUtils.subChildList);

            final CustomExpandableListViewOnChildClickListener onChildClickListener =
                    new CustomExpandableListViewOnChildClickListener(context,
                            adapter);

            final CustomExpandableListView customExpandableListView = new CustomExpandableListView(context,
                                                                                             navigationHeader);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                customExpandableListView.setDrawSelectorOnTop(true);
            }

            customExpandableListView.setGroupIndicator(null);
           // customExpandableListView.setSelector(R.color.transparent);
            customExpandableListView.setAdapter(adapter);
            customExpandableListView.setDivider(null);
            customExpandableListView.setOnChildClickListener(onChildClickListener);
            customExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView listView, View v, int groupPosition, long id) {

                    //Logger.print("onGroupClick: " + adapter.groupName);

                    adapter.setGroupExpanded();

                    onChildClickListener.groupName = adapter.groupName;

                    if (lastExpandableListView != null && lastExpandableListView != listView) {
                        lastExpandableListView.collapseGroup(0);

                        if (lastAdapter.groupExpanded) {
                            lastAdapter.setGroupExpanded();
                        }
                    }

                    lastExpandableListView = listView;

                    lastAdapter = adapter;

                    return false;
                }
            });

            return customExpandableListView;
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }
}