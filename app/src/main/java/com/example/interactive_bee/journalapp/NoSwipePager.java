package com.example.interactive_bee.journalapp;

/**
 * Created by interactive_bee on 28/06/2018.
 */

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


//Swiping must be disabled. for the bottom nav... as we are using a view page
//NoSwipePager in your XML layout instead of a regular ViewPager


public class NoSwipePager extends ViewPager {
    private boolean enabled;

    public NoSwipePager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
