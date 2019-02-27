package hw1;

import hw1.controller.Controller;
import hw1.controller.AdviceController;
import hw1.controller.MetricsController;
import hw1.controller.PagesController;
import hw1.handler.MetricsHandler;
import hw1.logging.LoggerConfigurator;
import org.pmw.tinylog.Logger;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        LoggerConfigurator.init();

        PagesController pagesController = new PagesController();
        Controller factsController = new AdviceController();
        MetricsController metricsController = new MetricsController();
        Logger.info("Server running at 127.0.0.1:6969");


    }
}
