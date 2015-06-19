package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.TopDealsAdapter;
import com.ooredoo.bizstore.listeners.DealsFilterClickListener;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class FoodAndDinningFragment extends Fragment {
    HomeActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_food_dinning, container, false);
        mActivity = (HomeActivity) getActivity();

        View header_filters = inflater.inflate(R.layout.header_deals, null);

        new DealsFilterClickListener(mActivity, header_filters);

        ListView listView = (ListView) v.findViewById(R.id.lv);

        listView.addHeaderView(header_filters);

        init(v);

        return v;
    }

    private void init(View v) {
        List<Deal> deals = new ArrayList<>();
        deals.add(new Deal());
        deals.add(new Deal());
        deals.add(new Deal());
        deals.add(new Deal());
        deals.add(new Deal());
        deals.add(new Deal());
        ListView listView = (ListView) v.findViewById(R.id.lv);
        TopDealsAdapter adapter = new TopDealsAdapter(mActivity, R.layout.list_item_food_n_dining, deals);
        listView.setAdapter(adapter);
    }

    public static FoodAndDinningFragment newInstance() {
        FoodAndDinningFragment fragment = new FoodAndDinningFragment();

        return fragment;
    }

}