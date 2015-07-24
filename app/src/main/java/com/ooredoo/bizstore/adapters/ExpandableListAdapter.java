package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.NavigationItem;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.Converter;
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

    private CustomExpandableListViewAdapter adapter;

    private CustomExpandableListView customExpandableListView;

    private int direction;

    public ExpandableListAdapter(NavigationMenuUtils navigationMenuUtils, Context context,
                                 List<NavigationItem> groupList,
                                 HashMap<String, List<NavigationItem>> childList)
    {
        this.navigationMenuUtils = navigationMenuUtils;

        this.context = context;

        this.groupList = groupList;

        this.childList = childList;

        adapter = new CustomExpandableListViewAdapter(context, null, navigationMenuUtils.subChildList);

        customExpandableListView = new CustomExpandableListView(context);
        //customExpandableListView.setAdapter(adapter);

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

        String name = navigationItem.getItemName();
        int resId = navigationItem.getResId();

        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        tvName.setLayoutDirection(direction);
        tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(resId, 0, 0, 0);
        tvName.setText(name);

        /*if(HomeActivity.rtl) {
            tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, resId, 0);
        } else {
            tvName.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
        }
        tvName.setTextDirection(HomeActivity.rtl ? View.TEXT_DIRECTION_RTL : View.TEXT_DIRECTION_LTR);
        tvName.setLayoutDirection(HomeActivity.rtl ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);*/
        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {

        Logger.print("getChildView: groupPos: "+groupPosition+ ", childPos: "+childPosition);
        /*if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_navigation_child, parent, false);
        }*/

       // ExpandableListView expandableListView = new ExpandableListView(context);

        if(groupPosition == 0 && childPosition != 0)
        {
            /*CustomExpandableListView expandableListView = (CustomExpandableListView) convertView;

            if(expandableListView == null)
            {
                expandableListView = new CustomExpandableListView(context);
            }*/

       /* ExpandableListAdapter adapter = new ExpandableListAdapter(navigationMenuUtils, context,
                                                                      navigationMenuUtils.subGroupList,
                                                                      navigationMenuUtils.subChildList);

            CustomExpandableListViewAdapter adapter = new CustomExpandableListViewAdapter(context,
                    navigationMenuUtils.subGroupList.get(childPosition).getItemName(),
                    navigationMenuUtils.subChildList);*/

           // expandableListView.setAdapter(adapter);

            NavigationItem navigationItem = navigationMenuUtils.subGroupList.get(childPosition);

            final CustomExpandableListViewAdapter adapter = new CustomExpandableListViewAdapter(context,
                    navigationItem,
                    navigationMenuUtils.subChildList);

            CustomExpandableListView customExpandableListView = new CustomExpandableListView(context);

            customExpandableListView.setGroupIndicator(null);
            //setIndicatorBounds(customExpandableListView);

            customExpandableListView.setAdapter(adapter);
            customExpandableListView.setDivider(null);
            customExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                    adapter.setGroupExpanded();

                    return false;
                }
            });

            //navigationMenuUtils.setIndicatorBounds(customExpandableListView);

            /*adapter.setSubGroupItemName(navigationItem.getItemName());
            expandableListView.setAdapter(adapter);*/

            return customExpandableListView;
        }
        else
        {
            /*if(convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = inflater.inflate(R.layout.list_navigation_child, parent, false);
            }*/

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
        }


        /*if((groupPosition == 0 && childPosition == 0) || groupPosition == 1)
        {
            NavigationItem navigationItem = (NavigationItem) getChild(groupPosition, childPosition);

            String name = navigationItem.getItemName();
            int resId = navigationItem.getResId();

            TextView tvName = (TextView) convertView.findViewById(R.id.name);

            tvName.setLayoutDirection(direction);
            tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(resId, 0, 0, 0);

            tvName.setText(name);

//        if(HomeActivity.rtl) {
//            tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, resId, 0);
//        } else {
//            tvName.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
//        }
//
//        tvName.setTextDirection(HomeActivity.rtl ? View.TEXT_DIRECTION_RTL : View.TEXT_DIRECTION_LTR);
//        tvName.setLayoutDirection(HomeActivity.rtl ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        }
        else
        {
            ExpandableListView expandableListView = new ExpandableListView(context);

            *//*ExpandableListAdapter adapter = new ExpandableListAdapter(navigationMenuUtils, context,
                                                                      navigationMenuUtils.subGroupList,
                                                                      navigationMenuUtils.subChildList);*//*

            CustomExpandableListViewAdapter adapter = new CustomExpandableListViewAdapter(context,
                                                          navigationMenuUtils.subGroupList,
                                                          navigationMenuUtils.subChildList);

            expandableListView.setAdapter(adapter);

            return expandableListView;
        }

        return convertView;*/
    }

    public void setIndicatorBounds(final CustomExpandableListView expandableListView) {
        expandableListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    expandableListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    expandableListView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                applyIndicatorBounds(expandableListView);
            }
        });
    }

    private void applyIndicatorBounds(CustomExpandableListView expandableListView) {
        int start = expandableListView.getWidth() - (int) Converter.convertDpToPixels(40);

        int end = expandableListView.getWidth();
//start = 400;
       // end = 450;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expandableListView.setIndicatorBoundsRelative(start, end);
        } else {
            expandableListView.setIndicatorBounds(start, end);
        }

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }
}