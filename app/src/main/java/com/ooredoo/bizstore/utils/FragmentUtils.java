package com.ooredoo.bizstore.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * @author Babar
 * @since 11-Jun-15.
 */
public class FragmentUtils {
    public static void addFragmentWithBackStack(AppCompatActivity activity, int containerId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = activity.getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(containerId, fragment, tag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static void replaceFragmentWithBackStack(AppCompatActivity activity, int containerId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = activity.getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment, tag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
