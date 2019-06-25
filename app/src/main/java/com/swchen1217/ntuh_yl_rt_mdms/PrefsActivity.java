package com.swchen1217.ntuh_yl_rt_mdms;

import android.content.Context;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PrefsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
    }

    public static String getServer(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("server", "");
    }

    /*public static String getDevice(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("device", "");
    }

    public static String getUser(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("user", "");
    }*/
}
