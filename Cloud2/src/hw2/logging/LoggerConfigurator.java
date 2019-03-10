package hw2.logging;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.writers.SharedFileWriter;

public class LoggerConfigurator {
    public static void init() {
        Configurator.currentConfig()
                .writer(new SharedFileWriter("log.txt", true))
                .activate();
    }
}
