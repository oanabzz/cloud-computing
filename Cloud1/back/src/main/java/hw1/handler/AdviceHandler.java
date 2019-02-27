package hw1.handler;

import com.amazonaws.services.lambda.model.TooManyRequestsException;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import hw1.handler.caller.AdviceCaller;
import hw1.handler.caller.SpeechCaller;
import hw1.handler.caller.TranslationCaller;
import hw1.handler.util.Languages;
import hw1.handler.util.RequestType;
import hw1.handler.util.Response;
import hw1.model.Advice;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ThreadLocalRandom;

public class AdviceHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        Response response = new Response();
        System.out.println("Handeluiesc un advice");
        switch (RequestType.getRequestType(httpExchange.getRequestMethod(), httpExchange.getRequestURI().getPath())) {
            case GET_ADVICE: {
                Logger.info("Handling /advice request...");
                long startTime = System.currentTimeMillis();
                Advice advice;
                try {
                    advice = getAdvice();
                    response.setBody(new Gson().toJson(advice));
                    response.setCode(200);
                    long latency = System.currentTimeMillis() - startTime;
                    Logger.info("GET request to /advice terminated successfully: " + latency);
                    System.out.println(response.getBody());
//                    throw new TooManyRequestsException("ss");
                    break;
                } catch (TooManyRequestsException e) {
                    Logger.error(e, "GET request to /advice failed: 429");
                    response = new Response("too many requests", 429);
                    break;
                } catch (IOException e) {
                    Logger.error(e, "GET request to /advice failed: 500");
                    response= new Response("",500);
                    break;
                }

            }
            default: {
                response = new Response("bad request", 400);
            }
        }
        sendResponse(httpExchange, response);
    }

    private void sendResponse(HttpExchange exchange, Response response) {
        System.out.println("someone reached facts" + exchange.getRequestURI().getPath());
        System.out.println("response" + response.toString());
        OutputStream responseBodyStream = exchange.getResponseBody();
        try {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(response.getCode(), response.getBody().getBytes().length);
            responseBodyStream.write(response.getBody().getBytes());
        } catch (IOException e) {
            System.out.println("!!! Could not send response");
            e.printStackTrace();
        } finally {
            try {
                responseBodyStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("!! Could not close socket");
            }
        }
    }

    private Advice getAdvice() throws IOException, TooManyRequestsException {
        String randomAdvice = new AdviceCaller().getData().split("\"")[5];
        System.out.println(randomAdvice);

        String language = getRandomLanguage();
        System.out.println(language);
        String translation = new TranslationCaller().getData(randomAdvice, language);
        translation = translation.split("\"")[9];


//        String randomAdvice = "You are an exceptional chef! This dish is amazing, I am speechless!";
//        String translation = "mamma mia dis food is shit 8 out of 10 iz oke mate";
//        String language = Languages.PIRATE.getLang();


        String fileName = new SpeechCaller().getData(translation);
        Advice result = new Advice();
        result.setOriginal(randomAdvice);
        result.setTranslation(translation);
        result.setLanguage(language);
        result.setFileName(fileName);
        return result;
    }

    private String getRandomLanguage() {
        switch (ThreadLocalRandom.current().nextInt(1, 6)) {
            case 1: {
                return Languages.SHAKESPEARE.getLang();
            }
            case 2: {
                return Languages.DOTHRAKI.getLang();
            }
            case 3: {
                return Languages.MINION.getLang();
            }
            case 4: {
                return Languages.PIRATE.getLang();
            }
            case 5: {
                return Languages.YODA.getLang();
            }
            default: {
                return Languages.SHAKESPEARE.getLang();
            }
        }
    }
}