package ua.hillel.app;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ua.hillel.app.server.Server;

@Slf4j
public class Main {
    public static void main(String[] args) {
        log.info("Starting application...");

        try (Server server = new Server(8080)) {
            server.start();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        log.info("Application finished.");
    }
}
