package com.gr3mlin106.to_do_at_home;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.parse.ConfigCallback;
import com.parse.Parse;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import io.fabric.sdk.android.Fabric;

/**
 * Created by gr3mlin106 on 27.12.15.
 */
public class MainApplication extends Application {

    private static MainApplication instance = new MainApplication();

    public MainApplication() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        Log.d(this.getClass().getName(),"Start");

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {

            Log.d(this.getClass().getName(),"User is logged!");
            try {
                currentUser.fetchIfNeeded();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }


    }

}
