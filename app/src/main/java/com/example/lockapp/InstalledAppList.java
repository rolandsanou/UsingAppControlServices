package com.example.lockapp;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

public class InstalledAppList {
    private PackageManager packageManager;

    public InstalledAppList(PackageManager packageManager) {
        this.packageManager = packageManager;
    }

    public List<String> getInstalledApps() {
        List<String> installedApps = new ArrayList<>();

        // Get the list of installed packages
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);

        for (PackageInfo packageInfo : packages) {
            // Get the package name
            String packageName = packageInfo.packageName;

            // Filter out system apps if needed (optional)
            if (!isSystemApp(packageInfo)) {
                installedApps.add(packageName);
            }
        }

        return installedApps;
    }

    private boolean isSystemApp(PackageInfo packageInfo) {
        // Check if the app is a system app
        return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
}
