package com.ooredoo.bizstore.listeners;

import android.view.View;

import com.ooredoo.bizstore.R;

/**
 * Created by Babar on 23-Jun-15.
 */
public class FilterOnClickListener implements View.OnClickListener
{
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.deals_discount_checkbox:

                if(v.isSelected())
                {

                }
                else
                {

                }


                break;

            case R.id.business_directory_checkbox:


                break;
        }

        v.setSelected(!v.isSelected());
    }


}
