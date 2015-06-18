package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.NavigationItem;

import java.util.HashMap;
import java.util.List;

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
        tvName.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
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
        tvName.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);

        int color;
        if(name.equals(context.getString(R.string.electronics)))
        {
            color = context.getResources().getColor(R.color.grey);
        }
        else
        {
            color = context.getResources().getColor(R.color.white);
        }

        tvName.setBackgroundColor(color);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }
}