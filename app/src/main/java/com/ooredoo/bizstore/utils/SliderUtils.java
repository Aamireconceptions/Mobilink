package com.ooredoo.bizstore.utils;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.ooredoo.bizstore.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Babar on 03-Aug-15.
 */
public class SliderUtils
{
    private ViewPager viewPager;

    private TimerTask timerTask;

    private Timer timer;

    private int pos = 0;

    private boolean isEndReached = false;

    public int count;

    private final static int DELAY = 5 * 1000;

    private final static int PERIOD = 3 * 1000;

    public SliderUtils(final ViewPager viewPager)
    {
        this.viewPager = viewPager;
    }

    public void start(final int count)
    {
        this.count = count;

        stop();

        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                if(isEndReached)
                {
                    pos--;

                    if(pos >= 0)
                    {
                        /*viewPager.post(new Runnable() {
                            @Override
                            public void run() {
                                View child = viewPager.getChildAt(pos);
                                if(child != null)
                                {
                                    View imageView = child.findViewById(R.id.image_view);

                                    //Logger.print("SliderImageView: "+imageView);

                                    String tag = (String) imageView.getTag();

                                    if(imageView != null && tag != null &&  tag.equals("loaded"))
                                    {
                                        viewPager.setCurrentItem(pos, true);
                                    }
                                    else
                                    {
                                        pos++;
                                    }
                                }
                            }
                        });*/
                        viewPager.post(new Runnable() {
                            @Override
                            public void run() {
                                viewPager.setCurrentItem(pos, true);
                            }
                        });


                    }
                    else
                    {
                        isEndReached = false;
                    }
                }
                else
                {
                    pos++;

                    if(pos < count)
                    {
                       /* viewPager.post(new Runnable() {
                            @Override
                            public void run() {
                                View child = viewPager.getChildAt(pos);
                                if(child != null)
                                {
                                    View imageView = child.findViewById(R.id.image_view);

                                    //Logger.print("SliderImageView: "+imageView);

                                    String tag = (String) imageView.getTag();

                                    if(imageView != null && tag != null &&  tag.equals("loaded"))
                                    {
                                        viewPager.setCurrentItem(pos, true);
                                    }
                                    else
                                    {
                                        pos--;
                                    }
                                }
                            }
                        });*/
                        viewPager.post(new Runnable() {
                            @Override
                            public void run() {
                                viewPager.setCurrentItem(pos, true);
                            }
                        });

                    }
                    else
                    {
                        isEndReached = true;
                    }
                }
            }
        };

        timer = new Timer();
        timer.schedule(timerTask, DELAY, PERIOD);
    }

    public void stop()
    {
        if(timer != null)
        {
            timer.cancel();
        }

        if(timerTask != null)
        {
            timerTask.cancel();
        }
    }
}
