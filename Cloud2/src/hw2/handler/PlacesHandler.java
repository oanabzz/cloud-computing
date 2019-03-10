package hw2.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import hw2.handler.repo.PlacesRepo;
import hw2.handler.repo.UsersRepo;
import hw2.handler.util.RequestType;
import hw2.handler.util.Response;

import java.io.IOException;
import java.io.OutputStream;

public class PlacesHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        System.out.println("got a places req");
        Response response = new Response();
        PlacesRepo placesRepo = new PlacesRepo();
        switch (RequestType.getRequestType(exchange.getRequestMethod(), exchange.getRequestURI().getPath())) {
            case GET_PLACE: {
                String username = exchange.getRequestURI().getPath().split("/")[2];
                response = placesRepo.getPlace(username);
                System.out.println(response);
                break;
            }
            case GET_PLACES: {
                response = placesRepo.getPlaces();
                System.out.println(response);
                break;
            }
            default: {
                System.out.println("UNKNOWN WTF");
                response.setCode(400);
                break;
            }
        }
        sendResponse(exchange, response);
    }

    private void sendResponse(HttpExchange exchange, Response response) {
        System.out.println("someone reached places " + exchange.getRequestURI().getPath());
        exchange.getResponseHeaders().set("Content-type", "application/json");
        OutputStream responseBodyStream = exchange.getResponseBody();
        try {
            exchange.sendResponseHeaders(response.getCode(), response.getBody().getBytes().length);
            responseBodyStream.write(response.getBody().getBytes());
        } catch (IOException e) {
            System.out.println("!!! Could not write to client");
            ;
        } finally {
            try {
                responseBodyStream.close();
            } catch (IOException e) {
                System.out.println("!!! Could not close output stream");
            }

        }
    }
}
