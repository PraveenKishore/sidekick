package com.prosoft.google.codesprint.sidekick.chat;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by prave on 27-Aug-16.
 */
public class AIMLHelper {
    private TreeSet<Response> aimlSet;
    public static String TAG = "AIMLHelper";

    public AIMLHelper() {
        aimlSet = new TreeSet();
    }

    public void load(InputStream is) throws ParserConfigurationException {
        if(is == null) {
            Log.i("AIMLHelper", "InputStream is null");
            return;
        }
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document doc = null;

        try {
            doc = builder.parse(is);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        doc.getDocumentElement().normalize();
        Log.i(TAG, "Root element: " + doc.getDocumentElement().getNodeName());

        NodeList categories = doc.getElementsByTagName("category");
        for(int i = 0; i < categories.getLength(); i++) {
            Node category = categories.item(i);
            if(category.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) category;
                try {
                    String pattern = (element.getElementsByTagName("pattern")).item(0).getTextContent().toLowerCase().trim();
                    String oob = (element.getElementsByTagName("oob")).item(0).getTextContent().toLowerCase().trim();
                    Element templateElement = (Element)(element.getElementsByTagName("template")).item(0);
                    NodeList templateList = (templateElement.getElementsByTagName("li"));
                    String[] template = null;

                    if(templateList.getLength() > 1) {
                        template = new String[templateList.getLength()];
                        for(int j = 0; j < templateList.getLength(); j++) {
                            template[j] = templateList.item(j).getTextContent().toString();
                        }
                    } else {
                        template = new String[1];
                        template[0] = (element.getElementsByTagName("template")).item(0).getTextContent().trim();
                    }
                    Log.i(TAG, "Pattern: " + pattern);
                    for(String t:template) {
                        Log.i(TAG, "Template: " + t);
                    }
                    Log.i(TAG, "OOB: " + oob);

                    aimlSet.add(new Response(pattern, template, oob));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public Response getResponse(String text) {
        Response response = null;
        if(text != null) {
            text = text.trim();
            for(Response r:aimlSet) {
                Log.i(TAG, "Comparing: " + r.getPattern());
                if(r.getPattern().equalsIgnoreCase(text)) {
                    Log.i(TAG, "Matched: {" + r.getPattern() + "} Reply: " + r.getTemplates());
                    return r;
                }
            }
        }
        return response;
    }
}