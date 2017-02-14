package com.ooredoo.bizstore.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.model.City;


import java.util.List;

public class CitiesAdapter extends ArrayAdapter<City> {

    Activity mActivity;
    int layoutResID;
    List<City> cities;

    public CitiesAdapter(Activity activity, int layoutResourceID, List<City> cities) {
        super(activity, layoutResourceID, cities);
        this.mActivity = activity;
        this.cities = cities;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public City getItem(int position) {

        List<City> cities2 = new Select().all().from(City.class).where("cityId=" + cities.get(position).id).execute();

        return cities2.size() > 0 ? cities2.get(0) : cities.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final City city = getItem(position);

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

        holder.checkBox.setOnCheckedChangeListener(new CheckBoxChangeListener(position, city));

        holder.tvTitle.setText(city.name);
        holder.checkBox.setChecked(city.isChecked);

        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCheckBox(holder.checkBox, city);
            }
        });

        holder.ivCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCheckBox(holder.checkBox, city);
            }
        });

        return view;
    }

    private void toggleCheckBox(CheckBox checkBox, City city) {
        boolean isChecked = checkBox.isChecked();
        checkBox.setChecked(!isChecked);
        city.isChecked = !isChecked;
    }

    public void updateItems(List<City> cities) {
        this.cities = cities;
    }

    private class CheckBoxChangeListener implements CompoundButton.OnCheckedChangeListener {
        int position;
        City city;

        public CheckBoxChangeListener(int position, City city) {
            this.position = position;
            this.city = city;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            city.isChecked = isChecked;
            cities.get(position).isChecked = isChecked;
        }
    }

    private static class Holder {
        TextView tvTitle;
        CheckBox checkBox;
        ImageView ivCategory;
    }
}