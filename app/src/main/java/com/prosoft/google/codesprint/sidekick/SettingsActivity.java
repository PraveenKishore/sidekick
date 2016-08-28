package com.prosoft.google.codesprint.sidekick;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat enableSpeakingNotification;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editor = PreferenceManager.getDefaultSharedPreferences(this).edit();

        enableSpeakingNotification = ((SwitchCompat)findViewById(R.id.enableSpeakingNotifications));

        enableSpeakingNotification.setChecked(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isSpeakingNotificationEnabled", false));

        enableSpeakingNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(!isNotificationAccessGranted()) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                }
                editor.putBoolean("isSpeakingNotificationEnabled", checked);
                editor.apply();
                editor.commit();
            }
        });
        findViewById(R.id.aboutUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, AboutUsActivity.class));
            }
        });
    }

    public void onResume() {
        super.onResume();
        if(isNotificationAccessGranted() && PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isSpeakingNotificationEnabled", false)) {
            enableSpeakingNotification.setChecked(true);
        } else {
            enableSpeakingNotification.setChecked(false);
        }
    }

    public boolean isNotificationAccessGranted() {
        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();
        return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName);
    }

}
