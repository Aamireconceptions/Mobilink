package com.ooredoo.bizstore.listeners;

import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.ooredoo.bizstore.ui.activities.HomeActivity;

/**
 * @author Pehlaj Rai
 * @since 12-Jul-2015.
 */
public class DrawerChangeListener implements DrawerLayout.DrawerListener {

    HomeActivity homeActivity;

    public DrawerChangeListener(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    @Override
    public void onDrawerSlide(View view, float v) {
    }

    @Override
    public void onDrawerOpened(View view) {
        homeActivity.hideSearchPopup();
        homeActivity.hideSearchResults();
    }

    @Override
    public void onDrawerClosed(View view) {

    }

    @Override
    public void onDrawerStateChanged(int i) {
    }
}
