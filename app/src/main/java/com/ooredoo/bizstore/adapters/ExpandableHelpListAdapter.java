package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Question;
import com.ooredoo.bizstore.ui.activities.AboutUsActivity;
import com.ooredoo.bizstore.ui.activities.HelpActivity;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.MyAccountActivity;
import com.ooredoo.bizstore.ui.activities.NotificationsActivity;
import com.ooredoo.bizstore.utils.Logger;

import java.util.HashMap;
import java.util.List;

import static com.ooredoo.bizstore.utils.DialogUtils.showRateAppDialog;
import static com.ooredoo.bizstore.utils.DialogUtils.showUnSubscribeDialog;

/**
 * @author Pehlaj Rai
 * @since 24-Jun-15.
 */
public class ExpandableHelpListAdapter extends BaseExpandableListAdapter {
    private Context context;

    private List<Question> groupList;

    private HashMap<String, List<Question>> childList;

    public ExpandableHelpListAdapter(Context context, List<Question> groupList, HashMap<String, List<Question>> childList) {
        this.context = context;

        this.groupList = groupList;

        this.childList = childList;
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<Question> childList = this.childList.get(groupList.get(groupPosition).getQuestion());

        return childList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<Question> childList = this.childList.get(groupList.get(groupPosition).getQuestion());

        Question item = childList.get(childPosition);

        Log.i("QUESTION", item.getQuestion());
        return item;
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

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_navigation_group, parent, false);
        }

        Question item = (Question) getGroup(groupPosition);

        String name = item.getQuestion();

        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        tvName.setText(name);

        tvName.setTextDirection(HomeActivity.rtl ? View.TEXT_DIRECTION_RTL : View.TEXT_DIRECTION_LTR);
        tvName.setLayoutDirection(HomeActivity.rtl ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_navigation_child, parent, false);
        }

        Question item = (Question) getChild(groupPosition, childPosition);

        String name = item.getAnswer();

        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        tvName.setText(name);

        tvName.setTextDirection(HomeActivity.rtl ? View.TEXT_DIRECTION_RTL : View.TEXT_DIRECTION_LTR);
        tvName.setLayoutDirection(HomeActivity.rtl ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);

        final HomeActivity homeActivity = (HomeActivity) context;

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeActivity.showHideDrawer(GravityCompat.START, false);
                Logger.print("NAVIGATION: " + groupPosition + ", " + childPosition);
                if(groupPosition == 0) {
                    homeActivity.selectTab(childPosition + 1); //Skip Home Tab
                } else if(groupPosition == 1) {
                    switch(childPosition) {
                        case 0:
                            showActivity(MyAccountActivity.class);
                            break;
                        case 1:
                            //TODO show 'My Notifications' screen
                            showActivity(NotificationsActivity.class);
                            break;
                        case 2:
                            showRateAppDialog(homeActivity);
                            break;
                        case 3:
                            showActivity(HelpActivity.class);
                            break;
                        case 4:
                            showActivity(AboutUsActivity.class);
                            break;
                        case 5:
                            showUnSubscribeDialog(homeActivity);
                            break;
                    }
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private void showActivity(Class cls) {
        final HomeActivity homeActivity = (HomeActivity) context;
        homeActivity.startActivity(new Intent(context, cls));
    }
}