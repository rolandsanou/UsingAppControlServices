package com.example.lockapp;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.widget.Toast;

import java.util.List;

public class AppStatusChecker {

    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
        Toast.makeText(context, taskInfo.get(0).topActivity.getClassName(), Toast.LENGTH_LONG).show();
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        String packageName1 = componentInfo.getPackageName();

        /*if (activityManager != null) {
            // Get the list of running processes
            for (ActivityManager.RunningAppProcessInfo processInfo : activityManager.getRunningAppProcesses()) {
                if (processInfo.processName.equals(packageName)) {
                    // The app with the given packageName is running
                    return true;
                }
            }
        }*/

        // The app with the given packageName is not running
        return false;
    }
}
