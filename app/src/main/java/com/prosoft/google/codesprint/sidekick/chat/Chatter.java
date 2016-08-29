package com.prosoft.google.codesprint.sidekick.chat;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by prave on 26-Aug-16.
 */
public class Chatter {

    private Context context;
    private static Chatter instance;
    private MediaPlayer mediaPlayer;
    private static AIMLHelper2 aimlHelper;

    protected Chatter(Context context) {
        this.context = context;
    }

    public static Chatter getInstance(Context context) throws IOException, ParserConfigurationException {
        if(instance == null) {
            instance = new Chatter(context);
            aimlHelper = new AIMLHelper2();
            aimlHelper.load(context.getApplicationContext().getAssets().open("brain.aiml"));
        }
        return instance;
    }

    public String normalise(String string) {
        Log.i("Chatter", string);
        string = " " + string + " ";
        string = string.replace("?", "");
        string = string.replace("!", "");
        string = string.replace(".", " . ");
        string = string.replace(" u ", " you ");
        string = string.replace(" r ", " are ");
        string = string.replace("who\'s", "who is");
        string = string.replace("I\'m", "I am");
        string = string.replace("Iam", "I am");
        Log.i("Chatter", "Normalised: " + string);
        return string;
    }

    public String respond(String text) {
        String response = "";
        if(text == null || text.isEmpty()) {
            return response;
        }
        text = normalise(text);
        text = text.toLowerCase();
        Response r = aimlHelper.getResponse(text);
        if(r != null) {
            String templates[] = r.getTemplates();
            response = templates[new Random().nextInt(templates.length)];
            response = response.trim();
            String action = r.getAction();
            if(action != null && !action.isEmpty()) {
                if(action.contains("play")) {
                    String clip = action.replaceAll("play", "").trim();
                    int id = context.getResources().getIdentifier(clip, "raw", context.getPackageName());
                    if(id != -1) {
                        response = " " + response;  // If there is a space at the beginning, then dont speak;
                        playSound(id);
                    }
                }
            }
        }
        /*
        if(text.contains("who are you")) {
            response = "I\'m SideKick, your personal fun assistant!";
        }*/
        return response;
    }
    public void playSound(int resId) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(context, resId);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
    }
}
