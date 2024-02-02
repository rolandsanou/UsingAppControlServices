package com.example.lockapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.view.Display;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class ForegroundService extends Service {
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final int NOTIFICATION_ID = 123;
    private Context _context;
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Bye", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent("tr.com.ebilge.cprot.lockApp.restart.service");
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
        System.out.println("onDestroy Broadcast sent");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
            ArrayList<String> lockedApps = PreferenceData.getInstance(getApplicationContext()).getLockedApps();
            if (lockedApps.contains(Appname)) {
                startActivity(Appname);
            }
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        // Create a notification for the foreground service.
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("AppLocker")
                .setSmallIcon(R.drawable.notification_logo)
                .setColor(Color.BLUE)
                .build();


        // Start the service as a foreground service.
        startForeground(NOTIFICATION_ID, notification);

        // Perform your background task here.
        //startService(new Intent(_context, ServiceClass.class));
        if (intent != null) {
            LockAppBroadcastReceiver.completeWakefulIntent(intent);
        }

        // Return START_STICKY to indicate that the service should be restarted if killed by the system.
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_NONE
            );

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            channel.setLightColor(Color.BLUE);
            manager.createNotificationChannel(channel);
        }
    }

    private void startActivity(String appPackageName){
        Intent intent = new Intent(ForegroundService.this, LockTestByNotification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("lastActivity", appPackageName);
        startActivity(intent);
    }
}
