package com.prosoft.google.codesprint.sidekick;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.prosoft.google.codesprint.sidekick.utils.TTSHelper;

public class NotificationService extends NotificationListenerService {
    Context context;
    private static final String TAG = "NotificationService";
    private TTSHelper ttsHelper;

    @Override
    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();
        ttsHelper = TTSHelper.getInstance(context);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean isEnabled = prefs.getBoolean("isSpeakingNotificationEnabled", false);
        Log.i(TAG, "SpeakingNotification: " + isEnabled);

        if(!isEnabled) {
            return;
        }

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        boolean onlyOnHeadset = prefs.getBoolean("onlyOnHeadset", false);
        boolean headsetConnected = audioManager.isWiredHeadsetOn();
        if(onlyOnHeadset) {
            if(!headsetConnected) {
                return;
            }
        }

        Log.i(TAG,"ID :" + sbn.getId() + "t" + sbn.getNotification().toString()+ "\t" + sbn.getPackageName());
        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        CharSequence notificationText = extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence notificationSubText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);

        String packageName = sbn.getPackageName().toLowerCase();
        if(!packageName.contains("music") &&
                !packageName.contains("systemui") &&
                !packageName.contains("browser") &&
                !packageName.contains("ucmobile") &&
                !packageName.contains("walkman") &&
                !packageName.contains("inputmethod") &&
                !packageName.contains("gaana") &&
                !packageName.contains("com.android") &&
                !packageName.contains("saavn")) {
            Log.i(TAG, "App: " + getAppName(packageName));
            Log.i(TAG, "Title: " + notificationTitle);
            Log.i(TAG, "Text: " + notificationText);
            Log.i(TAG, "Subtext: " + notificationSubText);

            if(notificationText == null) {
                return;
            }

            if(packageName.contains("whatsapp")) {
                try {
                    ttsHelper.speak("You have message from " + notificationTitle + ". " + notificationText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if(packageName.contains("messaging") || getAppName(packageName).contains("Messaging")) {
                ttsHelper.speak("You received a text message from " + notificationTitle);
            } else {
                ttsHelper.speak(getAppName(packageName) + " says " + notificationText);
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");
    }

    public String getAppName(String packageName) {
        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo( packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    }
}
