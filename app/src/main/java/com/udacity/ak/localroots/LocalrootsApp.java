package com.udacity.ak.localroots;

import android.app.Application;

import timber.log.Timber;

public class LocalrootsApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

}
