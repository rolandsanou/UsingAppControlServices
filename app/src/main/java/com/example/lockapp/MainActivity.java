package com.example.lockapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageManager packageManager = this.getPackageManager();
        List<ResolveInfo> installedApps = getInstalledApps();
        List<AppInfo> appList = new ArrayList<>();

        startService(new Intent(this, ServiceClass.class));
        checkOverlayPermission();
        List<String> result = findAndRetrieve("SMSTO:+905369921217:hello the world");
        List<String> settings = querySettingPkgName();
        Toast.makeText(this," "+settings.get(0)+"  "+settings.get(1),Toast.LENGTH_LONG).show();

        for (ResolveInfo resolveInfo : installedApps) {
            if (StringHelper.isEqual(resolveInfo.activityInfo.packageName, this.getPackageName()) ||
                    (resolveInfo.activityInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                continue;
            }

            String appName = resolveInfo.loadLabel(packageManager).toString();
            String packageName = resolveInfo.activityInfo.packageName;
            if (resolveInfo.loadIcon(packageManager) != null) {
                Drawable appIcon = resolveInfo.loadIcon(packageManager);
                AppInfo appInfo = new AppInfo(appName, packageName, appIcon, true);
                appList.add(appInfo);
            }
        }

        ListView appListView = findViewById(R.id.appListView);
        AppListAdapter adapter = new AppListAdapter(this, appList);
        appListView.setAdapter(adapter);

        startServiceToGetRecentApps();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, ForegroundService.class));
        } else {
            startService(new Intent(this, ForegroundService.class));
        }

    }

    private List<ResolveInfo> getInstalledApps() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> pkgAppsList = this.getPackageManager().queryIntentActivities(mainIntent, 0);
        return pkgAppsList;
    }

    public void startServiceToGetRecentApps() {
        // And you should check first the user API level is 21 or more if yes you should get access for usage access settings
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (UStats.getUsageStatsList(this).isEmpty()) {

                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);

                return;
            }
        }

        startService(new Intent(this, ServiceClass.class));
    }

    public void checkOverlayPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(myIntent);
            }
        }
    }

    private List<String> findAndRetrieve(String qrCode){
        int firstOccurence = qrCode.indexOf(":", 0);
        int lastOccurence = qrCode.lastIndexOf(":");
        String header = qrCode.substring(0,firstOccurence);//retrieve header string
        String number = qrCode.substring(firstOccurence+1,lastOccurence);//number
        String content = qrCode.substring(lastOccurence+1);//content
        List<String>result = new ArrayList<>();
        result.add(number);
        result.add(content);
        result.add(header);
        return result;//return a list from which you can use the result
    }

    private List<String> querySettingPkgName() {
        Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        List<ResolveInfo> resolveInfos1 = getPackageManager().queryIntentActivities(intent1, PackageManager.MATCH_DEFAULT_ONLY);
        /*if (resolveInfos == null || resolveInfos.size() == 0) {
            return;
        }*/
        List<String>result = new ArrayList<>();
        result.add(resolveInfos.get(0).activityInfo.packageName);
        result.add(resolveInfos1.get(0).activityInfo.packageName);

        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, ServiceClass.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}