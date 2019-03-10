package hw2.controller;

import hw2.handler.PlacesHandler;

import java.io.IOException;

public class PlacesController extends Controller {
    public PlacesController() {
        createLocalContexts();
    }

    @Override
    void createLocalContexts() {
        httpServer.createContext("/places", new PlacesHandler());
    }
}
