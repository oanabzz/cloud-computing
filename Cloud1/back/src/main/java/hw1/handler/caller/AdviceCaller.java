package hw1.handler.caller;

import java.io.IOException;

public class AdviceCaller implements ApiCaller {

    private static final String URL = "https://api.adviceslip.com/advice";

    @Override
    public String getData(String... params) throws IOException {
        return getCall(URL).replace("\\u2018","'").replace("\\u2019","'");
    }
}
