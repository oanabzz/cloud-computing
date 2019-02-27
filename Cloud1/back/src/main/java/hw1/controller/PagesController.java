package hw1.controller;

import hw1.handler.PagesHandler;

public class PagesController extends Controller {
    public PagesController() {
        createLocalContexts();
    }

    @Override
    void createLocalContexts() {
        httpServer.createContext("/res", new PagesHandler());
        httpServer.createContext("/", new PagesHandler());
    }
}
