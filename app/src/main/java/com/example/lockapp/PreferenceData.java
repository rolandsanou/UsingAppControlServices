package com.example.lockapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class PreferenceData {
    public static final int VERSION                                      = 1;
    private static final String PREFERENCES_NAME                         = "lockApp";
    private static final String KEY_LOCKED_APPS                          = "LockedApps";
    private static final String KEY_UNLOCKED_APPS                        = "UnlockedApps";
    private static final String KEY_LAST_LOCKED_APP                      = "LastLockedApp";

    private Context _context;
    private ArrayList<String> _lockedApps = new ArrayList<>();
    private PreferenceValue _lockedAppsStr;
    private PreferenceValue _unlockedAppsStr;
    private PreferenceValue _lastLockedApp;
    private static PreferenceData _instance;
    private ArrayList<String> _unlockedApps = new ArrayList<>();

    public PreferenceData(Context context){
        _context = context;
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        _lockedAppsStr = new PreferenceValue(pref.getString(KEY_LOCKED_APPS, ""));
        _unlockedAppsStr = new PreferenceValue(pref.getString(KEY_UNLOCKED_APPS, ""));
        _lastLockedApp = new PreferenceValue(pref.getString(KEY_LAST_LOCKED_APP, ""));

        String lockedAppsStr = _lockedAppsStr.getStringValue();
        manageLockedApps(lockedAppsStr);
        manageUnlockedApps(_unlockedAppsStr.getStringValue());
    }

    private void manageUnlockedApps(String unlockedAppsStr) {
        _unlockedApps.clear();
        if (!StringHelper.isNullOrEmpty(unlockedAppsStr))
        {
            String[] apps = unlockedAppsStr.split(",");
            if (apps.length > 0)
            {
                Collections.addAll(_unlockedApps, apps);
            }
        }
    }

    private void manageLockedApps(String lockedAppsStr) {
        _lockedApps.clear();
        if (!StringHelper.isNullOrEmpty(lockedAppsStr))
        {
            String[] apps = lockedAppsStr.split(",");
            if (apps.length > 0)
            {
                Collections.addAll(_lockedApps, apps);
            }
        }
    }

    public void addOrRemoveFromLockedApps(String appName, boolean isAdded) {
        if (isAdded)
        {
            if (_lockedApps.contains(appName))
            {
                return;
            }

            String appStr = _lockedAppsStr.getStringValue();
            String lockedApps = StringHelper.isNullOrEmpty(appStr) ? appName :
                    String.format(Locale.getDefault(),"%s,%s",appStr,appName);
            _lockedAppsStr.setValue(lockedApps);
        }
        else{
            _lockedApps.remove(appName);
            if (_lockedApps.size() == 0)
            {
                _lockedAppsStr.setValue("");
                return;
            }

            String lockedApps = _lockedApps.get(0);
            for (int i=1;i<_lockedApps.size();i++)
            {
                lockedApps = String.format(Locale.getDefault(),"%s,%s",lockedApps,_lockedApps.get(i));
            }

            _lockedAppsStr.setValue(lockedApps);
        }
    }

    public static PreferenceData getInstance(Context context){
        if (_instance == null)
        {
            synchronized (PreferenceData.class)
            {
                if (_instance == null)
                {
                    _instance = new PreferenceData(context);
                }
            }
        }
        return _instance;
    }

    public void commit(){
        SharedPreferences pref = _context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        _lockedAppsStr.commit(editor, KEY_LOCKED_APPS);
        _unlockedAppsStr.commit(editor,KEY_UNLOCKED_APPS);
        editor.apply();
        manageLockedApps(_lockedAppsStr.getStringValue());
        manageUnlockedApps(_unlockedAppsStr.getStringValue());
    }

    public ArrayList<String> getLockedApps()
    {
        return _lockedApps;
    }

    public int getLockedAppsCount()
    {
        return _lockedApps.size();
    }

    public void setLockedApps(ArrayList<String> value)
    {
        _lockedApps = value;
    }

    public boolean isAppUnlocked(String packageName){
        return _unlockedApps.contains(packageName);
    }

    public void unlockApp(String packageName){
        if (_unlockedApps.contains(packageName))
        {
            return;
        }

        String appStr = _unlockedAppsStr.getStringValue();
        String lockedApps = StringHelper.isNullOrEmpty(appStr) ? packageName :
                String.format(Locale.getDefault(),"%s,%s",appStr,packageName);
        _unlockedAppsStr.setValue(lockedApps);
    }

    public void clearUnlockedApps(){
        _unlockedAppsStr.setValue("");
    }

    public String getLastLockedApp() {
        return _lastLockedApp.getStringValue();
    }

    public void setLastLockedApp(String value) {
        _lastLockedApp.setValue(value);
    }

    private class PreferenceValue {
        private static final int VALUE_TYPE_BOOLEAN = 0;
        private static final int VALUE_TYPE_INT     = 1;
        private static final int VALUE_TYPE_STRING  = 2;
        private static final int VALUE_TYPE_LONG = 3;

        private int     _type;
        private boolean _isDirty;

        private boolean _booleanValue;
        private int     _intValue;
        private long _longValue;
        private String  _stringValue;

        public PreferenceValue(boolean initialValue)
        {
            _type = VALUE_TYPE_BOOLEAN;
            _isDirty = false;
            _booleanValue = initialValue;
        }

        public PreferenceValue(int initialValue)
        {
            _type = VALUE_TYPE_INT;
            _isDirty = false;
            _intValue = initialValue;
        }

        public PreferenceValue(String initialValue)
        {
            _type = VALUE_TYPE_STRING;
            _isDirty = false;
            _stringValue = initialValue;
        }

        public PreferenceValue(long initialValue) {
            _type = VALUE_TYPE_LONG;
            _isDirty = false;
            _longValue = initialValue;
        }

        public boolean getBooleanValue()
        {
            if (_type != VALUE_TYPE_BOOLEAN)
            {
                throw new IllegalStateException("Value is not a boolean type.");
            }

            return _booleanValue;
        }

        public int getIntValue()
        {
            if (_type != VALUE_TYPE_INT)
            {
                throw new IllegalStateException("Value is not a int type.");
            }

            return _intValue;
        }

        public String getStringValue()
        {
            if (_type != VALUE_TYPE_STRING)
            {
                throw new IllegalStateException("Value is not a string type.");
            }
            return _stringValue;
        }

        public long getLongValue() {
            if (_type != VALUE_TYPE_LONG) {
                throw new IllegalStateException("Value is not a int type.");
            }

            return _longValue;
        }

        public void setValue(boolean value)
        {
            if (_type != VALUE_TYPE_BOOLEAN)
            {
                throw new IllegalStateException("Value is not a boolean type.");
            }

            if (_booleanValue != value)
            {
                _booleanValue = value;
                _isDirty = true;
            }
        }

        public void setValue(int value)
        {
            if (_type != VALUE_TYPE_INT)
            {
                throw new IllegalStateException("Value is not a int type.");
            }

            if (_intValue != value)
            {
                _intValue = value;
                _isDirty = true;
            }
        }

        public void setValue(String value)
        {
            if (_type != VALUE_TYPE_STRING)
            {
                throw new IllegalStateException("Value is not a string type.");
            }

            if (!StringHelper.isEqual(_stringValue, value))
            {
                _stringValue = value;
                _isDirty = true;
            }
        }

        public void setValue(long value) {
            if (_type != VALUE_TYPE_LONG) {
                throw new IllegalStateException("Value is not a long type.");
            }

            if (_longValue != value) {
                _longValue = value;
                _isDirty = true;
            }
        }

        public void commit(SharedPreferences.Editor editor, String key)
        {
            if (_isDirty)
            {
                switch (_type)
                {
                    case VALUE_TYPE_BOOLEAN:
                        editor.putBoolean(key, _booleanValue);
                        break;

                    case VALUE_TYPE_INT:
                        editor.putInt(key, _intValue);
                        break;

                    case VALUE_TYPE_STRING:
                        editor.putString(key, _stringValue);
                        break;

                    case VALUE_TYPE_LONG:
                        editor.putLong(key, _longValue);
                        break;

                    default:
                        throw new IllegalStateException();
                }
            }
        }
    }

}
