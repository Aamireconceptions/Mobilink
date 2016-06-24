package com.ooredoo.bizstore.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.ooredoo.bizstore.ui.fragments.DemoFragment;

/**
 * @author Babar
 * @since 11-Jun-15.
 */
public class FragmentUtils {
    public static void addFragmentWithBackStack(AppCompatActivity activity, int containerId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = activity.getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(containerId, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public static void replaceFragment(Activity activity, int containerId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = activity.getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment, tag);
        fragmentTransaction.commit();
    }

    public static void replaceFragmentAllowStateLose(Activity activity, int containerId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = activity.getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment, tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public static void replaceFragmentWithBackStack(AppCompatActivity activity, int containerId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = activity.getFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public static void popBackStackInclusive(AppCompatActivity activity)
    {
        FragmentManager fragmentManager = activity.getFragmentManager();

        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public static void removeFragment(Activity activity, Fragment fragment)
    {
        FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();

        fragmentManager.executePendingTransactions();
    }

    public static void popBackStack(Activity activity)
    {
        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.popBackStack();
    }


}
