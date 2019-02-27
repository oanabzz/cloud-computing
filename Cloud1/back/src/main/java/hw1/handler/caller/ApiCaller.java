package hw1.handler.caller;

import com.amazonaws.services.lambda.model.TooManyRequestsException;
import org.pmw.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public interface ApiCaller {
    default String getCall(String endpoint) throws IOException {
        Logger.info("Initiating GET request to " + endpoint + "...");
        long startTime = System.currentTimeMillis();

        URL url = new URL(endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();

        Logger.info(endpoint + " responded with code: " + con.getResponseCode());

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        Logger.info(endpoint + " responded with: " + content.toString());

        con.disconnect();
        long latency = (System.currentTimeMillis() - startTime);
        Logger.info("GET request to " + endpoint + " terminated successfully: " + latency);
        return content.toString();
    }

    default String postCall(String body, String endpoint) throws IOException {
//        System.out.println(body);
        Logger.info("Initiating POST request to " + endpoint + "...");
        Logger.info("Request body: " + body);
        long startTime = System.currentTimeMillis();

        URL obj = new URL(endpoint);
        HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setRequestProperty("Content-Type", "application/json");
        postConnection.setDoOutput(true);

        OutputStream os = postConnection.getOutputStream();
        os.write(body.getBytes());
        os.flush();
        os.close();

        int responseCode = postConnection.getResponseCode();
        Logger.info(endpoint + " responded with code: " + postConnection.getResponseCode());
        Logger.info(endpoint + " responded with: " + postConnection.getResponseMessage());
//        System.out.println("POST Response Code :  " + responseCode);
//        System.out.println("POST Response Message : " + postConnection.getResponseMessage());
        StringBuilder response = new StringBuilder();

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    postConnection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

//            System.out.println(response.toString());

//            TODO: other codes
        } else {
//            System.out.println("POST DIDN'T WORK");
            Logger.error("POST request to " + endpoint + " failed: "+postConnection.getResponseCode());
            throw new TooManyRequestsException("Exceded number of requests to API");
        }

        long latency = System.currentTimeMillis() - startTime;
        Logger.info("POST request to " + endpoint + " terminated successfully: " + latency);
        return response.toString();
    }

    String getData(String... params) throws IOException;
}
