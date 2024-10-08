package com.emojimixer;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class EMApplication extends Application {
    private static Application instance;

    @Override
    public void onCreate() {

        super.onCreate();
        instance = this;
        DynamicColors.applyToActivitiesIfAvailable(this);
    }

    public static Application getInstance() {
        return instance;
    }
}