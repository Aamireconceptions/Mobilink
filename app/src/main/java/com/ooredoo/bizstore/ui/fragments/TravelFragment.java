package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.interfaces.OnRefreshListener;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.listeners.ScrollListener;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.FontUtils;
import com.ooredoo.bizstore.utils.ResourceUtils;
import com.ooredoo.bizstore.views.MultiSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class TravelFragment extends Fragment implements OnFilterChangeListener,
                                                        OnDealsTaskFinishedListener,
                                                        SwipeRefreshLayout.OnRefreshListener {
    private HomeActivity activity;

    private ListViewBaseAdapter adapter;

    private ProgressBar progressBar;

    private ImageView ivBanner;

    private RelativeLayout rlHeader;

    private TextView tvEmptyView;

    private ListView listView;

    private MultiSwipeRefreshLayout swipeRefreshLayout;

    public static TravelFragment newInstance()
    {
        TravelFragment fragment = new TravelFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_listing, container, false);

        init(v);

        fetchAndDisplayTravel();

        return v;
    }

    private void init(View v)
    {
        activity = (HomeActivity) getActivity();

        swipeRefreshLayout = (MultiSwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.random, R.color.black);
        swipeRefreshLayout.setSwipeableChildrens(R.id.list_view, R.id.empty_view);
        swipeRefreshLayout.setOnRefreshListener(this);

        ivBanner = (ImageView) v.findViewById(R.id.banner);

        rlHeader = (RelativeLayout) v.findViewById(R.id.header);

        CategoryUtils.showSubCategories(activity, CategoryUtils.CT_TRAVEL);

        FilterOnClickListener clickListener = new FilterOnClickListener(activity, CategoryUtils.CT_TRAVEL);

        Button btNewDeals = (Button) v.findViewById(R.id.new_deals);
        btNewDeals.setOnClickListener(clickListener);
        clickListener.setButtonSelected(btNewDeals);

        FontUtils.setFont(activity, BizStore.DEFAULT_FONT, btNewDeals);

        Button btPopularDeals = (Button) v.findViewById(R.id.popular_deals);
        btPopularDeals.setOnClickListener(clickListener);

        FontUtils.setFont(activity, BizStore.DEFAULT_FONT, btPopularDeals);

        ImageView ivFilter = (ImageView) v.findViewById(R.id.filter);
        ivFilter.setOnClickListener(clickListener);

        List<GenericDeal> deals = new ArrayList<>();

        adapter = new ListViewBaseAdapter(activity, R.layout.list_deal, deals);
        adapter.setCategory(ResourceUtils.TRAVEL_AND_TOUR);

        tvEmptyView = (TextView) v.findViewById(R.id.empty_view);

        listView = (ListView) v.findViewById(R.id.list_view);
        //listView.setOnItemClickListener(new ListViewOnItemClickListener(activity));
        listView.setAdapter(adapter);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
    }

    private void fetchAndDisplayTravel()
    {
        DealsTask dealsTask = new DealsTask(activity, adapter, progressBar, ivBanner, this);
        dealsTask.execute("travel");
    }

    @Override
    public void onFilterChange()
    {
        fetchAndDisplayTravel();
    }

    @Override
    public void onRefresh() {
        fetchAndDisplayTravel();
    }

    @Override
    public void onRefreshCompleted() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onHaveDeals() {
        ivBanner.setImageResource(R.drawable.travel_tour_banner);

        rlHeader.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNoDeals() {
        ivBanner.setImageDrawable(null);

        rlHeader.setVisibility(View.GONE);

        listView.setEmptyView(tvEmptyView);
    }
}