package hw2.handler.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum RequestType {
    POST_USER("POST", "/users"),
    GET_USERS("GET", "/users"),
    GET_USER("GET", "/users/[a-zA-Z0-9]+"),
    UPDATE_USER("PATCH", "/users/[a-zA-Z0-9]+"),
    POST_PLACE("POST", "/places/[a-zA-Z0-9]+"),
    GET_PLACE("GET", "/places/[a-zA-Z0-9]+"),
    GET_PLACES("GET", "/places"),
    GET_PLACE_WITH_ID("GET", "/places/[a-zA-Z0-9]+/[a-zA-Z0-9_]+"),
    UPDATE_PLACE_WITH_ID("PATCH", "/places/[a-zA-Z0-9]+/[a-zA-Z0-9_]+"),
    //TODO: check below!!
    DELETE_PLACE("DELETE", "/places/[a-zA-Z0-9]+/[a-zA-Z0-9_]+"),
    UNKNOWN("",".");

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


