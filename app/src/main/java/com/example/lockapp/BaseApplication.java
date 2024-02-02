package com.example.lockapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;


public class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks{

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static BaseApplication _instance;
    public static BaseApplication getInstance()
    {
        return _instance;
    }
    private static Activity _lastActivity;
    public static Context getContext()
    {
        return _instance;
    }
    private static ArrayList<String> _activities;

    @Override
    public void onCreate()
    {
        _instance = this;
        _activities = new ArrayList<String>();
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
    }

    public static Activity getLastActivity() {
        return _lastActivity;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        String activityId = String.format("%s%d",activity.getTitle() , activity.getTaskId());
        _activities.add(activityId);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        String activityId = String.format("%s%d",activity.getTitle() , activity.getTaskId());
        _activities.remove(activityId);
    }
}
