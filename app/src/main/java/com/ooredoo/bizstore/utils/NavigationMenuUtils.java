package com.ooredoo.bizstore.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ExpandableListAdapter;
import com.ooredoo.bizstore.asynctasks.BitmapDownloadTask;
import com.ooredoo.bizstore.listeners.HeaderNavigationListener;
import com.ooredoo.bizstore.listeners.NavigationMenuChildClickListener;
import com.ooredoo.bizstore.listeners.NavigationMenuOnClickListener;
import com.ooredoo.bizstore.model.NavigationItem;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.MyAccountActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ooredoo.bizstore.AppConstant.PROFILE_PIC_URL;
import static com.ooredoo.bizstore.ui.activities.HomeActivity.profilePicture;
import static com.ooredoo.bizstore.utils.Converter.convertDpToPixels;
import static java.lang.String.valueOf;

/**
 * @author Babar
 * @since 12-Jun-15.
 */
public class NavigationMenuUtils implements ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnGroupExpandListener, View.OnClickListener {
    private final String[] groupNames, categories, settings;
    private final int[] groupResIds, categoriesResIds, settingsResIds;
    private AppCompatActivity activity;
    private ExpandableListView expandableListView;

    // private int start, end;
    private List<NavigationItem> groupList;
    private HashMap<String, List<NavigationItem>> childList;

    public NavigationMenuUtils(AppCompatActivity activity, ExpandableListView expandableListView) {
        this.activity = activity;

        this.expandableListView = expandableListView;

        groupNames = new String[] { activity.getString(R.string.categories), activity.getString(R.string.settings) };

        groupResIds = new int[] { R.drawable.ic_categories, R.drawable.ic_settings };

        categories = new String[] { activity.getString(R.string.top_deals), activity.getString(R.string.food_dining), activity.getString(R.string.shopping_speciality),
                activity.getString(R.string.electronics), activity.getString(R.string.hotels_spa),
                activity.getString(R.string.markets_malls), activity.getString(R.string.automotive),
                activity.getString(R.string.travel_tours), activity.getString(R.string.entertainment),
                activity.getString(R.string.jewelry_exchange), activity.getString(R.string.sports_fitness)};

        categoriesResIds = new int[]{R.drawable.ic_top_deals, R.drawable.ic_food_dining,
                R.drawable.ic_shopping,
                R.drawable.ic_electronics, R.drawable.ic_hotels,
                R.drawable.ic_malls, R.drawable.ic_automotive,
                R.drawable.ic_travel, R.drawable.ic_entertainment,
                R.drawable.ic_jewellery, R.drawable.ic_sports};

        settings = new String[] {activity.getString(R.string.my_account), activity.getString(R.string.my_notifications),
                activity.getString(R.string.rate_us), activity.getString(R.string.help),
                activity.getString(R.string.about), activity.getString(R.string.un_subscribe)};

        settingsResIds = new int[]{R.drawable.ic_user, R.drawable.ic_notification,
                R.drawable.ic_rate, R.drawable.ic_help,
                R.drawable.ic_about, R.drawable.ic_unsubscribe};
    }

    public void setupNavigationMenu() {
        setIndicatorBounds();

        prepareNavigationMenuData();

        View navigationHeader = activity.getLayoutInflater().inflate(R.layout.layout_navigation_header, null);

        setProfilePicture(navigationHeader);

        HomeActivity homeActivity = (HomeActivity) activity;

        //TODO remove following comment to enable arabic/english version
        /*LanguageChangeListener languageChangeListener = new LanguageChangeListener(homeActivity, navigationHeader);
        languageChangeListener.expandableListView = expandableListView;*/

        NavigationMenuOnClickListener clickListener = new NavigationMenuOnClickListener(activity);

        Button btEnglish = (Button) navigationHeader.findViewById(R.id.btn_lang_english);
        btEnglish.setOnClickListener(clickListener);
        if(BizStore.getLanguage().equals("en")) clickListener.setSelected(btEnglish);

        Button btArabic = (Button) navigationHeader.findViewById(R.id.btn_lang_arabic);
        btArabic.setOnClickListener(clickListener);
        if(BizStore.getLanguage().equals("ar")) clickListener.setSelected(btArabic);

        new HeaderNavigationListener(homeActivity, navigationHeader);

        ExpandableListAdapter adapter = new ExpandableListAdapter(activity, groupList, childList);

        expandableListView.addHeaderView(navigationHeader);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupCollapseListener(this);
        expandableListView.setOnGroupExpandListener(this);
        expandableListView.setOnChildClickListener(new NavigationMenuChildClickListener(homeActivity, adapter));
    }

    public void setProfilePicture(View navigationHeader) {

        profilePicture = (ImageView) navigationHeader.findViewById(R.id.dp);

        profilePicture.setMaxHeight((int) Converter.convertDpToPixels(100));
        profilePicture.setOnClickListener(this);

        Bitmap bitmap = MemoryCache.getInstance().getBitmapFromCache(PROFILE_PIC_URL);

        if(bitmap != null) {
            profilePicture.setImageBitmap(bitmap);
        } else {
            int width = (int) convertDpToPixels(75);
            int height = width;
            ProgressBar progressBar = (ProgressBar) navigationHeader.findViewById(R.id.pbProfilePic);
            BitmapDownloadTask bitmapTask = new BitmapDownloadTask(profilePicture, progressBar);
            bitmapTask.execute(PROFILE_PIC_URL, valueOf(width), valueOf(height));
        }

        /*
        ProgressBar progressBar = (ProgressBar) navigationHeader.findViewById(R.id.progress_bar);

        MemoryCache memoryCache = MemoryCache.getInstance();

        String url = MyAccountActivity.PROFILE_PIC_URL;

        Bitmap bitmap = memoryCache.getBitmapFromCache(url);

        if(bitmap != null) {
            profilePicture.setImageBitmap(bitmap);
        } else {

            Resources resources = activity.getResources();

            int reqWidth = (int) Converter.convertDpToPixels(resources.getDimension(R.dimen._100sdp) / resources.getDisplayMetrics().density);
            int reqHeight = (int) Converter.convertDpToPixels(resources.getDimension(R.dimen._100sdp) / resources.getDisplayMetrics().density);

            Logger.print("req Width Pixels:" + reqWidth);
            Logger.print("req Height Pixels:" + reqHeight);

            BitmapDownloadTask bitmapDownloadTask = new BitmapDownloadTask(profilePicture, progressBar);
            bitmapDownloadTask.execute(url, String.valueOf(reqWidth), String.valueOf(reqHeight));
        }*/
    }

    private void setIndicatorBounds() {
        expandableListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    expandableListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    expandableListView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                applyIndicatorBounds();
            }
        });
    }

    private void applyIndicatorBounds() {
        int start = expandableListView.getWidth() - (int) Converter.convertDpToPixels(40);

        int end = expandableListView.getWidth();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expandableListView.setIndicatorBoundsRelative(start, end);
        } else {
            expandableListView.setIndicatorBounds(start, end);
        }
    }

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
    }

    @Override
    public void onGroupCollapse(int groupPosition) {
        Logger.print(groupPosition + " Collapsed");

    }

    @Override
    public void onGroupExpand(int groupPosition) {
        Logger.print(groupPosition + " Expanded");
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.dp) {
            ((HomeActivity) activity).showHideDrawer(GravityCompat.START, false);
            activity.startActivity(new Intent(activity, MyAccountActivity.class));
        }
    }
}
