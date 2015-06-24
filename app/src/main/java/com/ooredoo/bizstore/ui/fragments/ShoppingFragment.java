package com.ooredoo.bizstore.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.GridViewBaseAdapter;
import com.ooredoo.bizstore.asynctasks.ShoppingTask;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.SnackBarUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Babar
 */
public class ShoppingFragment extends Fragment implements View.OnClickListener {
    private Activity activity;

    private List<GenericDeal> deals;

    private GridViewBaseAdapter adapter;

    private ProgressBar progressBar;

    private SnackBarUtils snackBarUtils;

    private View lastSelected;

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
        activity = getActivity();

        snackBarUtils = new SnackBarUtils(activity, v);

        deals = new ArrayList<>();

        adapter = new GridViewBaseAdapter(activity, R.layout.grid_generic, deals);

        GridView gridView = (GridView) v.findViewById(R.id.shopping_gridview);
        gridView.setAdapter(adapter);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        ImageView ivFilter = (ImageView) v.findViewById(R.id.filter);
        ivFilter.setOnClickListener(this);

        Button btNewDeals = (Button) v.findViewById(R.id.new_deals);
        btNewDeals.setOnClickListener(this);

        Button btPopularDeals = (Button) v.findViewById(R.id.popular_deals);
        btPopularDeals.setOnClickListener(this);

        btNewDeals.setSelected(true);

        lastSelected = btNewDeals;
    }

    private void loadDeals()
    {
        ShoppingTask shoppingTask = new ShoppingTask(adapter, progressBar, snackBarUtils);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.new_deals:

                setSelected(v);

                break;

            case R.id.popular_deals:

                setSelected(v);

                break;

            case R.id.filter:

                ((HomeActivity) activity).drawerLayout.openDrawer(GravityCompat.END);

                break;
        }
    }

    private void setSelected(View v)
    {
        if(lastSelected != null)
        {
            lastSelected.setSelected(false);
        }

        v.setSelected(true);

        lastSelected = v;
    }

    public static ShoppingFragment newInstance() {
        ShoppingFragment fragment = new ShoppingFragment();

        return fragment;
    }
}