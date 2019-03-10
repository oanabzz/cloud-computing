package hw2.controller;

import hw2.handler.UsersHandler;

import java.io.IOException;

public class UsersController extends Controller {
    public UsersController() {
        createLocalContexts();
    }

    @Override
    void createLocalContexts() {
        httpServer.createContext("/users", new UsersHandler());
    }
}

