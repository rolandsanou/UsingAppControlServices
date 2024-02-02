package com.example.lockapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LockTestByNotification extends AppCompatActivity {

    private String appPackageName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locktestby);
        appPackageName = getIntent().getStringExtra("lastActivity");
        listenerButton();
    }

    private void listenerButton(){

        findViewById(R.id.on).setOnClickListener(v -> {
            Toast.makeText(this, "Allowed access", Toast.LENGTH_SHORT).show();
            if (!StringHelper.isNullOrEmpty(appPackageName)){
                PreferenceData.getInstance(this).unlockApp(appPackageName);
                PreferenceData.getInstance(this).setLastLockedApp(appPackageName);
                PreferenceData.getInstance(this).commit();
            }

            finish();
        });

        findViewById(R.id.off).setOnClickListener(v -> {
            Toast.makeText(this, "Denied access", Toast.LENGTH_SHORT).show();
        });
    }


}
