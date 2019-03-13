package hw2.handler;

import com.amazonaws.util.IOUtils;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import hw2.cache.CachingHandler;
import hw2.handler.repo.PlacesRepo;
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
        try {
            switch (RequestType.getRequestType(exchange.getRequestMethod(), exchange.getRequestURI().getPath())) {
                case GET_PLACES_OF_USER: {
                    try{
                        String path = exchange.getRequestURI().getPath();
                        if (CachingHandler.check(path)) {
                            response = CachingHandler.get(path);
                            break;
                        } else {
                            String username = exchange.getRequestURI().getPath().split("/")[2];
                            response = placesRepo.getPlacesOfUser(username);
                            CachingHandler.add(path, response);
                            break;
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        String username = exchange.getRequestURI().getPath().split("/")[2];
                        response = placesRepo.getPlacesOfUser(username);
                        break;
                    }
                }
                case GET_PLACES: {
                    try{
                        String path = exchange.getRequestURI().getPath();
                        if (CachingHandler.check(path)) {
                            response = CachingHandler.get(path);
                            break;
                        } else {
                            response = placesRepo.getAllPlaces();
                            CachingHandler.add(path, response);
                            break;
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        response = placesRepo.getAllPlaces();
                        break;
                    }
                }
                case GET_PLACE_WITH_ID: {
                    try{
                        String path = exchange.getRequestURI().getPath();
                        if (CachingHandler.check(path)) {
                            response = CachingHandler.get(path);
                            break;
                        } else {
                            String username = exchange.getRequestURI().getPath().split("/")[2];
                            String placeId = exchange.getRequestURI().getPath().split("/")[3];
                            response = placesRepo.getPlaceById(username, placeId);
                            CachingHandler.add(path, response);
                            break;
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        String placeId = exchange.getRequestURI().getPath().split("/")[3];
                        String username = exchange.getRequestURI().getPath().split("/")[2];
                        response = placesRepo.getPlaceById(username, placeId);
                        break;
                    }
                }
                case POST_PLACE: {
                    try {
                        String username = exchange.getRequestURI().getPath().split("/")[2];
                        String placeJson = IOUtils.toString(exchange.getRequestBody());
                        response = placesRepo.postPlace(placeJson, username);
                        break;
                    } catch (JsonParseException e) {
                        response = new Response("", 400);
                        System.out.println("WRONG JSON HERE");
                        break;
                    }
                }
                case UPDATE_PLACE_WITH_ID: {
                    try {
                        String username = exchange.getRequestURI().getPath().split("/")[2];
                        String placeId = exchange.getRequestURI().getPath().split("/")[3];
                        String placeJson = IOUtils.toString(exchange.getRequestBody());
                        response = placesRepo.updatePlace(placeJson, username, placeId);
                        break;
                    } catch (JsonParseException e) {
                        System.out.println("OMG HERE");
                        response = new Response("", 400);
                        break;
                    }
                }
                case UPDATE_PLACES_OF_USER: {
                    try {
                        String placeJson = IOUtils.toString(exchange.getRequestBody());
                        response = placesRepo.updatePlaces(placeJson);
                        break;
                    } catch (JsonParseException e) {
                        response = new Response("", 400);
                        break;
                    }
                }
                case PATCH_PLACE: {
                    try {
                        String placeJson = IOUtils.toString(exchange.getRequestBody());
                        String username = exchange.getRequestURI().getPath().split("/")[2];
                        String placeId = exchange.getRequestURI().getPath().split("/")[3];
                        response = placesRepo.patchPlace(placeJson, username, placeId);
                        break;
                    } catch (JsonParseException e) {
                        response = new Response("", 400);
                        break;
                    }
                }
                case DELETE_PLACE: {
                    String username = exchange.getRequestURI().getPath().split("/")[2];
                    String placeId = exchange.getRequestURI().getPath().split("/")[3];
                    response = placesRepo.deletePlace(username, placeId);
                    break;
                }
                case DELETE_PLACES_FROM_USER: {
                    String username = exchange.getRequestURI().getPath().split("/")[2];
                    response = placesRepo.deleteAllUsersPlaces(username);
                    break;
                }
                case DELETE_ALL_PLACES: {
                    response = placesRepo.deleteAllPlaces();
                    break;
                }
                default: {
                    System.out.println("UNKNOWN WTF");
                    response.setCode(400);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = new Response("", 500);
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
