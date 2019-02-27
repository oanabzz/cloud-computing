package hw1.handler;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

public class HomeHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        OutputStream responseBodyStream = exchange.getResponseBody();
        try {
            System.out.println("someone reached home");
            System.out.println(exchange.getRequestURI());
            exchange.getResponseHeaders().set("Location", "/index.html");
            exchange.sendResponseHeaders(302, 0);
        } catch (IOException e) {
            System.out.println("!!! Could not send response to client");
        } finally {
            try {
                responseBodyStream.close();
            } catch (IOException e) {
                System.out.println("!!! Could not close output stream");
            }
        }
    }
}