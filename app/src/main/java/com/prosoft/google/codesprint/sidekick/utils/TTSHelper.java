package com.prosoft.google.codesprint.sidekick.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by prave on 23-Aug-16.
 */
public class TTSHelper {
    private static TTSHelper instance;
    private TextToSpeech textToSpeech;
    public static String TAG = "TextToSpeechHelper";

    protected TTSHelper(Context context) {
        textToSpeech = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.SUCCESS) {
                    Log.i(TAG, "TextToSpeech Initialization complete");
                    if(textToSpeech.isLanguageAvailable(Locale.UK) == TextToSpeech.LANG_AVAILABLE) {
                        // textToSpeech.setLanguage(Locale.UK);
                        textToSpeech.setSpeechRate(0.7f);
                    }
                }
            }
        });
    }

    public static TTSHelper getInstance(Context context) {
        if(instance == null) {
            instance = new TTSHelper(context);
        }
        return instance;
    }

    public boolean speak(String string) {
        if(textToSpeech != null) {
            textToSpeech.speak(string, TextToSpeech.QUEUE_FLUSH, null);
            return true;
        }
        return false;
    }

    public void shutdown() {
        if(textToSpeech != null) {
            textToSpeech.shutdown();
        }
        textToSpeech = null;
        instance = null;
        System.gc();
    }
}
