package com.ooredoo.bizstore.ui.fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ListViewBaseAdapter;
import com.ooredoo.bizstore.asynctasks.DealsTask;
import com.ooredoo.bizstore.interfaces.LocationChangeListener;
import com.ooredoo.bizstore.interfaces.OnDealsTaskFinishedListener;
import com.ooredoo.bizstore.interfaces.OnFilterChangeListener;
import com.ooredoo.bizstore.interfaces.OnSubCategorySelectedListener;
import com.ooredoo.bizstore.interfaces.ScrollToTop;
import com.ooredoo.bizstore.listeners.FabScrollListener;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.Logger;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ResourceUtils;
import com.ooredoo.bizstore.views.MultiSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.PREFIX_DEALS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.clearCache;

public class FoodAndDiningFragment extends Fragment implements OnFilterChangeListener,
                                                               OnDealsTaskFinishedListener,
                                                               OnSubCategorySelectedListener,
                                                               SwipeRefreshLayout.OnRefreshListener,
        ScrollToTop,
        LocationChangeListener{
    private HomeActivity activity;

    private ListViewBaseAdapter adapter;

    private ProgressBar progressBar;

    private ImageView ivBanner;

    private RelativeLayout rlHeader;

    private TextView tvEmptyView;

    private ListView listView;

    private boolean isRefreshed = false;

    private MultiSwipeRefreshLayout swipeRefreshLayout;

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    public static FoodAndDiningFragment newInstance() {
        FoodAndDiningFragment fragment = new FoodAndDiningFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BizStore bizStore = (BizStore) getActivity().getApplication();
        Tracker tracker = bizStore.getDefaultTracker();
        tracker.setScreenName("Food");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_listing, container, false);

        init(v, inflater);

        fetchAndDisplayFoodAndDining(progressBar);

        return v;
    }

    private RelativeLayout rlFilterTags;

    private TextView tvFilter;
    List<GenericDeal> deals;
    FilterOnClickListener clickListener;
    private void init(View v, LayoutInflater inflater)
    {

        activity = (HomeActivity) getActivity();

        swipeRefreshLayout = (MultiSwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.random, R.color.black);
        swipeRefreshLayout.setSwipeableChildrens(R.id.list_view, R.id.empty_view);
        swipeRefreshLayout.setOnRefreshListener(this);


        ivBanner = (ImageView) inflater.inflate(R.layout.image_view, null);

        FrameLayout flWrapper = (FrameLayout) inflater.inflate(R.layout.layout_filter_tags, null);

        rlFilterTags = (RelativeLayout) flWrapper.findViewById(R.id.tags_wrapper);

        tvFilter = (TextView) rlFilterTags.findViewById(R.id.filter);

        ImageView ivCloseFilerTag = (ImageView) rlFilterTags.findViewById(R.id.close);
        ivCloseFilerTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvFilter.setText("");

                rlFilterTags.setVisibility(View.GONE);
            }
        });

         clickListener = new FilterOnClickListener(activity, CategoryUtils.CT_FOOD);

        deals = new ArrayList<>();

        adapter = new ListViewBaseAdapter(activity, R.layout.list_deal_promotional, deals, this);
        adapter.setCategory(ResourceUtils.FOOD_AND_DINING);
        adapter.setListingType("deals");

        tvEmptyView = (TextView) v.findViewById(R.id.empty_view);

        listView = (ListView) v.findViewById(R.id.list_view); listView.setHeaderDividersEnabled(true);

        listView.addHeaderView(ivBanner);


        listView.setAdapter(adapter);
        listView.setOnScrollListener(new FabScrollListener(activity));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            listView.setNestedScrollingEnabled(true);
        }

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
    }

    DealsTask dealsTask;
    private void fetchAndDisplayFoodAndDining(ProgressBar progressBar) {

        if(tvEmptyView != null) {tvEmptyView.setVisibility(View.GONE);}
        dealsTask = new DealsTask(activity, adapter,
                                            progressBar, ivBanner,
                                            this);

        String cache = dealsTask.getCache("food");

        if(cache != null && !isRefreshed)
        {
            dealsTask.setData(cache);
        }
        else
        {
            dealsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "food");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.findItem(R.id.action_filter).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_filter)
        {
            clickListener.filter();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFilterChange()
    {
        isRefreshed = true;
        Logger.print("FoodAndDiningFragment onFilterChange");

        adapter.clearData();
        adapter.notifyDataSetChanged();

        dealsTask.cancel(true);

        if(DealsTask.sortColumn.equals("createdate"))
        {
            adapter.setListingType("deals");
        }
        else
        {
            adapter.setListingType("brands");
        }

        filterTagUpdate();

        fetchAndDisplayFoodAndDining(progressBar);

        isRefreshed = false;
    }

    @Override
    public void onRefresh()
    {
        if(adapter.deals != null && adapter.deals.size() > 0 && adapter.filterHeaderDeal != null)
        {
            adapter.filterHeaderDeal = null;
            adapter.deals.remove(0);
            adapter.notifyDataSetChanged();
        }

        if(adapter.brands != null && adapter.brands.size() > 0 && adapter.filterHeaderBrand != null)
        {
            adapter.filterHeaderBrand = null;
            adapter.brands.remove(0);
            adapter.notifyDataSetChanged();
        }

        diskCache.remove(adapter.deals);

        memoryCache.remove(adapter.deals);

        final String KEY = PREFIX_DEALS.concat("food");
        final String UPDATE_KEY = KEY.concat("_UPDATE");

        clearCache(activity, KEY);
        clearCache(activity, UPDATE_KEY);

        activity.resetFilters();

        CategoryUtils.showSubCategories(activity, CategoryUtils.CT_FOOD);

        isRefreshed = true;

        fetchAndDisplayFoodAndDining(null);

        isRefreshed = false;
    }

    @Override
    public void onRefreshCompleted() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onHaveDeals()
    {
        ivBanner.setImageResource(R.drawable.food_dinning_banner);

        tvEmptyView.setText("");
    }

    @Override
    public void onNoDeals(int stringResId) {
        ivBanner.setImageDrawable(null);

        tvEmptyView.setText(stringResId);
        listView.setEmptyView(tvEmptyView);

        adapter.filterHeaderDeal = null;
        adapter.filterHeaderBrand = null;
    }

    @Override
    public void onSubCategorySelected()
    {
        onFilterChange();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == AppCompatActivity.RESULT_OK)
        {
            boolean isFav = data.getBooleanExtra("is_fav", false);

            adapter.genericDeal.isFav = isFav;

            String voucher = data.getStringExtra("voucher");

            if(voucher != null)
            {
                adapter.genericDeal.voucher = voucher;
                adapter.genericDeal.status = "Available";
            }

            int views = data.getIntExtra("views", -1);

            adapter.genericDeal.views = views;

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void scroll() {
        listView.setSelection(0);
    }

    /**
     * This will setup the header needs to be displayed in ListViewAdapter
     * only if user has selected any filter from the right drawer layout.
     */
    public void filterTagUpdate()
    {
        String filter = "";

        if(activity.doApplyDiscount)
        {
            filter = "Discount: Highest to lowest, ";
        }

        if(activity.doApplyDistance)
        {
            filter = "Distance: Nearest first, ";
        }

        if(activity.doApplyRating)
        {
            filter += "Rating " + activity.ratingFilter +", ";
        }

        String categories = CategoryUtils.getSelectedSubCategoriesForTag(CategoryUtils.CT_FOOD);

        if(!categories.isEmpty())
        {
            filter += "Sub Categories: "+categories ;
        }

        if(! filter.isEmpty() && filter.charAt(filter.length() - 2) == ',')
        {
            filter = filter.substring(0, filter.length() - 2);
        }

        if(adapter.listingType.equals("deals"))
        {
            if(!filter.isEmpty())
            {
                adapter.subcategoryParent = CategoryUtils.CT_FOOD;
                adapter.filterHeaderDeal = new GenericDeal(true);
            }
            else
            {
                if(adapter.deals != null && adapter.deals.size() > 0 && adapter.filterHeaderDeal != null)
                {
                    adapter.filterHeaderDeal = null;
                    adapter.deals.remove(0);
                    adapter.notifyDataSetChanged();
                }

                if(adapter.filterHeaderDeal != null)
                {
                    adapter.filterHeaderDeal = null;
                }
            }
        }
        else
        {
            // This code will never get called as we are not showing brands
            if(!filter.isEmpty())
            {
                adapter.subcategoryParent = CategoryUtils.CT_FOOD;
                adapter.filterHeaderBrand = new Brand(true);
            }
            else
            {
                if(adapter.brands != null && adapter.brands.size() > 0 && adapter.filterHeaderBrand != null)
                {
                    adapter.filterHeaderBrand = null;
                    adapter.brands.remove(0);
                    adapter.notifyDataSetChanged();
                }

                if(adapter.filterHeaderBrand != null)
                {
                    adapter.filterHeaderBrand = null;
                }
            }
        }
    }

    @Override
    public void onLocationChanged() {
        if(tvEmptyView != null){tvEmptyView.setText("");}
        try {
            fetchAndDisplayFoodAndDining(null);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}