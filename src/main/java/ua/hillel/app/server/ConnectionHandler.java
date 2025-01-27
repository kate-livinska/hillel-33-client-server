package ua.hillel.app.server;

import ua.hillel.app.client.Connection;

public interface ConnectionHandler {
    void onConnect(Connection connection);
    void onDisconnect(Connection connection);
    void onMessage(Connection connection, String message);
}
