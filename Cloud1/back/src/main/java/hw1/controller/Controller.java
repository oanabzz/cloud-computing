package hw1.controller;

import com.sun.net.httpserver.HttpServer;
import hw1.handler.PagesHandler;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

public abstract class Controller {
    private static final String HOST_NAME = "localhost"; //or "localhost"
    private static final int PORT = 6969;
    protected static HttpServer httpServer;

    Controller() {
        try {
            if (httpServer == null) {
                httpServer = HttpServer.create(new InetSocketAddress(HOST_NAME, PORT), 3);
                httpServer.start();
                createGlobalContexts();
            }
        } catch (IOException e) {
            Logger.error(e,"Could not create server/serverSocket");
//            System.out.println("Could not create server/serverSocket");
        }
    }

    private void createGlobalContexts() {
        httpServer.createContext("/index.html", new PagesHandler()); //home or login
    }

    abstract void createLocalContexts();
}
