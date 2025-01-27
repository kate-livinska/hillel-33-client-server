package ua.hillel.app.server;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ua.hillel.app.client.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Slf4j
public class Server implements ConnectionHandler, AutoCloseable{
    private final List<Connection> activeConnections;
    private final ServerSocket serverSocket;
    private final AtomicInteger activeConnectionsCounter = new AtomicInteger(0);

    public Server(int port) throws IOException {
        log.debug("Initializing server on port {}", port);

        serverSocket = new ServerSocket(port);
        activeConnections = Collections.synchronizedList(new ArrayList<>());
    }

    public void start() {
        log.info("Starting server");

        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                log.info("New client connected");

                log.debug("Init client");
                if (activeConnections.isEmpty()) {
                    activeConnectionsCounter.set(0);
                }
                Runnable clientConnection = Connection.builder()
                        .connectionHandler(this)
                        .socket(clientSocket)
                        .name("Client-" + activeConnectionsCounter.incrementAndGet())
                        .time(LocalDateTime.now())
                        .build();
                new Thread(clientConnection).start();
            } catch (IOException e) {
                log.error("Server error: {}", e.getMessage(), e);
                break;
            }
        }
    }

    @Override
    public void onConnect(Connection connection) {
        log.info("[SERVER] Client {} connected successfully", connection.getName());
        log.debug("Connection: {}", connection);

        this.activeConnections.add(connection);
    }

    @Override
    public void onDisconnect(Connection connection) {
        log.info("[SERVER] Client {} disconnected successfully", connection.getName());
        log.debug("Closed connection: {}", connection);
        this.activeConnections.remove(connection);
    }

    @Override
    public void onMessage(Connection connection, String message) {
        log.info("[SERVER] Message received: {}", message);
        connection.sendMessage("[SERVER] echo: " + message);
    }

    @Override
    public void close() throws Exception {
        log.info("[SERVER] Closing server");
        serverSocket.close();
    }
}
