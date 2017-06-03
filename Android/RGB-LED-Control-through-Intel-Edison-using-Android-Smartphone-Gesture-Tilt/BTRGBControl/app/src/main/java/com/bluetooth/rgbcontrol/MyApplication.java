package com.bluetooth.rgbcontrol;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;

import com.google.android.gms.analytics.Tracker;

/**
 * Created by amigo on 25/2/16.
 */
public class MyApplication extends Application {


    public Tracker mTracker;
    private String TAG = "MyApplication";


    public void startTracking()
    {
        if(mTracker == null)
        {

            Log.d(TAG, "MyApplication() in startTracking()");

            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);

            mTracker = ga.newTracker(R.xml.track_app);
            ga.enableAutoActivityReports(this);
            ga.getLogger().setLogLevel(com.google.android.gms.analytics.Logger.LogLevel.VERBOSE);
        }
    }

    public Tracker getTracker()
    {

        startTracking();

        return mTracker;
    }
}
