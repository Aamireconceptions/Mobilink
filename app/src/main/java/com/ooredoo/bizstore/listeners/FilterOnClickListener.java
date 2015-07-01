package com.ooredoo.bizstore.listeners;

import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ooredoo.bizstore.BizStore;
import com.ooredoo.bizstore.R;
import com.ooredoo.bizstore.ui.activities.HomeActivity;

/**
 * Created by Babar on 23-Jun-15.
 */
public class FilterOnClickListener implements View.OnClickListener
{
    private HomeActivity activity;

    private View lastRatingSelected;

    private View lastButtonSelected;

    public String rating, minDiscount, maxDiscount;

    public FilterOnClickListener(HomeActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {

            case R.id.new_deals:

                setButtonSelected(v);

                break;

            case R.id.popular_deals:

                setButtonSelected(v);

                break;

            case R.id.filter:

                activity.drawerLayout.openDrawer(GravityCompat.END);

                break;

            case R.id.back:

                activity.drawerLayout.closeDrawer(GravityCompat.END);

                break;

            case R.id.done:

                activity.drawerLayout.closeDrawer(GravityCompat.END);

                break;
            /*case R.id.deals_discount_checkbox:

                if(v.isSelected())
                {

                }
                else
                {

                }


                break;

            case R.id.business_directory_checkbox:


                break;*/

            case R.id.rating_checkbox:

                if(v.isSelected())
                {

                }
                else
                {
                    rating = null;
                }

                setCheckboxSelected(v);

                break;

            case R.id.rating_1:

                setRatingSelected(v);

                activity.ratingFilter = "1";

                break;

            case R.id.rating_2:

                setRatingSelected(v);

                activity.ratingFilter = "2";

                break;


            case R.id.rating_3:

                setRatingSelected(v);

                activity.ratingFilter = "3";

                break;

            case R.id.rating_4:

                setRatingSelected(v);

                activity.ratingFilter = "4";

                break;

            case R.id.rating_5:

                setRatingSelected(v);

                activity.ratingFilter = "5";

                break;

            case R.id.discount_checkbox:

                if(v.isSelected())
                {

                }
                else
                {

                }

                setCheckboxSelected(v);

                break;
        }
    }

    public void setButtonSelected(View v)
    {
        if(lastButtonSelected != null)
        {
            lastButtonSelected.setSelected(false);
        }

        v.setSelected(true);

        lastButtonSelected = v;
    }

    public void setRatingSelected(View v)
    {
        if(lastRatingSelected != null)
        {
            lastRatingSelected.setSelected(false);
        }

        v.setSelected(true);

        lastRatingSelected = v;
    }

    private void setCheckboxSelected(View v)
    {
        v.setSelected(!v.isSelected());
    }
}
