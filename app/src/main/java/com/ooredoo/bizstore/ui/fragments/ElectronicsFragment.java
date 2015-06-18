package com.ooredoo.bizstore.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.TopDealsAdapter;
import com.ooredoo.bizstore.model.Deal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public class ElectronicsFragment extends Fragment implements View.OnClickListener {
    HomeActivity mActivity;

    Button btnNewDeals, btnPopularDeals;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_food_dinning, container, false);
        mActivity = (HomeActivity) getActivity();

        View header_filters = inflater.inflate(R.layout.header_deals, null);

        ListView listView = (ListView) v.findViewById(R.id.lv);

        btnNewDeals = (Button) header_filters.findViewById(R.id.btn_new_deals);
        btnPopularDeals = (Button) header_filters.findViewById(R.id.btn_popular_deals);

        btnNewDeals.setOnClickListener(this);
        btnPopularDeals.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_new_deals || id == R.id.btn_popular_deals) {
            btnNewDeals.setTextColor(id == R.id.btn_new_deals ? ColorUtils.WHITE : ColorUtils.BLACK);
            btnPopularDeals.setTextColor(id == R.id.btn_new_deals ? ColorUtils.BLACK : ColorUtils.WHITE);
            btnNewDeals.setBackgroundResource(id == R.id.btn_new_deals ? R.drawable.btn_red1 : R.drawable.btn_lt_grey1);
            btnPopularDeals.setBackgroundResource(id == R.id.btn_new_deals ? R.drawable.btn_lt_grey2 : R.drawable.btn_red2);
        }
    }

    public static ElectronicsFragment newInstance() {
        ElectronicsFragment fragment = new ElectronicsFragment();

        return fragment;
    }

}