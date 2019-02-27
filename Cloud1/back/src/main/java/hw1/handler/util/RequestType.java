package hw1.handler.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum RequestType {
    GET_ADVICE("GET", "/advice"),
    GET_METRICS("GET", "/metrics"),
    UNKNOWN("", ".");

    private String verb;
    private Pattern regex;

    RequestType(String verb, String regex) {
        this.regex = Pattern.compile(regex);
        this.verb = verb;
    }

    public static RequestType getRequestType(String verb, String path) {
        Matcher matcher;
        for (RequestType type : RequestType.values()) {
            matcher = type.regex.matcher(path);
            if (type.verb.equals(verb.toUpperCase()) && matcher.matches()) {
                return type;
            }
        }
        return UNKNOWN;
    }
}


