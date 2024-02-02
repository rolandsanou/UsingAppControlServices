package com.example.lockapp;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.util.ArrayList;

public class AccessService extends AccessibilityService {
    private static String _usageApp;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {

            String appPackageName;
            Log.d("Screen Action: ", AccessibilityEvent.eventTypeToString(event.getEventType()));
            if (AccessibilityEvent.eventTypeToString(event.getEventType()).contains("STATE_CHANGED")) {
                appPackageName = event.getPackageName().toString();
                Log.d("appPackageName: ", appPackageName);
                if (StringHelper.isNullOrEmpty(appPackageName)) {
                    return;
                }

                PreferenceData settings = PreferenceData.getInstance(getApplicationContext());

                    PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                        if (!powerManager.isInteractive()) {
                            settings.clearUnlockedApps();
                            settings.commit();
                        }
                    } else {
                        if (!powerManager.isScreenOn()) {
                            settings.clearUnlockedApps();
                            settings.commit();
                        }
                    }

                    if (settings.isAppUnlocked(appPackageName)) {
                        return;
                    }

                    String lastLockedApp = settings.getLastLockedApp();
                    if (!StringHelper.isNullOrEmpty(lastLockedApp)) {
                        if (StringHelper.isEqual(lastLockedApp, appPackageName) || StringHelper.isEqual(appPackageName, "android") || StringHelper.isEqual(appPackageName, "com.google.android.inputmethod.latin")) {
                            return;
                        } else {
                            settings.setLastLockedApp("");
                        }
                    }


                ArrayList<String> lockedApp = settings.getLockedApps();
                if (!lockedApp.contains(appPackageName)) {
                    return;
                }

                _usageApp = appPackageName;
                startActivity(appPackageName);
            }
        } catch (Exception e) {
            Log.d("C-Prot: ", e.getMessage());
        }
    }

    @Override
    public void onInterrupt() {
        Log.d("C-Prot: ", "ServiceInterrupt");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d("C-Prot: ", "ServiceConnect");
    }

    private void startActivity(String appPackageName){
        Intent intent = new Intent(getApplicationContext(), LockTestByNotification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("lastActivity", appPackageName);
        startActivity(intent);
    }
}
