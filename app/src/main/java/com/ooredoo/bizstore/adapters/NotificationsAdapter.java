package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.Notification;

import java.util.List;

public class NotificationsAdapter extends ArrayAdapter<Notification> {

    Activity mActivity;
    int layoutResID;
    List<Notification> items;

    public NotificationsAdapter(Activity activity, int layoutResourceID, List<Notification> items) {
        super(activity, layoutResourceID, items);
        this.mActivity = activity;
        this.items = items;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Notification item = this.items.get(position);

        final Holder holder;
        View view = convertView;

        final LayoutInflater inflater = mActivity.getLayoutInflater();

        if(view == null) {
            holder = new Holder();
        } else {
            holder = (Holder) view.getTag();
        }

        if(view == null) {
            view = inflater.inflate(layoutResID, parent, false);

            holder.tvTitle = (TextView) view.findViewById(R.id.tv_title);
            holder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            holder.ivCategory = (ImageView) view.findViewById(R.id.iv_category);

            view.setTag(holder);
        }

        holder.tvTitle.setText(item.title);
        holder.checkBox.setChecked(item.enabled);
        holder.ivCategory.setImageResource(item.icon);

        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = holder.checkBox.isChecked();
                holder.checkBox.setChecked(!checked);
            }
        });

        holder.ivCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = holder.checkBox.isChecked();
                holder.checkBox.setChecked(!checked);
            }
        });

        return view;
    }

    public void updateItems(List<Notification> items) {
        this.items = items;
    }

    private static class Holder {
        TextView tvTitle;
        CheckBox checkBox;
        ImageView ivCategory;
    }
}