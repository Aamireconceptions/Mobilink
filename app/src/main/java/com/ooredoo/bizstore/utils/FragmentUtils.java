package com.ooredoo.bizstore.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.ooredoo.bizstore.R;

/**
 * @author Babar
 * @since 11-Jun-15.
 */
public class FragmentUtils {
    public static void addFragmentWithBackStack(AppCompatActivity activity, int containerId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(containerId, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public static void replaceFragment(AppCompatActivity activity, int containerId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment, tag);
        fragmentTransaction.commit();
    }

    public static void replaceFragmentAllowStateLose(AppCompatActivity activity, int containerId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment, tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public static void replaceFragmentWithBackStack(AppCompatActivity activity, int containerId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    public static void popBackStackInclusive(AppCompatActivity activity)
    {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public static void removeFragment(AppCompatActivity activity, Fragment fragment)
    {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();

        fragmentManager.executePendingTransactions();
    }

    public static void popBackStack(AppCompatActivity activity)
    {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    public static void replaceFragmentAllowStateLoseAnimation(AppCompatActivity activity, int containerId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(containerId, fragment, tag);
        fragmentTransaction.commitAllowingStateLoss();
    }


}
