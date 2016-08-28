package com.prosoft.google.codesprint.sidekick.chat;

/**
 * Response Structure to hold each aiml response.
 */
public class Response implements Comparable<Response> {
    private String action;
    private String pattern;
    private String[] template;

    public Response(String pattern, String template[], String action) {
        this.action = action;
        this.pattern = pattern;
        this.template = template;
    }

    public String getAction() {
        return action;
    }

    public String getPattern() {
        return pattern;
    }

    public String[] getTemplates() {
        return template;
    }

    @Override
    public int compareTo(Response response) {
        if(response.pattern != null && response.pattern.compareToIgnoreCase(pattern) > 0) {
            return 1;
        } else if(response.pattern != null && response.pattern.compareToIgnoreCase(pattern) < 0) {
            return -1;
        }
        return 0;
    }
}
