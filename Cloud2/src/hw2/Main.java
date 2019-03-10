package hw2;

import hw2.controller.Controller;
import hw2.controller.PlacesController;
import hw2.controller.UsersController;
import hw2.handler.repo.UsersRepo;
import hw2.logging.LoggerConfigurator;

public class Main {
    public static void main(String[] args) {
        Controller userController = new UsersController();
        Controller placesController = new PlacesController();
        LoggerConfigurator.init();

//        UsersRepo repo = new UsersRepo();
//        System.out.println(repo.getUsers());
    }
}
