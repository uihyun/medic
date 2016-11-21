package com.uihyun.medic;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * Created by Uihyun on 2016. 11. 22..
 */

public class AppController extends Application {

    private static final String PROPERTY_ID = "UA-87755848-1";
    private static AppController instance;
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public static synchronized AppController getInstance() {
        return instance;
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID) :
                    (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker) :
                            analytics.newTracker(R.xml.ecommerce_tracker);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public enum TrackerName {
        APP_TRACKER,           // 앱 별로 트래킹
        GLOBAL_TRACKER,        // 모든 앱을 통틀어 트래킹
        ECOMMERCE_TRACKER,     // 유료 결재 트래킹
    }
}
