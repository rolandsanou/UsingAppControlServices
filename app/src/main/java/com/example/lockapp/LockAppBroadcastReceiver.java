package com.example.lockapp;

import android.content.Context;
import android.content.Intent;

import androidx.legacy.content.WakefulBroadcastReceiver;

public class LockAppBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && !StringHelper.isNullOrEmpty(intent.getAction()) && intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            System.out.println("YouYou");
        }
    }
}
