package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.GridViewBaseAdapter;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.listeners.DealGridOnItemClickListener;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ooredoo.bizstore.utils.CategoryUtils.CT_SHOPPING;
import static com.ooredoo.bizstore.utils.CategoryUtils.showSubCategories;

/**
 * @author Babar
 */
public class ShoppingFragment extends Fragment implements OnFilterChangeListener {
    private HomeActivity activity;

    private List<GenericDeal> deals;

    private GridViewBaseAdapter adapter;

    private ProgressBar progressBar;

    private SnackBarUtils snackBarUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shopping, container, false);

        init(v);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void init(View v) {
        activity = (HomeActivity) getActivity();

        snackBarUtils = new SnackBarUtils(activity, v);

        deals = new ArrayList<>();

        adapter = new GridViewBaseAdapter(activity, R.layout.grid_generic, deals);

        GridView gridView = (GridView) v.findViewById(R.id.shopping_gridview);
        gridView.setOnItemClickListener(new DealGridOnItemClickListener(activity, adapter));
        gridView.setAdapter(adapter);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        FilterOnClickListener clickListener = new FilterOnClickListener(activity, CT_SHOPPING);

        ImageView ivFilter = (ImageView) v.findViewById(R.id.filter);
        ivFilter.setOnClickListener(clickListener);

        Button btNewDeals = (Button) v.findViewById(R.id.new_deals);
        btNewDeals.setOnClickListener(clickListener);
        clickListener.setButtonSelected(btNewDeals);

        Button btPopularDeals = (Button) v.findViewById(R.id.popular_deals);
        btPopularDeals.setOnClickListener(clickListener);

        activity.findViewById(R.id.layout_sub_categories).setVisibility(View.VISIBLE);

        showSubCategories(activity, CT_SHOPPING);

        loadDeals();
    }

    private void loadDeals() {

        DealsTask shoppingTask = new DealsTask(activity, adapter, progressBar);
        shoppingTask.execute("shopping");
    }

    public static ShoppingFragment newInstance() {
        ShoppingFragment fragment = new ShoppingFragment();

        return fragment;
    }

    @Override
    public void onFilterChange() {
        loadDeals();
    }
}