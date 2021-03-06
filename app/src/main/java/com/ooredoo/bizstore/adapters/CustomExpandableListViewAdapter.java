package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.NavigationItem;
import com.ooredoo.bizstore.utils.Logger;

import java.util.HashMap;
import java.util.List;

/**
 * @author Babar
 * @since 23-Jul-15.
 */
public class CustomExpandableListViewAdapter extends BaseExpandableListAdapter
{
    private Context context;

    private NavigationItem navigationItem;

   // private String subGroupItemName;

    private HashMap<String, List<NavigationItem>> subChildList;

    private int direction;

    public boolean groupExpanded = false;

    public CustomExpandableListViewAdapter(Context context,
                                    NavigationItem navigationItem,
                                    HashMap<String, List<NavigationItem>> subChildList)
    {
        this.context = context;

        this.navigationItem = navigationItem;

        this.subChildList = subChildList;

        direction = BizStore.getLanguage().equals("en") ? View.LAYOUT_DIRECTION_LTR : View.LAYOUT_DIRECTION_RTL;
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        List<NavigationItem> childList = this.subChildList.get(navigationItem.getItemName());

        Logger.print("childCount: "+childList.size());
        return childList.size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        /*return subGroupList.get(groupPosition);*/

        return navigationItem.getItemName();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        List<NavigationItem> childList = this.subChildList.get(navigationItem.getItemName());

        Logger.print("getChild:"+childList.get(childPosition).getItemName());
        return childList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public void setGroupExpanded()
    {
        if(groupExpanded)
        {
            Logger.print("Group was expanded");
        }
        else
        {
            Logger.print("Group was collapsed");
        }

        groupExpanded = !groupExpanded;

        if(groupExpanded)
        {
            Logger.print("Group is expanded");
        }
        else
        {
            Logger.print("Group is collapsed");
        }
    }

    public String groupName;
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_navigation_sub_group, parent, false);
        }

        /*NavigationItem navigationItem = (NavigationItem) getGroup(groupPosition);

        String name = navigationItem.getItemName();
        int resId = navigationItem.getResId();*/

        ImageView ivIndicator = (ImageView) convertView.findViewById(R.id.indicator);

        if(groupExpanded)
        {
            //convertView.setBackgroundResource(R.drawable.group_expanded_ripple);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.grey));

            ivIndicator.setImageResource(R.drawable.ic_group_expand);
        }
        else
        {
           //convertView.setBackgroundResource(R.drawable.group_collpased_ripple);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));

            if(BizStore.getLanguage().equals("en"))
            {
                ivIndicator.setImageResource(R.drawable.ic_group_fwd);
            }
            else
            {
                ivIndicator.setImageResource(R.drawable.ic_group_fwd_flip);
            }
        }

        groupName = (String) getGroup(groupPosition);

        int resId = navigationItem.getResId();

        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        tvName.setLayoutDirection(direction);
        tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(resId, 0, 0, 0);
        tvName.setText(groupName);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_navigation_sub_child, parent, false);
        }

        NavigationItem navigationItem = (NavigationItem) getChild(groupPosition, childPosition);

        String name = navigationItem.getItemName();
        int resId = navigationItem.getResId();
        Logger.print("Name: "+name);
        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        tvName.setLayoutDirection(direction);
        tvName.setCompoundDrawablesRelativeWithIntrinsicBounds(resId, 0, 0, 0);
        tvName.setText(name);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
