package com.ooredoo.bizstore.ui.activities;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.activeandroid.query.Select;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.CitiesAdapter;
import com.ooredoo.bizstore.asynctasks.CityTask;
import com.ooredoo.bizstore.model.City;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.Logger;


import java.util.ArrayList;
import java.util.List;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.PREFIX_DEALS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.updateVal;

/**
 * Created by Babar on 21-Sep-15.
 */
public class CitySelectionActivity extends BaseActivity implements View.OnClickListener
{
    public List<City> cities = new ArrayList<>();

    private ListView mListView;
    private CitiesAdapter mAdapter;

    private final String[] citiesList = new String[] {"Islamabad", "Rawalpindi", "Lahore", "Karachi"};
    public CitySelectionActivity() {
        super();
        layoutResId = R.layout.activity_city_selection;
    }

    @Override
    public void init() {
        setupToolbar();
        mListView = (ListView) findViewById(R.id.lv_cities);

        initCitiesData();

        mAdapter = new CitiesAdapter(this, R.layout.list_item_notification, cities);
        mListView.setAdapter(mAdapter);

        RelativeLayout rlSelectAll = (RelativeLayout) findViewById(R.id.rl_select_all);
       // rlSelectAll.setOnClickListener(this);

        findViewById(R.id.save).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.rl_select_all) {
            CheckBox cbSelectAll = (CheckBox) findViewById(R.id.cb_select_all);
            boolean checkAll = !cbSelectAll.isChecked();
            cbSelectAll.setChecked(checkAll);
            for(City city : cities) {
                city.isChecked = checkAll;
                saveCity(city);
            }
            mAdapter.updateItems(cities);
            mAdapter.notifyDataSetChanged();
        }

        if(viewId == R.id.save)
        {
            String mCities = "";

            for(City city : cities)
            {
                Logger.print("Checked:" + city.isChecked);

                saveCity(city);

                if(city.isChecked)
                {
                    mCities = mCities + city.name.toLowerCase() + ",";
                }
            }

            Logger.print("mCities" + mCities);

            CityTask cityTask = new CityTask(this);
            cityTask.execute(mCities);

            clearCache(this);

           // HomeActivity.forceRefresh();

            /*setResult(RESULT_OK);

            finish();*/
        }
    }

    public static void clearCache(Activity activity) {

        for(String key : CategoryUtils.CACHE_KEYS) {
            updateVal(activity, key, "");
            updateVal(activity, key.concat("_UPDATE"), 0);
        }

        for(String category : CategoryUtils.categories) {
            final String KEY = PREFIX_DEALS.concat(category);
            final String UPDATE_KEY = KEY.concat("_UPDATE");
            updateVal(activity, KEY, "");
            updateVal(activity, UPDATE_KEY, 0);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.my_city);
    }

    public void initCitiesData() {
        /*cities = new ArrayList<>();
        cities.add(new City(1, true, R.drawable.ic_shopping, citiesList[0]));
        cities.add(new City(2, true, R.drawable.ic_shopping, citiesList[1]));
        cities.add(new City(3, true, R.drawable.ic_shopping, citiesList[2]));
        cities.add(new City(4, true, R.drawable.ic_shopping, citiesList[3]));*/

        cities = new Select().all().from(City.class).execute();

        if(cities == null || cities.size() == 0)
        {
            cities = new ArrayList<>();
            City isb = new City(1, true, R.drawable.ic_shopping, citiesList[0]);
            //saveCity(isb);
            cities.add(isb);

            City rwp = new City(2, true, R.drawable.ic_shopping, citiesList[1]);
            //saveCity(rwp);
            cities.add(rwp);

            City lhr = new City(3, true, R.drawable.ic_shopping, citiesList[2]);
            //saveCity(lhr);
            cities.add(lhr);

            City kar = new City(4, true, R.drawable.ic_shopping, citiesList[3]);
            //saveCity(kar);
            cities.add(kar);
        }
    }

    public void saveCity(City city) {
        if(city != null && city.id > 0) {
            List<City> cities = new Select().all().from(City.class).where("cityId=" + city.id).execute();

            if(cities == null || cities.size() == 0) {
                Log.i("SAVE", "NEW---" + city.toString());

                City c1 = new City(city.id, city.isChecked, city.resId, city.name);

                c1.save();
            } else {
                City c = cities.get(0);
                c.id = city.id;
                c.resId = city.resId;
                c.name = city.name;
                c.isChecked = city.isChecked;
                Log.i("UPDATE", "EXISTING---" + c.toString());
                c.save();
            }
        }
    }
}
