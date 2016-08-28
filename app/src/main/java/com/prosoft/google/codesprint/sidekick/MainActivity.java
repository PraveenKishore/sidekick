package com.prosoft.google.codesprint.sidekick;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.prosoft.google.codesprint.sidekick.chat.Chatter;
import com.prosoft.google.codesprint.sidekick.utils.TTSHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    TTSHelper ttsHelper;
    private AppCompatEditText editInput;
    private TextView textResponse;
    private Chatter chatter;
    public String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ttsHelper = TTSHelper.getInstance(this);
        editInput = (AppCompatEditText) findViewById(R.id.editInput);

        try {
            chatter = Chatter.getInstance(MainActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        findViewById(R.id.speak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ttsHelper.speak("HelloWorld! I\'m Jarvis, your personal digital assistant!");
            }
        });

        textResponse = (TextView) findViewById(R.id.response);

        findViewById(R.id.btn_mic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = editInput.getText().toString();
                /*if(input != null && !input.isEmpty()) {
                    ttsHelper.speak(input);
                }*/
                promptSpeechInput();
            }
        });

        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        editInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Log.i(TAG, "" + editInput.getText().toString());
                    String response = chatter.respond(editInput.getText().toString());
                    if(!response.isEmpty() && !(response.startsWith(" "))) {
                        ttsHelper.speak(response);
                    }
                    textResponse.setText(response);
                    return false;
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something");
        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),"Speech not supported", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editInput.setText(result.get(0));
                    String response = chatter.respond(result.get(0));
                    if(!response.isEmpty() && !(response.startsWith(" "))) {
                        ttsHelper.speak(response);
                    }
                    textResponse.setText(response);
                }
                break;
            }

        }
    }

}
