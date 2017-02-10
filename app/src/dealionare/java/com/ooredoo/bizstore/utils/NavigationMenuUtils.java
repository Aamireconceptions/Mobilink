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
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.BuildConfig;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ExpandableListAdapter;
import com.ooredoo.bizstore.asynctasks.ProfilePicDownloadTask;
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
import static java.lang.String.valueOf;

/**
 * @author Babar
 * @since 12-Jun-15.
 */
public class NavigationMenuUtils implements ExpandableListView.OnGroupCollapseListener,
        ExpandableListView.OnGroupExpandListener, View.OnClickListener {
    private final String[] groupNames, categories, settings;
    private final int[] groupResIds, categoriesResIds, settingsResIds;
    public final String[] subCategories;
    public final String[] foodSubCategories, shoppingSubCategories, lifestyleSubCategories,
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

        categories = new String[] {activity.getString(R.string.top_deals),
                activity.getString(R.string.food_dining),
                activity.getString(R.string.shopping_speciality),
                activity.getString(R.string.lifestyle),
                activity.getString(R.string.entertainment)};

        categoriesResIds = new int[]{
                R.drawable.ic_top_deals,
                R.drawable.ic_food_dining,
                R.drawable.ic_shopping,
                R.drawable.ic_ladies,
                R.drawable.ic_entertainment};

       /* categories = new String[] { activity.getString(R.string.food_dining), activity.getString(R.string.shopping_speciality),
                activity.getString(R.string.electronics)};

        categoriesResIds = new int[]{ R.drawable.ic_food_dining,
                R.drawable.ic_shopping,
                R.drawable.ic_electronics};*/

        settings = new String[] {activity.getString(R.string.my_account),
                activity.getString(R.string.my_city),
                activity.getString(R.string.my_notifications),
                activity.getString(R.string.rate_us),
                activity.getString(R.string.help),
                activity.getString(R.string.about), activity.getString(R.string.un_subscribe)};

        settingsResIds = new int[]{R.drawable.ic_user,
                R.drawable.ic_business,
                R.drawable.ic_notification,
                R.drawable.ic_rate,
                R.drawable.ic_help,
                R.drawable.ic_about, R.drawable.ic_unsubscribe};

        subCategories = new String[] { activity.getString(R.string.top_deals),
                activity.getString(R.string.food_dining),
                activity.getString(R.string.shopping_speciality),
                activity.getString(R.string.lifestyle),
                activity.getString(R.string.entertainment)};

        subGroupResIds = new int[]{ R.drawable.ic_top_deals,
                R.drawable.ic_food_dining,
                R.drawable.ic_shopping,
                R.drawable.ic_ladies,
                R.drawable.ic_entertainment};

        foodSubCategories = new String[] {activity.getString(R.string.fast_food),
        activity.getString(R.string.restaurants), activity.getString(R.string.cafe)};

        shoppingSubCategories = new String[] {activity.getString(R.string.clothing),
        activity.getString(R.string.home_appliances), activity.getString(R.string.others)};

        lifestyleSubCategories = new String[] {activity.getString(R.string.spas_salons),
        activity.getString(R.string.fitness)};

        entertainmentSubCategories = new String[] { activity.getString(R.string.cinemas),
                activity.getString(R.string.others)};
    }

    public void setupNavigationMenu() {
        setIndicatorBounds(expandableListView);

        prepareNavigationMenuData();

        View navigationHeader = activity.getLayoutInflater().inflate(R.layout.layout_navigation_header, null);
        View navigationFooter = activity.getLayoutInflater().inflate(R.layout.layout_navigation_footer, null);

        setProfilePicture(navigationHeader);

        HomeActivity homeActivity = (HomeActivity) activity;

        NavigationMenuOnClickListener clickListener = new NavigationMenuOnClickListener(activity);

        LinearLayout llLangToggle = (LinearLayout) navigationHeader.findViewById(R.id.lang_toggle_layout);
        llLangToggle.setVisibility(View.GONE);


        TextView tvNumber = (TextView) navigationHeader.findViewById(R.id.number);
        tvNumber.setText(PhoneNumberUtils
                .formatNumber(BuildConfig.FLAVOR.equals("ooredoo")
                        ? "+974"
                        : "+92" + BizStore.username));

        Button btEnglish = (Button) navigationHeader.findViewById(R.id.btn_lang_english);
        btEnglish.setOnClickListener(clickListener);
        if(BizStore.getLanguage().equals("en")) clickListener.setSelected(btEnglish);
        FontUtils.setFont(activity, BizStore.DEFAULT_FONT, btEnglish);

        Button btArabic = (Button) navigationHeader.findViewById(R.id.btn_lang_arabic);
        btArabic.setOnClickListener(clickListener);
        if(BizStore.getLanguage().equals("ar")) clickListener.setSelected(btArabic);
        FontUtils.setFont(activity, BizStore.ARABIC_DEFAULT_FONT, btArabic);

        new HeaderNavigationListener(homeActivity, navigationHeader);

        ExpandableListAdapter adapter = new ExpandableListAdapter(this, activity, groupList, childList, navigationHeader);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            expandableListView.setDrawSelectorOnTop(true);
        }

        expandableListView.addHeaderView(navigationHeader);
        //expandableListView.addFooterView(navigationFooter);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupCollapseListener(this);
        expandableListView.setOnGroupExpandListener(this);
        expandableListView.setOnChildClickListener(new NavigationMenuChildClickListener(homeActivity, adapter));
    }

    public void setProfilePicture(View navigationHeader) {

        profilePicture = (ImageView) navigationHeader.findViewById(R.id.dp);

        profilePicture.setOnClickListener(this);

        Bitmap bitmap = MemoryCache.getInstance().getBitmapFromCache(PROFILE_PIC_URL);

        int width = (int) Converter.convertDpToPixels(600);
        int height = (int) Converter.convertDpToPixels(600);

        if(bitmap != null) {

            Logger.print("profile before: "+bitmap.getWidth()+", "+bitmap.getHeight());

           // BitmapProcessor bitmapProcessor = new BitmapProcessor();
           // bitmap = bitmapProcessor.downsizeBitmap(bitmap, width, height);

            //Logger.print("profile after: "+bitmap.getWidth()+", "+bitmap.getHeight());

            profilePicture.setImageBitmap(bitmap);
        } else {
           // ExecutorService executorService = Executors.newSingleThreadExecutor();
            //int width = (int) Converter.convertDpToPixels(100);
            //int height = width;
            ProgressBar progressBar = (ProgressBar) navigationHeader.findViewById(R.id.pbProfilePic);
            ProfilePicDownloadTask bitmapTask = new ProfilePicDownloadTask(profilePicture, progressBar);
            bitmapTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, PROFILE_PIC_URL, valueOf(width), valueOf(height));
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
            //expandableListView.setIndicatorBoundsRelative(start, end);
            expandableListView.setIndicatorBounds(start, end);
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

        List<NavigationItem> lifestyleSubList = new ArrayList<>();

        for(int i = 0; i < lifestyleSubCategories.length; i++)
        {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(lifestyleSubCategories[i]);
            navigationItem.setResId(getTreeNode(i, lifestyleSubCategories));

            lifestyleSubList.add(navigationItem);
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

        subChildList.put(subGroupList.get(3).getItemName(), lifestyleSubList);
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
            ((HomeActivity) activity).showHideDrawer(GravityCompat.START, false);
            activity.startActivity(new Intent(activity, MyAccountActivity.class));
        }
    }
}