package com.example.lockapp;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;


public class ServiceClass extends Service {
    private BroadcastReceiver _appLockerBroadcastReceiver;
    private static Timer _timer = new Timer();

    class ServiceTimerTask extends TimerTask {
        @Override
        public void run() {
            _toastHandler.sendEmptyMessage(0);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "started", Toast.LENGTH_SHORT).show();
        startService();
        Intent intent = new Intent("tr.com.ebilge.cprot.lockApp.restart.service");
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
        System.out.println("onCreate Broadcast sent");

        try {
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            _appLockerBroadcastReceiver = new LockAppBroadcastReceiver();
            registerReceiver(_appLockerBroadcastReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            LockAppBroadcastReceiver.completeWakefulIntent(intent);
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (_timer != null) {
                _timer.cancel();
                _timer = null;
            }

            if (_appLockerBroadcastReceiver != null) {
                unregisterReceiver(_appLockerBroadcastReceiver);
            }
        } catch (Exception ex) {
            //FirebaseCrash.report(ex);
        }

        Toast.makeText(getApplicationContext(), "Bye", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent("tr.com.ebilge.cprot.lockApp.restart.service");
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
        System.out.println("onDestroy Broadcast sent");
    }

    private final Handler _toastHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            checkForeGroundApp();
        }
    };

    private void checkForeGroundApp() {
        if(isScreenOn(getApplicationContext())){
            String Appname = getCurrentForegroundAppPackage(getApplicationContext());
            System.out.println(Appname+" ON");
        }else System.out.println("Why u close the screen");

    }

    private void startService() {
        _timer = new Timer();
        _timer.scheduleAtFixedRate(new ServiceTimerTask(), 0, 1000);
    }

    public boolean isScreenOn(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            boolean screenOn = false;
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    screenOn = true;
                }
            }
            return screenOn;
        } else {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            //noinspection deprecation
            return pm.isScreenOn();
        }
    }

   /* @SuppressLint("WrongConstant")
    private String lockAppForPostLollipop() {
        String currentApp = null;
        UsageStatsManager usageStatsManager = null;
        long time = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        } else {
            usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService("usagestats");
        }

        PackageManager packageManager = getApplicationContext().getPackageManager();
        ApplicationInfo applicationInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            UsageEvents usageEvents = usageStatsManager.queryEvents(time - 1000 * 1000, time);
            UsageEvents.Event event = new UsageEvents.Event();
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
            }

            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                currentApp = event.getPackageName();
            }

            if (StringHelper.isNullOrEmpty(currentApp)) {
                return null;
            }
        } else {
            List<UsageStats> appList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList == null || appList.size() <= 0) {
                return null;
            }

            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : appList) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }

            if (!mySortedMap.isEmpty()) {
                currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
            }

            if (StringHelper.isNullOrEmpty(currentApp)) {
                return null;
            }
        }

        try {
            applicationInfo = packageManager.getApplicationInfo(currentApp, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (applicationInfo == null || currentApp.equalsIgnoreCase(getApplicationContext().getPackageName())) {
            return null;
        }

        return currentApp;
    }*/


    /*class ForegroundCheckTask extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... params) {
            final Context context = params[0].getApplicationContext();
            return isAppOnForeground(context);
        }

        private boolean isAppOnForeground(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = context.getPackageName();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
            return false;
        }
    }*/

    private String getCurrentForegroundAppPackage(Context context) {
        String topPackageName = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            @SuppressLint("WrongConstant") UsageStatsManager mUsageStatsManager = (UsageStatsManager)getSystemService("usagestats");
            long time = System.currentTimeMillis();
            // We get usage stats for the last 10 seconds
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*1000, time);
            // Sort the stats by the last time used
            if(stats != null) {
                SortedMap<Long,UsageStats> mySortedMap = new TreeMap<Long,UsageStats>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(),usageStats);
                }
                if(mySortedMap != null && !mySortedMap.isEmpty()) {
                    topPackageName =  mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        }

        // If unable to retrieve, try an alternative method (requires GET_TASKS permission)
        return topPackageName;
    }

    /*private String getForegroundAppPackageAlternative(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            if (tasks != null && !tasks.isEmpty()) {
                return tasks.get(0).processName;
            }
        }
        return null;
    }


    public String getRecentApps(Context context) {
            String topPackageName = "";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

                long time = System.currentTimeMillis();

                UsageEvents usageEvents = mUsageStatsManager.queryEvents(time - 1000 * 30, System.currentTimeMillis() + (10 * 1000));
                UsageEvents.Event event = new UsageEvents.Event();
                while (usageEvents.hasNextEvent()) {
                    usageEvents.getNextEvent(event);
                }

                if (event != null && !StringHelper.isNullOrEmpty(event.getPackageName()) && event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    if (AndroidUtils.isRecentActivity(event.getClassName())) {
                        return event.getClassName();
                    }
                    return event.getPackageName();
                } else {
                    topPackageName = "";
                }
            } else {
                ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;

                // If the current running activity it will return not the package name it will return the activity refernce.
                if (AndroidUtils.isRecentActivity(componentInfo.getClassName())) {
                    return componentInfo.getClassName();
                }

                topPackageName = componentInfo.getPackageName();
            }


            return topPackageName;
        }*/


}

