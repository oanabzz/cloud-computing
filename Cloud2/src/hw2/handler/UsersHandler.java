package hw2.handler;

import com.amazonaws.util.IOUtils;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import hw2.cache.CachingHandler;
import hw2.handler.repo.UsersRepo;
import hw2.handler.util.RequestType;
import hw2.handler.util.Response;

import java.io.IOException;
import java.io.OutputStream;

public class UsersHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) {
        Response response = new Response();
        UsersRepo usersRepo = new UsersRepo();
        try {
            switch (RequestType.getRequestType(exchange.getRequestMethod(), exchange.getRequestURI().getPath())) {
                case GET_USER: {
                    String path = exchange.getRequestURI().getPath();
                    if (CachingHandler.check(path)) {
                        System.out.println("I GOT CACHING");
                        response = CachingHandler.get(path);
                        break;
                    } else {
                        String username = exchange.getRequestURI().getPath().split("/")[2];
                        response = usersRepo.getUser(username);
                        CachingHandler.add(path, response);
                        break;
                    }
                }
                case GET_USERS: {
                    String path = exchange.getRequestURI().getPath();
                    if (CachingHandler.check(path)) {
                        response = CachingHandler.get(path);
                        break;
                    } else {
                        response = usersRepo.getUsers();
                        CachingHandler.add(path, response);
                        System.out.println(response);
                        break;
                    }
                }
                case POST_USER: {
                    String json = null;
                    try {
                        json = IOUtils.toString(exchange.getRequestBody());
                        response = usersRepo.postUser(json);
                    } catch (IOException e) {
                        response = new Response("", 500);
                        System.out.println("BAD REQUEST HERE");
                        break;
                    } catch (JsonParseException e) {
                        response = new Response("", 400);
                        System.out.println("WRONG JSON HERE");
                        break;
                    }
                    break;
                }
                case UPDATE_USER: {
                    try {
                        String json = IOUtils.toString(exchange.getRequestBody());
                        response = usersRepo.updateUser(json);
                    } catch (IOException e) {
                        response = new Response("", 500);
                        break;
                    } catch (JsonParseException e) {
                        response = new Response("", 400);
                        break;
                    }
                    break;
                }
                case UPDATE_USERS: {
                    try {
                        String json = IOUtils.toString(exchange.getRequestBody());
                        response = usersRepo.updateUsers(json);
                    } catch (IOException e) {
                        response = new Response("", 500);
                        break;
                    } catch (JsonParseException e) {
                        response = new Response("", 400);
                        break;
                    }
                    break;
                }
                case DELETE_ALL_USERS: {
                    try {
                        response = usersRepo.deleteAllUsers();
                    } catch (InterruptedException e) {
                        response = new Response("", 500);
                        break;
                    }
                    break;
                }
                case DELETE_USER: {
                    response = usersRepo.deleteUser(exchange.getRequestURI().getPath().split("/")[2]);
                    break;
                }
                case PATCH_USER: {
                    try {
                        String json = IOUtils.toString(exchange.getRequestBody());
                        String username = exchange.getRequestURI().getPath().split("/")[2];
                        response = usersRepo.patchUser(json, username);
                    } catch (IOException e) {
                        response = new Response("", 500);
                        break;
                    }
                    break;
                }
                default: {
                    System.out.println("UNKNOWN WTF");
                    response.setCode(400);
                    break;
                }
            }
        } catch (Exception e) {
            response = new Response("", 500);
        }
        sendResponse(exchange, response);
    }

    private void sendResponse(HttpExchange exchange, Response response) {
        System.out.println("someone reached users " + exchange.getRequestURI().getPath());
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
}
