package com.example.lockapp;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private String name;
    private String packageName;
    private Drawable icon;
    private boolean IsLocked;

    public AppInfo(String name, String packageName, Drawable icon, boolean isLocked) {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
        IsLocked = isLocked;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public boolean isLocked() {
        return IsLocked;
    }

    public void setLocked(boolean locked) {
        IsLocked = locked;
    }
}