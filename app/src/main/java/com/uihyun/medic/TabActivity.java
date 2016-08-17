package com.uihyun.medic;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TabHost;

import com.uihyun.medic.page.AboutActivity;
import com.uihyun.medic.page.FavoriteActivity;
import com.uihyun.medic.page.IndgActivity;
import com.uihyun.medic.page.MainActivity;
import com.uihyun.medic.page.ShapeActivity;

/**
 * Created by Uihyun on 2016. 6. 12..
 */
public class TabActivity extends Activity {

    private static final String CURRENT_TAB = "CURRENT_TAB";
    private static final String TAB_NAME = "TAB_NAME";
    private static final String TAB_INDG = "TAB_INDG";
    private static final String TAB_SHAPE = "TAB_SHAPE";
    private static final String TAB_FAVORITE = "TAB_FAVORITE";
    private static final String TAB_ABOUT = "TAB_ABOUT";

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private TabHost tabHost;
    private LocalActivityManager mLocalActivityManager;
    private String currentTab;
    private GestureDetector gestureScanner;
    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.OnGestureListener() {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (e1 == null || e2 == null)
                return false;
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // right swipe
                if (tabHost.getCurrentTab() < tabHost.getTabWidget().getChildCount()) {
                    tabHost.setCurrentTab(tabHost.getCurrentTab() + 1);
                    return true;
                }
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // left swipe
                if (tabHost.getCurrentTab() > 0) {
                    tabHost.setCurrentTab(tabHost.getCurrentTab() - 1);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        mLocalActivityManager = new LocalActivityManager(this, false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);
        tabHost.setup(mLocalActivityManager);

        if (savedInstanceState != null) {
            // load current tab
            currentTab = savedInstanceState.getString(CURRENT_TAB);
            initializeTabs();
            tabHost.setCurrentTabByTag(currentTab);
        } else {
            initializeTabs();
            tabHost.setCurrentTab(0);
        }

        gestureScanner = new GestureDetector(this, mGestureListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureScanner.onTouchEvent(event);
    }

    public void initializeTabs() {
        TabHost.TabSpec spec;

        spec = tabHost.newTabSpec(TAB_NAME).setContent(new Intent(this, MainActivity.class)).setIndicator(null, getResources().getDrawable(R.drawable.tab_name_selector));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec(TAB_INDG).setContent(new Intent(this, IndgActivity.class)).setIndicator(null, getResources().getDrawable(R.drawable.tab_indg_selector));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec(TAB_SHAPE).setContent(new Intent(this, ShapeActivity.class)).setIndicator(null, getResources().getDrawable(R.drawable.tab_shape_selector));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec(TAB_FAVORITE).setContent(new Intent(this, FavoriteActivity.class)).setIndicator(null, getResources().getDrawable(R.drawable.tab_favorite_selector));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec(TAB_ABOUT).setContent(new Intent(this, AboutActivity.class)).setIndicator(null, getResources().getDrawable(R.drawable.tab_about_selected));
        tabHost.addTab(spec);

        setTabColor(tabHost);
        setTabAction(tabHost);
    }

    private void setTabColor(TabHost tabHost) {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#F0F0F0"));
    }

    private void setTabAction(final TabHost tabHost) {
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String arg) {
                // tab select action
                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#F0F0F0"));
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // save current tab
        outState.putString(CURRENT_TAB, currentTab);
        super.onSaveInstanceState(outState);
    }
}
