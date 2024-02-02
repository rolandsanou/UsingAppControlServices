package com.example.lockapp;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends ArrayAdapter<AppInfo> {
    private Context context;
    private List<AppInfo> appList;


    public AppListAdapter(Context context, List<AppInfo> appList) {
        super(context, R.layout.list_item_app, appList);
        this.context = context;
        this.appList = appList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_app, parent, false);
        }

        final AppInfo appInfo = appList.get(position);
        final String packageName = appInfo.getPackageName();

        CheckBox appCheckBox = convertView.findViewById(R.id.appCheckBox);
        ImageView appIconImageView = convertView.findViewById(R.id.appIconImageView);
        TextView appNameTextView = convertView.findViewById(R.id.appNameTextView);

        appCheckBox.setOnCheckedChangeListener(null); // Clear any previous listeners
        appCheckBox.setChecked(PreferenceData.getInstance(context).getLockedApps().contains(appInfo.getPackageName())); // Set checkbox state

        // Handle checkbox state changes
        appCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // App is selected, add it to the selectedApps list
                isChecked = appCheckBox.isChecked();
                //appCheckBox.setChecked(isChecked);
                PreferenceData preferenceData = new PreferenceData(getContext());
                preferenceData.addOrRemoveFromLockedApps(packageName,isChecked);
                preferenceData.commit();
                notifyDataSetChanged(); // Refresh the view
            }
        });

        appNameTextView.setText(appInfo.getName());
        appIconImageView.setImageDrawable(appInfo.getIcon());


        return convertView;
    }

    /*public List<AppInfo> getSelectedApps() {
        return selectedApps;
    }*/
}
