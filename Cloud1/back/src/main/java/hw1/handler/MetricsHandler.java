package hw1.handler;

import com.amazonaws.services.lambda.model.TooManyRequestsException;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import hw1.handler.util.RequestType;
import hw1.handler.util.Response;
import hw1.model.Advice;
import org.pmw.tinylog.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetricsHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) {
        Response response = new Response();
//        System.out.println("Handeluiesc un advice");
        switch (RequestType.getRequestType(httpExchange.getRequestMethod(), httpExchange.getRequestURI().getPath())) {
            case GET_METRICS: {
                try {
                    response.setBody(computeStats());
                    System.out.println(response.getBody());
                    response.setCode(200);
                    break;
                } catch (IOException e) {
                    response.setCode(500);
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
        System.out.println("someone reached metrics" + exchange.getRequestURI().getPath());
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
                System.out.println("!! Could not close socket");
            }
        }
    }

    public String computeStats() throws IOException {
        Pattern regexReqFail = Pattern.compile("[a-zA-Z :]+equest to [a-zA-Z:/. ]+ failed: [0-9]+.*");
        Pattern regexReqSuccess = Pattern.compile("[a-zA-Z :]+equest to [a-zA-Z:/. ]+ successfully: [0-9]+.*");

        String advApi = "adviceslip";
        String translateApi = "funtranslations";
        String awsPolly = "AWSPolly";
        String fullApi = " /advice ";

        int advLatency = 0;
        int trnaslateLatency = 0;
        int pollyLatency = 0;
        int fullLatency = 0;

        int advNr = 1;
        int transNr = 1;
        int pollyNr = 1;
        int fullNr = 1;

        String advCode = "UNKNOWN";
        String transCode = "UNKNOWN";
        String pollyCode = "UNKNOWN";
        String fullCode = "UNKNOWN";

        FileInputStream fstream = new FileInputStream("log.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;

        //Read File Line By Line
        while ((strLine = br.readLine()) != null) {

            Matcher matcherFail = regexReqFail.matcher(strLine);
            Matcher matcherSuccess = regexReqSuccess.matcher(strLine);

            if (matcherFail.matches()) {
                String code = strLine.split("failed: ")[1].split(" ")[0];
                if (strLine.contains(advApi)) {
                    advCode = code;
//                    System.out.println("FAIL: " + advApi);
                }
                if (strLine.contains(translateApi)) {
                    transCode = code;
//                    System.out.println("FAIL: " + translateApi);
                }
                if (strLine.contains(awsPolly)) {
                    pollyCode = code;
//                    System.out.println("FAIL: " + awsPolly);
                }
                if (strLine.contains(fullApi)) {
                    fullCode = code;
//                    System.out.println("FAIL: " + fullApi);
                }
            }
            if (matcherSuccess.matches()) {
//                System.out.println(strLine);
                String latency = strLine.split("successfully: ")[1].split(" ")[0];
                if (strLine.contains(advApi)) {
                    advCode = "200";
                    advNr++;
                    advLatency = advLatency + Integer.parseInt(latency);
//                    System.out.println("SUCCESS" + strLine);
                }
                if (strLine.contains(translateApi)) {
                    transCode = "200";
                    transNr++;
                    trnaslateLatency = trnaslateLatency + Integer.parseInt(latency);
//                    System.out.println("SUCCESS" + translateApi);
                }
                if (strLine.contains(awsPolly)) {
                    pollyCode = "200";
                    pollyNr++;
                    pollyLatency = pollyLatency + Integer.parseInt(latency);
//                    System.out.println("SUCCESS" + awsPolly);
                }
                if (strLine.contains(fullApi)) {
                    fullCode = "200";
                    fullNr++;
                    fullLatency = fullLatency + Integer.parseInt(latency);
//                    System.out.println("SUCCESS" + fullApi);
                }

            }

        }
        System.out.println("avg adv " + advLatency / advNr);
        System.out.println("avg trnas " + trnaslateLatency / transNr);
        System.out.println("avg polly " + pollyLatency / pollyNr);
        System.out.println("avg full " + fullLatency / fullNr);

        System.out.println("last code adv " + advCode);
        System.out.println("last code trnas " + transCode);
        System.out.println("last code polly " + pollyCode);
        System.out.println("last code full " + fullCode.replace(":",""));

        //Close the input stream
        fstream.close();
        Map<String, List<String>> result = new HashMap<>();

        List<String> adv = new LinkedList<>();
        Double advAvg = (double)advLatency/advNr;
        adv.add(advAvg.toString());
        adv.add(advCode);

        List<String> trans = new LinkedList<>();
        Double transAvg = (double) trnaslateLatency/transNr;
        trans.add(transAvg.toString());
        trans.add(transCode);

        List<String> polly = new LinkedList<>();
        Double pollyAvg = (double) pollyLatency/pollyNr;
        polly.add(pollyAvg.toString());
        polly.add(pollyCode);

        List<String> full = new LinkedList<>();
        Double fullAvg = (double) fullLatency/fullNr;
        full.add(fullAvg.toString());
        full.add(fullCode);

        result.put("https://api.adviceslip.com/advice",adv);
        result.put("https://api.funtranslations.com/translate",trans);
        result.put("AWSPolly", polly);
        result.put("/advice", full);

//        System.out.println( new Gson().toJson(result));
        return new Gson().toJson(result);
    }
}
