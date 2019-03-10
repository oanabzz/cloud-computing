package hw2.handler.util;

import java.util.Map;
import java.util.TreeMap;

public class Response {
    private String body;
    private int code;
    private Map<String, String> headers = new TreeMap<>();

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(String key, String value) {
        this.headers.put(key, value);
    }

    public Response() {

    }

    public Response(String body, int code) {
        this.body = body;
        this.code = code;
    }


    public String getBody() {
        return body;
    }

    public int getCode() {
        return code;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Response{" +
                "body='" + body + '\'' +
                ", code=" + code +
                ", headers=" + headers +
                '}';
    }
}
