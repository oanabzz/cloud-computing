package hw1.handler.caller;

import com.amazonaws.services.lambda.model.TooManyRequestsException;

import java.io.IOException;

public class TranslationCaller implements ApiCaller {

    private static final String TRANSLATE_URL = "https://api.funtranslations.com/translate/";

    @Override
    public String getData(String... params) throws TooManyRequestsException, IOException {
        return postCall(formatBody(params[0]), TRANSLATE_URL + params[1]);
    }

    private String formatBody(String text) {
        return "{\"text\":\"" + text + "\"}";
    }
}
