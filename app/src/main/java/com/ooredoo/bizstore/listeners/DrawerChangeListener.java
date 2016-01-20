package com.ooredoo.bizstore.listeners;

import android.support.v4.view.GravityCompat;
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

        /*if(homeActivity.drawerLayout.isDrawerOpen(GravityCompat.END))
        {
            homeActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
        }*/
    }

    @Override
    public void onDrawerClosed(View view)
    {
        //homeActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
    }

    @Override
    public void onDrawerStateChanged(int i) {
    }
}
