package hw1.handler;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

public class PagesHandler implements HttpHandler {

    private final String[] extensions = {
            ".html", ".css", ".js", "png"
    };
    private final String[] contentTypes = {
            "text/html", "text/css", "text/javascript", "image/png"
    };

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            System.out.println("pages handler");
            System.out.println(exchange.getRequestURI().getPath());
            String path = "../front" + exchange.getRequestURI().getPath();
            System.out.println(path);
            setHeaders(exchange);
            exchange.sendResponseHeaders(200, getResource(path).length);
            OutputStream responseOutputStream = exchange.getResponseBody();
            responseOutputStream.write(getResource(path));
            responseOutputStream.close();
        } catch(IOException e) {
            System.out.println(e.getMessage());
        }
        //TODO: exceptions
    }

    private void setHeaders(HttpExchange exchange) {
        for (int i = 0; i < extensions.length; i++) {
            if (exchange.getRequestURI().getPath().contains(extensions[i])) {
                exchange.getResponseHeaders().set("Content-Type", contentTypes[i]);
                return;
            }
        }
    }

    private byte[] getResource(String path) throws IOException {
        if (path.contains(".png") || path.contains(".mp3") || path.contains(".gif")) {
            return java.nio.file.Files.readAllBytes(Paths.get(path));
        } else {
            return Files.toString(new File(path), Charsets.UTF_8).getBytes();
        }
    }
}
