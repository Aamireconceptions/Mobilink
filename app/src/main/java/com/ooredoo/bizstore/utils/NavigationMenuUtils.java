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

import com.ooredoo.bizstore.AppConstant;
import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.adapters.ExpandableListAdapter;
import com.ooredoo.bizstore.listeners.HeaderNavigationListener;
import com.ooredoo.bizstore.listeners.NavigationMenuChildClickListener;
import com.ooredoo.bizstore.listeners.NavigationMenuOnClickListener;
import com.ooredoo.bizstore.model.NavigationItem;
import com.ooredoo.bizstore.model.User;
import com.ooredoo.bizstore.ui.activities.HomeActivity;
import com.ooredoo.bizstore.ui.activities.MyAccountActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ooredoo.bizstore.ui.activities.HomeActivity.profilePicture;

/**
 * @author Babar
 * @since 12-Jun-15.
 */
public class NavigationMenuUtils implements ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnGroupExpandListener, View.OnClickListener {
    private final String[] groupNames, categories, settings;
    private final int[] groupResIds, categoriesResIds, settingsResIds;
    public final String[] subCategories;
    public final String[] foodSubCategories, shoppingSubCategories, electronicsSubCategories,
                          hotelsSubCategories, mallsSubCategories, automotiveSubCategories,
                          travelSubCategories, entertainmentSubCategories, jewellerySubCategories,
                          sportsSubCategories;
    public final int[] subGroupResIds;
    private AppCompatActivity activity;
    private ExpandableListView expandableListView;

    // private int start, end;
    private List<NavigationItem> groupList;
    public List<NavigationItem> subGroupList;
    private HashMap<String, List<NavigationItem>> childList;
    public HashMap<String, List<NavigationItem>> subChildList;

    public NavigationMenuUtils(AppCompatActivity activity, ExpandableListView expandableListView) {
        this.activity = activity;

        this.expandableListView = expandableListView;

        groupNames = new String[] { activity.getString(R.string.categories), activity.getString(R.string.settings) };

        groupResIds = new int[] { R.drawable.ic_categories, R.drawable.ic_settings };

        categories = new String[] {activity.getString(R.string.top_deals),
                activity.getString(R.string.food_dining),
                activity.getString(R.string.shopping_speciality),
                activity.getString(R.string.electronics), activity.getString(R.string.hotels_spa),
                activity.getString(R.string.markets_malls), activity.getString(R.string.automotive),
                activity.getString(R.string.travel_tours), activity.getString(R.string.entertainment),
                activity.getString(R.string.jewelry_exchange), activity.getString(R.string.sports_fitness)};

        categoriesResIds = new int[]{ R.drawable.ic_top_deals,
                R.drawable.ic_top_deals, R.drawable.ic_food_dining,
                R.drawable.ic_shopping,
                R.drawable.ic_electronics, R.drawable.ic_hotels,
                R.drawable.ic_malls, R.drawable.ic_automotive,
                R.drawable.ic_travel, R.drawable.ic_entertainment,
                R.drawable.ic_jewellery, R.drawable.ic_sports};

       /* categories = new String[] { activity.getString(R.string.food_dining), activity.getString(R.string.shopping_speciality),
                activity.getString(R.string.electronics)};

        categoriesResIds = new int[]{ R.drawable.ic_food_dining,
                R.drawable.ic_shopping,
                R.drawable.ic_electronics};*/

        settings = new String[] {activity.getString(R.string.my_account), activity.getString(R.string.my_notifications),
                activity.getString(R.string.rate_us), activity.getString(R.string.help),
                activity.getString(R.string.about), activity.getString(R.string.un_subscribe)};

        settingsResIds = new int[]{R.drawable.ic_user, R.drawable.ic_notification,
                R.drawable.ic_rate, R.drawable.ic_help,
                R.drawable.ic_about, R.drawable.ic_unsubscribe};

        subCategories = new String[] { activity.getString(R.string.top_deals),
                activity.getString(R.string.food_dining),
                activity.getString(R.string.shopping_speciality),
                activity.getString(R.string.electronics), activity.getString(R.string.hotels_spa),
                activity.getString(R.string.markets_malls), activity.getString(R.string.automotive),
                activity.getString(R.string.travel_tours), activity.getString(R.string.entertainment),
                activity.getString(R.string.jewelry_exchange), activity.getString(R.string.sports_fitness)};

        subGroupResIds = new int[]{ R.drawable.ic_top_deals,
                R.drawable.ic_food_dining,
                R.drawable.ic_shopping,
                R.drawable.ic_electronics, R.drawable.ic_hotels,
                R.drawable.ic_malls, R.drawable.ic_automotive,
                R.drawable.ic_travel, R.drawable.ic_entertainment,
                R.drawable.ic_jewellery, R.drawable.ic_sports};

        foodSubCategories = new String[] {"Food_sub1", "Food_sub2", "Food_sub3", "food_sub4"};

        shoppingSubCategories = new String[] {"Shopping_sub1", "Shopping_sub2"};

        electronicsSubCategories = new String[] {"Electronics_sub1"};

        hotelsSubCategories = new String[] {"hotels_sub1"};

        mallsSubCategories = new String[] {"malls_sub1"};

        automotiveSubCategories = new String[] {"automotive_sub1"};

        travelSubCategories = new String[] {"travel_sub1"};

        entertainmentSubCategories = new String[] {"entertainment_sub1"};

        jewellerySubCategories = new String[] {"jewellery_sub1"};

        sportsSubCategories = new String[] {"sports_sub1"};
    }

    public void setupNavigationMenu() {
        setIndicatorBounds(expandableListView);

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

        ExpandableListAdapter adapter = new ExpandableListAdapter(this, activity, groupList, childList);

        expandableListView.addHeaderView(navigationHeader);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupCollapseListener(this);
        expandableListView.setOnGroupExpandListener(this);
        expandableListView.setOnChildClickListener(new NavigationMenuChildClickListener(homeActivity, adapter));
    }

    public void setProfilePicture(View navigationHeader) {

        profilePicture = (ImageView) navigationHeader.findViewById(R.id.dp);

        //profilePicture.setMaxHeight((int) Converter.convertDpToPixels(100));
        profilePicture.setOnClickListener(this);

       /* Bitmap bitmap = MemoryCache.getInstance().getBitmapFromCache(PROFILE_PIC_URL);

        if(bitmap != null) {
            profilePicture.setImageBitmap(bitmap);
        } else {
            int width = (int) convertDpToPixels(160);
            int height = width;
            ProgressBar progressBar = (ProgressBar) navigationHeader.findViewById(R.id.pbProfilePic);
            BitmapDownloadTask bitmapTask = new BitmapDownloadTask(profilePicture, progressBar);
            bitmapTask.execute(PROFILE_PIC_URL, valueOf(width), valueOf(height));
        }*/

        if(User.dp != null)
        {
            profilePicture.setImageBitmap(User.dp);
        }
        else
        {
            int reqWidth = (int) (Converter.convertDpToPixels(activity.getResources().getDimension(R.dimen._75sdp))
                            / activity.getResources().getDisplayMetrics().density);

            int reqHeight = (int) (Converter.convertDpToPixels(activity.getResources().getDimension(R.dimen._75sdp))
                    / activity.getResources().getDisplayMetrics().density);

            Bitmap bitmap = null;
            try
            {
                bitmap = new BitmapProcessor(null).decodeSampledBitmapFromFile(AppConstant.PROFILE_PIC_URL,
                                                                            reqWidth, reqHeight);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(bitmap != null)
            profilePicture.setImageBitmap(bitmap);

            User.dp = bitmap;
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
            navigationItem.setResId(R.drawable.ic_action_back);

            foodSubList.add(navigationItem);
        }

        List<NavigationItem> shoppingSubList = new ArrayList<>();

        for(int i = 0; i < shoppingSubCategories.length; i++)
        {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(shoppingSubCategories[i]);
            navigationItem.setResId(R.drawable.ic_action_back);

            shoppingSubList.add(navigationItem);
        }

        List<NavigationItem> electronicsSubList = new ArrayList<>();

        for(int i = 0; i < electronicsSubCategories.length; i++)
        {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(electronicsSubCategories[i]);
            navigationItem.setResId(R.drawable.ic_action_back);

            electronicsSubList.add(navigationItem);
        }

        List<NavigationItem> hotelsSubList = new ArrayList<>();

        for(int i = 0; i < hotelsSubCategories.length; i++)
        {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(hotelsSubCategories[i]);
            navigationItem.setResId(R.drawable.ic_action_back);

            hotelsSubList.add(navigationItem);
        }

        List<NavigationItem> mallsSubList = new ArrayList<>();

        for(int i = 0; i < mallsSubCategories.length; i++)
        {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(mallsSubCategories[i]);
            navigationItem.setResId(R.drawable.ic_action_back);

            mallsSubList.add(navigationItem);
        }

        List<NavigationItem> automotiveSubList = new ArrayList<>();

        for(int i = 0; i < automotiveSubCategories.length; i++)
        {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(automotiveSubCategories[i]);
            navigationItem.setResId(R.drawable.ic_action_back);

            automotiveSubList.add(navigationItem);
        }

        List<NavigationItem> travelSubList = new ArrayList<>();

        for(int i = 0; i < travelSubCategories.length; i++)
        {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(travelSubCategories[i]);
            navigationItem.setResId(R.drawable.ic_action_back);

            travelSubList.add(navigationItem);
        }

        List<NavigationItem> entertainmentSubList = new ArrayList<>();

        for(int i = 0; i < entertainmentSubCategories.length; i++)
        {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(entertainmentSubCategories[i]);
            navigationItem.setResId(R.drawable.ic_action_back);

            entertainmentSubList.add(navigationItem);
        }

        List<NavigationItem> jewellerySubList = new ArrayList<>();

        for(int i = 0; i < jewellerySubCategories.length; i++)
        {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(jewellerySubCategories[i]);
            navigationItem.setResId(R.drawable.ic_action_back);

            jewellerySubList.add(navigationItem);
        }

        List<NavigationItem> sportsSubList = new ArrayList<>();

        for(int i = 0; i < sportsSubCategories.length; i++)
        {
            NavigationItem navigationItem = new NavigationItem();
            navigationItem.setItemName(sportsSubCategories[i]);
            navigationItem.setResId(R.drawable.ic_action_back);

            sportsSubList.add(navigationItem);
        }

        subChildList = new HashMap<>();
        subChildList.put(subGroupList.get(1).getItemName(), foodSubList);
        subChildList.put(subGroupList.get(2).getItemName(), shoppingSubList);
        subChildList.put(subGroupList.get(3).getItemName(), electronicsSubList);
        subChildList.put(subGroupList.get(4).getItemName(), hotelsSubList);
        subChildList.put(subGroupList.get(5).getItemName(), mallsSubList);
        subChildList.put(subGroupList.get(6).getItemName(), automotiveSubList);
        subChildList.put(subGroupList.get(7).getItemName(), travelSubList);
        subChildList.put(subGroupList.get(8).getItemName(), entertainmentSubList);
        subChildList.put(subGroupList.get(9).getItemName(), jewellerySubList);
        subChildList.put(subGroupList.get(10).getItemName(), sportsSubList);
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
