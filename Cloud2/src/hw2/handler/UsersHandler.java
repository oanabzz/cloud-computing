package hw2.handler;

import com.amazonaws.util.IOUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import hw2.handler.repo.UsersRepo;
import hw2.handler.util.RequestType;
import hw2.handler.util.Response;

import java.io.IOException;
import java.io.OutputStream;

public class UsersHandler implements HttpHandler {

    private static final String SET_COOKIE = "Set-Cookie";
//    private UsersRepo usersRepo = new UsersRepo();

    @Override
    public void handle(HttpExchange exchange) {
        System.out.println("got a users req");
        Response response = new Response();
        UsersRepo usersRepo = new UsersRepo();
        switch (RequestType.getRequestType(exchange.getRequestMethod(), exchange.getRequestURI().getPath())) {
            case GET_USER: {
                System.out.println("GET USER");
                String username = exchange.getRequestURI().getPath().split("/")[2];
                response = usersRepo.getUser(username);
                System.out.println(response);
                break;
            }
            case GET_USERS:{
                System.out.println("GET USERS");
                response = usersRepo.getUsers();
                System.out.println(response);
                break;
            }
//            case POST_USER: {
//                String json = null;
//                try {
//                    json = IOUtils.toString(exchange.getRequestBody());
//                    response = usersRepo.postUser(json);
//                } catch (IOException e) {
//                    response = new Response("", 500);
//                }
//                break;
//            }
//            case UPDATE_USER: {
//                try {
//                    String json = IOUtils.toString(exchange.getRequestBody());
//                    String username = exchange.getRequestURI().getPath().split("/")[2];
//                    if (!Verification.userRequiredVerification(Verification.getCookie(exchange.getRequestHeaders()), username)) {
//                        response = new Response("", 403);
//                    } else {
//                        response = usersRepo.updateUser(json, username);
//                    }
//                } catch (IOException e) {
//                    response = new Response("", 500);
//                }
//                break;
//            }
            default: {
                System.out.println("UNKNOWN WTF");
                response.setCode(400);
                break;
            }
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
