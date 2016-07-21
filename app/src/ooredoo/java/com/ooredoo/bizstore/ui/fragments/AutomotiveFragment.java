package com.ooredoo.bizstore.ui.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.ooredoo.bizstore.interfaces.ScrollToTop;
import com.ooredoo.bizstore.listeners.FabScrollListener;
import com.ooredoo.bizstore.listeners.FilterOnClickListener;
import com.ooredoo.bizstore.model.Brand;
import com.ooredoo.bizstore.model.GenericDeal;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.utils.CategoryUtils;
import com.ooredoo.bizstore.utils.DiskCache;
import com.ooredoo.bizstore.utils.MemoryCache;
import com.ooredoo.bizstore.utils.ResourceUtils;
import com.ooredoo.bizstore.views.MultiSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import static com.ooredoo.bizstore.utils.SharedPrefUtils.PREFIX_DEALS;
import static com.ooredoo.bizstore.utils.SharedPrefUtils.clearCache;

public class AutomotiveFragment extends Fragment implements OnFilterChangeListener,
                                                            OnDealsTaskFinishedListener,
                                                            OnSubCategorySelectedListener,
                                                            SwipeRefreshLayout.OnRefreshListener,
        ScrollToTop{
    private HomeActivity activity;

    private ListViewBaseAdapter adapter;

    private ProgressBar progressBar;

    private ImageView ivBanner;

    private RelativeLayout rlHeader;

    private TextView tvEmptyView;

    private ListView listView;

    private boolean isCreated = false;

    private MultiSwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefreshed = false;

    private MemoryCache memoryCache = MemoryCache.getInstance();

    private DiskCache diskCache = DiskCache.getInstance();

    public static AutomotiveFragment newInstance()
    {
        AutomotiveFragment fragment = new AutomotiveFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_listing, container, false);

        init(v, inflater);

        fetchAndDisplayAutomotive(progressBar);

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

        FilterOnClickListener clickListener = new FilterOnClickListener(activity, CategoryUtils.CT_AUTOMOTIVE);
        clickListener.setLayout(rlHeader);

        List<GenericDeal> deals = new ArrayList<>();

        adapter = new ListViewBaseAdapter(activity, R.layout.list_deal_promotional, deals, this);
        adapter.setCategory(ResourceUtils.AUTOMOTIVE);
        adapter.setListingType("deals");

        tvEmptyView = (TextView) v.findViewById(R.id.empty_view);

        listView = (ListView) v.findViewById(R.id.list_view);
        listView.addHeaderView(ivBanner);
        listView.addHeaderView(rlHeader);
        //listView.setOnItemClickListener(new ListViewOnItemClickListener(activity));
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new FabScrollListener(activity));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            listView.setNestedScrollingEnabled(true);
        }

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
    }

    DealsTask dealsTask;

    private void fetchAndDisplayAutomotive(ProgressBar progressBar)
    {
        tvEmptyView.setVisibility(View.GONE);

        dealsTask = new DealsTask(activity, adapter, progressBar, ivBanner, this);

        String cache = dealsTask.getCache("automotive");
//cache = null;
        if(cache != null && !isRefreshed)
        {
            dealsTask.setData(cache);
        }
        else
        {
            dealsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "automotive");
        }
    }

    @Override
    public void onFilterChange() {
        isRefreshed = true;
        adapter.clearData();
        adapter.notifyDataSetChanged();

        tvEmptyView.setText("");

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

        fetchAndDisplayAutomotive(progressBar);

        isRefreshed = false;
    }


    @Override
    public void onRefresh() {
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

        final String KEY = PREFIX_DEALS.concat("automotive");
        final String UPDATE_KEY = KEY.concat("_UPDATE");

        clearCache(activity, KEY);
        clearCache(activity, UPDATE_KEY);

        activity.resetFilters();

        CategoryUtils.showSubCategories(activity, CategoryUtils.CT_AUTOMOTIVE);

        isRefreshed = true;
        fetchAndDisplayAutomotive(null);
        isRefreshed = false;
    }

    @Override
    public void onRefreshCompleted() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onHaveDeals() {
        ivBanner.setImageResource(R.drawable.automotive_banner);

        rlHeader.setVisibility(View.VISIBLE);

        tvEmptyView.setText("");
    }

    @Override
    public void onNoDeals(int stringResId) {
        ivBanner.setImageDrawable(null);

        rlHeader.setVisibility(View.GONE);

        tvEmptyView.setText(stringResId);
        listView.setEmptyView(tvEmptyView);

        adapter.filterHeaderDeal = null;
        adapter.filterHeaderBrand = null;
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

    public void filterTagUpdate()
    {
        String filter = "";

        if(activity.doApplyDiscount)
        {
            filter = "Discount: Highest to lowest, ";
        }

        if(activity.doApplyRating)
        {
            filter += "Rating " + activity.ratingFilter +", ";
        }

        String categories = CategoryUtils.getSelectedSubCategoriesForTag(CategoryUtils.CT_AUTOMOTIVE);

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
                adapter.subcategoryParent = CategoryUtils.CT_AUTOMOTIVE;
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
            if(!filter.isEmpty())
            {
                adapter.subcategoryParent = CategoryUtils.CT_AUTOMOTIVE;
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
}