package com.india.android.mapp;

import android.app.Application;

import com.orhanobut.hawk.Hawk;

/**
 * Created by admin on 26-08-2018.
 */

public class ApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(getApplicationContext()).build();
    }
}
