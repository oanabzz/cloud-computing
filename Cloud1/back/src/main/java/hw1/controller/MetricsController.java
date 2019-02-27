package hw1.controller;

import hw1.handler.MetricsHandler;

public class MetricsController extends Controller {
    public MetricsController() {
        createLocalContexts();
    }

    @Override
    void createLocalContexts() {
        httpServer.createContext("/metrics", new MetricsHandler());
    }
}
