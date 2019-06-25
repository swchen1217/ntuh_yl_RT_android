package com.swchen1217.ntuh_yl_rt_mdms;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class PrefsActivity extends PreferenceActivity {

    EditTextPreference et_s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
        Log.d("MyLog","PrefsActivity onCreate");
        et_s= (EditTextPreference) findPreference("server");
        et_s.setSummary(PreferenceManager.getDefaultSharedPreferences(this)
                .getString("server", ""));
        et_s.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(newValue.toString().equals("")){
                    new AlertDialog.Builder(PrefsActivity.this)
                            .setTitle("伺服器位址不能為空!!")
                            .setMessage("請重新輸入")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                    preference.setSummary(newValue.toString());
                }else{
                    preference.setSummary(newValue.toString());
                }
                return true;
            }
        });
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
