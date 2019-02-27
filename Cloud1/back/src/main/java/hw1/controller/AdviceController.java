package hw1.controller;

import hw1.handler.AdviceHandler;

public class AdviceController extends Controller {
    public AdviceController() {
        createLocalContexts();
    }

    @Override
    void createLocalContexts() {
        httpServer.createContext("/advice", new AdviceHandler());
    }
}
