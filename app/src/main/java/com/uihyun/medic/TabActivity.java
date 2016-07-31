package com.uihyun.medic;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * Created by Uihyun on 2016. 6. 12..
 */
public class TabActivity extends Activity {

    private static final String CURRENT_TAB = "CURRENT_TAB";
    private static final String TAB_HOME = "TAB_HOME";
    private static final String TAB_SEARCH = "TAB_MAP";
    private static final String TAB_MY = "TAB_MY";
    private TabHost tabHost;
    private LocalActivityManager mLocalActivityManager;
    private String currentTab;

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

    }

    public void initializeTabs() {
        TabHost.TabSpec spec;

        spec = tabHost.newTabSpec(TAB_HOME).setContent(new Intent(this, MainActivity.class)).setIndicator(null, getResources().getDrawable(R.drawable.tab_name_selector));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec(TAB_SEARCH).setContent(new Intent(this, IndgActivity.class)).setIndicator(null, getResources().getDrawable(R.drawable.tab_indg_selector));
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec(TAB_MY).setContent(new Intent(this, ShapeActivity.class)).setIndicator(null, getResources().getDrawable(R.drawable.tab_shape_selector));
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
