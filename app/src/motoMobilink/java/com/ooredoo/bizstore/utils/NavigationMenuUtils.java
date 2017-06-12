package com.ooredoo.bizstore.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ExpandableListAdapter;
import com.ooredoo.bizstore.asynctasks.ProfilePicDownloadTask;
import com.ooredoo.bizstore.listeners.HeaderNavigationListener;
import com.ooredoo.bizstore.listeners.NavigationMenuChildClickListener;
import com.ooredoo.bizstore.model.NavigationItem;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.MyAccountActivity_;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ooredoo.bizstore.AppConstant.PROFILE_PIC_URL;
import static com.ooredoo.bizstore.ui.activities.HomeActivity.profilePicture;
import static java.lang.String.valueOf;

/** Helper class for setting up the left drawer menu
 * @author Babar
 * @since 12-Jun-15.
 */
public class NavigationMenuUtils implements ExpandableListView.OnGroupCollapseListener,
        ExpandableListView.OnGroupExpandListener, View.OnClickListener {
    private final String[] groupNames, categories, settings;
    private final int[] groupResIds, categoriesResIds, settingsResIds;
    public final String[] subCategories;
    public final String[] foodSubCategories, shoppingSubCategories, healthSubCategories,
                          entertainmentSubCategories;

    public final int[] subGroupResIds;
    private AppCompatActivity activity;
    private ExpandableListView expandableListView;

    // private int start, end;
    private List<NavigationItem> groupList;
    public List<NavigationItem> subGroupList;
    private HashMap<String, List<NavigationItem>> childList;
    public HashMap<String, List<NavigationItem>> subChildList;

    private int lastExpandedGroup = -1;

    public NavigationMenuUtils(AppCompatActivity activity, ExpandableListView expandableListView) {
        this.activity = activity;

        this.expandableListView = expandableListView;

        groupNames = new String[] { activity.getString(R.string.categories), activity.getString(R.string.settings) };

        groupResIds = new int[] { R.drawable.ic_categories, R.drawable.ic_settings };

        categories = new String[] {

                activity.getString(R.string.top_deals),
                activity.getString(R.string.food_dining),
                activity.getString(R.string.shopping_speciality),

                activity.getString(R.string.health_fitness),
                activity.getString(R.string.entertainment),
                activity.getString(R.string.new_arrivals),};

        categoriesResIds = new int[]{
                R.drawable.ic_top_deals,

                R.drawable.ic_food_dining,
                R.drawable.ic_shopping,
                R.drawable.ic_health,
                R.drawable.ic_entertainment,
                R.drawable.ic_new_deals,};

       /* categories = new String[] { activity.getString(R.string.food_dining), activity.getString(R.string.shopping_speciality),
                activity.getString(R.string.electronics)};

        categoriesResIds = new int[]{ R.drawable.ic_food_dining,
                R.drawable.ic_shopping,
                R.drawable.ic_electronics};*/

        settings = new String[] {activity.getString(R.string.my_account),
             //   activity.getString(R.string.my_city),
                activity.getString(R.string.my_notifications),
             //   activity.getString(R.string.rate_us),
                activity.getString(R.string.help),
                activity.getString(R.string.about),
                activity.getString(R.string.privacy_policy),
                activity.getString(R.string.un_subscribe)};

        settingsResIds = new int[]{
                R.drawable.ic_user,
              //  R.drawable.ic_business,
                R.drawable.ic_notification,
              //  R.drawable.ic_rate,
                R.drawable.ic_help,
                R.drawable.ic_about,
                R.drawable.ic_privacy,
                R.drawable.ic_unsubscribe};

        subCategories = new String[] {
                activity.getString(R.string.top_deals),


                activity.getString(R.string.food_dining),
                activity.getString(R.string.shopping_speciality),

                activity.getString(R.string.health_fitness),
                activity.getString(R.string.entertainment),
                activity.getString(R.string.new_arrivals)};

        subGroupResIds = new int[]{
                R.drawable.ic_top_deals,

                R.drawable.ic_food_dining,
                R.drawable.ic_shopping,
                R.drawable.ic_health,
                R.drawable.ic_entertainment,
                R.drawable.ic_new_deals};

        foodSubCategories = new String[] {activity.getString(R.string.pakistani),
        activity.getString(R.string.Fast_Food), activity.getString(R.string.Chinese),
        activity.getString(R.string.free_delivery)};

        shoppingSubCategories = new String[] {activity.getString(R.string.clothing),
        activity.getString(R.string.home_appliances), activity.getString(R.string.others)};

        healthSubCategories = new String[] {activity.getString(R.string.Spas),
        activity.getString(R.string.beauty_parlors), activity.getString(R.string.gyms),
        activity.getString(R.string.beauty_clinics)};

        entertainmentSubCategories = new String[] {
                activity.getString(R.string.travel),
                activity.getString(R.string.events),
                activity.getString(R.string.kids),
                activity.getString(R.string.other_activities)
        };
    }

    public void onResume()
    {
        if(!BizStore.username.isEmpty())
        {
            tvNumber.setVisibility(View.VISIBLE);
            tvNumber.setText(PhoneNumberUtils
                    .formatNumber("+92" + BizStore.username));

            headerNavigationListener.resume();

            adapter.notifyDataSetChanged();
        }
    }

    TextView tvNumber;
    HeaderNavigationListener headerNavigationListener;

    ExpandableListAdapter adapter;
    public void setupNavigationMenu() {
        setIndicatorBounds(expandableListView);

        prepareNavigationMenuData();

        View navigationHeader = activity.getLayoutInflater().inflate(R.layout.layout_navigation_header, null);

        setProfilePicture(navigationHeader);

        HomeActivity homeActivity = (HomeActivity) activity;

        tvNumber = (TextView) navigationHeader.findViewById(R.id.number);
        tvNumber.setText(PhoneNumberUtils
                .formatNumber("+92" + BizStore.username));

        headerNavigationListener = new HeaderNavigationListener(homeActivity, navigationHeader);

        adapter = new ExpandableListAdapter(this, activity, groupList, childList, navigationHeader);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            expandableListView.setDrawSelectorOnTop(true);
        }

        expandableListView.addHeaderView(navigationHeader);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupCollapseListener(this);
        expandableListView.setOnGroupExpandListener(this);
        expandableListView.setOnChildClickListener(new NavigationMenuChildClickListener(homeActivity));
    }

    public void setProfilePicture(View navigationHeader) {

        profilePicture = (ImageView) navigationHeader.findViewById(R.id.dp);

        profilePicture.setOnClickListener(this);

        Bitmap bitmap = MemoryCache.getInstance().getBitmapFromCache(PROFILE_PIC_URL);

        int width = (int) Converter.convertDpToPixels(300);
        int height = (int) Converter.convertDpToPixels(300);

        if(bitmap != null) {

            Logger.print("profile before: "+bitmap.getWidth()+", "+bitmap.getHeight());

            profilePicture.setImageBitmap(bitmap);
        } else {
            if(!BizStore.username.isEmpty()) {
                ProgressBar progressBar = (ProgressBar) navigationHeader.findViewById(R.id.pbProfilePic);
                ProfilePicDownloadTask bitmapTask = new ProfilePicDownloadTask(profilePicture, progressBar);
                bitmapTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, PROFILE_PIC_URL, valueOf(width), valueOf(height));
            }
        }
    }

    public void setIndicatorBounds(final ExpandableListView expandableListView) {
        expandableListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    expandableListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    expandableListView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                applyIndicatorBounds(expandableListView);
            }
        });
    }

    private void applyIndicatorBounds(ExpandableListView expandableListView) {
        int start = expandableListView.getWidth() - (int) Converter.convertDpToPixels(40);

        int end = expandableListView.getWidth();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expandableListView.setIndicatorBounds(start, end);
        } else {
            expandableListView.setIndicatorBounds(start, end);
        }
    }

    /**
     * fill up the data structure to be passed
     * to the expandableListView.
     */
    private void prepareNavigationMenuData() {
        groupList = new ArrayList<>();

        childList = new HashMap<>();

        for(int i = 0; i < groupNames.length; i++) {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(groupNames[i]);
            navigationItem.setResId(groupResIds[i]);

            groupList.add(navigationItem);
        }

        List<NavigationItem> categoriesList = new ArrayList<>();

        for(int i = 0; i < categories.length; i++) {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(categories[i]);
            navigationItem.setResId(categoriesResIds[i]);

            categoriesList.add(navigationItem);
        }

        List<NavigationItem> settingsList = new ArrayList<>();

        for(int i = 0; i < settings.length; i++) {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(settings[i]);
            navigationItem.setResId(settingsResIds[i]);

            settingsList.add(navigationItem);
        }

        childList.put(groupList.get(0).getItemName(), categoriesList);
        childList.put(groupList.get(1).getItemName(), settingsList);

        subGroupList = new ArrayList<>();

        for(int i = 0; i < subCategories.length ; i++)
        {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(subCategories[i]);
            navigationItem.setResId(subGroupResIds[i]);

            subGroupList.add(navigationItem);
        }

        List<NavigationItem> foodSubList = new ArrayList<>();

        for(int i = 0; i < foodSubCategories.length; i++)
        {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(foodSubCategories[i]);
            navigationItem.setResId(getTreeNode(i, foodSubCategories));

            foodSubList.add(navigationItem);
        }

        List<NavigationItem> shoppingSubList = new ArrayList<>();

        for(int i = 0; i < shoppingSubCategories.length; i++)
        {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(shoppingSubCategories[i]);
            navigationItem.setResId(getTreeNode(i, shoppingSubCategories));

            shoppingSubList.add(navigationItem);
        }

        List<NavigationItem> healthSubList = new ArrayList<>();

        for(int i = 0; i < healthSubCategories.length; i++)
        {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(healthSubCategories[i]);
            navigationItem.setResId(getTreeNode(i, healthSubCategories));

            healthSubList.add(navigationItem);
        }

        List<NavigationItem> entertainmentSubList = new ArrayList<>();

        for(int i = 0; i < entertainmentSubCategories.length; i++)
        {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(entertainmentSubCategories[i]);
            navigationItem.setResId(getTreeNode(i, entertainmentSubCategories));

            entertainmentSubList.add(navigationItem);
        }

        subChildList = new HashMap<>();
        subChildList.put(subGroupList.get(1).getItemName(), foodSubList);
        subChildList.put(subGroupList.get(2).getItemName(), shoppingSubList);

        subChildList.put(subGroupList.get(3).getItemName(), healthSubList);
        subChildList.put(subGroupList.get(4).getItemName(), entertainmentSubList);
    }

    private int getTreeNode(int index, String [] array)
    {

        int resId = index != array.length - 1
                ? BizStore.getLanguage().equals("en") ? R.drawable.node_start : R.drawable.node_start_flip
                :  BizStore.getLanguage().equals("en") ? R.drawable.node_end : R.drawable.node_end_flip;

        return  resId;
    }

    @Override
    public void onGroupCollapse(int groupPosition) {
        Logger.print(groupPosition + " Collapsed");
    }

    @Override
    public void onGroupExpand(int groupPosition)
    {
        if(lastExpandedGroup != -1 && lastExpandedGroup != groupPosition) {
            expandableListView.collapseGroup(lastExpandedGroup);
        }
        lastExpandedGroup = groupPosition;
        Logger.print(groupPosition + " Expanded");
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.dp) {

            if(!BizStore.username.isEmpty()) {
                ((HomeActivity) activity).showHideDrawer(GravityCompat.START, false);
                activity.startActivity(new Intent(activity, MyAccountActivity_.class));
            }
        }
    }
}