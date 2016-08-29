package com.prosoft.google.codesprint.sidekick.chat;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by prave on 27-Aug-16.
 */
public class AIMLHelper2 {
    private TreeSet<Response> aimlSet;
    public static String TAG = "AIMLHelper";

    public AIMLHelper2() {
        aimlSet = new TreeSet();
    }

    public void load(InputStream is) throws ParserConfigurationException, IOException {
        if(is == null) {
            Log.i("AIMLHelper", "InputStream is null");
            return;
        }
        String aimlFile = getStringFromInputStream(is);
        // Log.i(TAG, "File: ");
        // Log.i(TAG, aimlFile);
        aimlFile = aimlFile.replaceAll(" +", " ");
        aimlFile = StringUtils.substringBetween(aimlFile, "<aiml>", "</aiml>");
        String[] categories = StringUtils.substringsBetween(aimlFile, "<category>", "</category>");
        for(String cat:categories) {
            cat = cat.trim();
            if(cat.isEmpty()) {
                continue;
            }
            // Log.i(TAG, cat);
            String pattern = StringUtils.substringBetween(cat, "<pattern>", "</pattern>");
            String template = StringUtils.substringBetween(cat, "<template>", "</template>");
            String oob = StringUtils.substringBetween(cat, "<oob>", "</oob>");

            Response response = null;
            if(template.contains("<li>")) {
                String[] list = StringUtils.substringsBetween(template, "<li>", "</li>");
                response = new Response(pattern, list, oob);
            } else {
                response = new Response(pattern, new String[] {template}, oob);
            }
            Log.i(TAG, "Pattern: " + pattern);
            for(String t:response.getTemplates()) {
                Log.i(TAG, "Template: " + t);
            }
            Log.i(TAG, "OOB: " + oob);
            aimlSet.add(response);
        }
    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public Response getResponse(String text) {
        Response response = null;
        if(text != null) {
            text = text.trim();
            for(Response r:aimlSet) {
                Log.i(TAG, "Comparing: " + r.getPattern());
                if(text.toLowerCase().matches(r.getPattern().replace("*", ".*"))) {
                    Log.i(TAG, "Matched: {" + r.getPattern() + "} Reply: " + r.getTemplates());
                    return r;
                }
            }
        }
        return response;
    }
}