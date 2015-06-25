package com.ooredoo.bizstore.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Question;

import java.util.List;
import java.util.Map;

/**
 * @author Pehlaj Rai
 * @since 24-Jun-15.
 */
public class ExpandableHelpListAdapter extends BaseExpandableListAdapter {
    private Context context;

    private List<String> groupList;

    private Map<String, List<Question>> childList;

    public ExpandableHelpListAdapter(Context context, List<String> groupList, Map<String, List<Question>> childList) {
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
        List<Question> childList = this.childList.get(groupList.get(groupPosition));

        return childList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<Question> childList = this.childList.get(groupList.get(groupPosition));

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

            convertView = inflater.inflate(R.layout.list_help_group, parent, false);
        }

        String groupName = getGroup(groupPosition).toString();

        TextView tvName = (TextView) convertView.findViewById(R.id.name);
        tvName.setText(groupName);

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_help_child, parent, false);
        }

        //TODO remove following comment once actual help question/answer list get populated.

        /*Question item = (Question) getChild(groupPosition, childPosition);

        TextView tvAnswer = (TextView) convertView.findViewById(R.id.tv_answer);
        TextView tvQuestion = (TextView) convertView.findViewById(R.id.tv_question);

        tvAnswer.setText(item.getAnswer());
        tvQuestion.setText(item.getQuestion());*/

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}