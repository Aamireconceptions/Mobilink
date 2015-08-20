package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.interfaces.OnSubCategorySelectedListener;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.ResourceUtils;
import com.ooredoo.bizstore.views.MultiSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import static com.ooredoo.bizstore.utils.StringUtils.isNotNullOrEmpty;

public class SportsAndFitnessFragment extends Fragment implements OnFilterChangeListener,
                                                                  OnDealsTaskFinishedListener,
                                                                  OnSubCategorySelectedListener,
                                                                  SwipeRefreshLayout.OnRefreshListener {
    private HomeActivity activity;

    private ListViewBaseAdapter adapter;

    private ProgressBar progressBar;

    public static String subCategory;

    private ImageView ivBanner;

    private RelativeLayout rlHeader;

    private boolean isCreated = false;

    private TextView tvEmptyView;

    private ListView listView;

    private MultiSwipeRefreshLayout swipeRefreshLayout;

    public static SportsAndFitnessFragment newInstance()
    {
        SportsAndFitnessFragment fragment = new SportsAndFitnessFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_listing, container, false);

        init(v, inflater);

        fetchAndDisplaySportsAndFitness(progressBar);

        isCreated = true;

        return v;
    }

    private void init(View v, LayoutInflater inflater)
    {
        activity = (HomeActivity) getActivity();

        swipeRefreshLayout = (MultiSwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.random, R.color.black);
        swipeRefreshLayout.setSwipeableChildrens(R.id.list_view, R.id.empty_view);
        swipeRefreshLayout.setOnRefreshListener(this);

        /* ivBanner = (ImageView) v.findViewById(R.id.banner);

        rlHeader = (RelativeLayout) v.findViewById(R.id.header);*/

        ivBanner = (ImageView) inflater.inflate(R.layout.image_view, null);

        rlHeader = (RelativeLayout) inflater.inflate(R.layout.layout_filter_header, null);

        FilterOnClickListener clickListener = new FilterOnClickListener(activity, CategoryUtils.CT_SPORTS);
        clickListener.setLayout(rlHeader);

        List<GenericDeal> deals = new ArrayList<>();

        adapter = new ListViewBaseAdapter(activity, R.layout.list_deal_promotional, deals);
        adapter.setCategory(ResourceUtils.SPORTS_AND_FITNESS);

        tvEmptyView = (TextView) v.findViewById(R.id.empty_view);

        listView = (ListView) v.findViewById(R.id.list_view);
        listView.addHeaderView(ivBanner);
        listView.addHeaderView(rlHeader);
        //listView.setOnItemClickListener(new ListViewOnItemClickListener(activity));
        listView.setAdapter(adapter);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            listView.setNestedScrollingEnabled(true);
        }

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
    }

    private void fetchAndDisplaySportsAndFitness(ProgressBar progressBar)
    {
        DealsTask dealsTask = new DealsTask(activity, adapter, progressBar, ivBanner, this);

        if(isNotNullOrEmpty(subCategory)) {
            DealsTask.subCategories = subCategory;
            subCategory = ""; //Reset sub category filter.
        }

        dealsTask.execute("sports_fitness");
    }

    @Override
    public void onFilterChange()
    {
        fetchAndDisplaySportsAndFitness(progressBar);
    }

    @Override
    public void onRefresh() {
        fetchAndDisplaySportsAndFitness(null);
    }

    @Override
    public void onRefreshCompleted() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onHaveDeals() {
        ivBanner.setImageResource(R.drawable.sports_fitness_banner);

        rlHeader.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoDeals(int stringResId) {
        ivBanner.setImageDrawable(null);

        rlHeader.setVisibility(View.GONE);

        tvEmptyView.setText(stringResId);
        listView.setEmptyView(tvEmptyView);
    }

    @Override
    public void onSubCategorySelected()
    {
        if(!isCreated)
        {
            onFilterChange();
        }
        else
        {
            isCreated = false;
        }
    }
}