package ua.hillel.app.server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ua.hillel.app.client.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class ServerTest {
    @Mock
    private ServerSocket serverMock;
    @Mock
    private Socket clientMock;
    @Mock
    private Connection connection;
    private Server server;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Mock the ServerSocket's behavior to simulate accepting a connection
        when(serverMock.accept()).thenReturn(clientMock);

        // Initialize the server with a mock port
        server = new Server(8080) {
            @Override
            public void start() {
                // Override start to simulate the connection handling in a controlled way
                try {
                    server.onConnect(connection);
                    server.onMessage(connection, "Test");
                    server.onDisconnect(connection);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // Mock the Connection instance
        when(connection.getSocket()).thenReturn(clientMock);
        when(connection.getName()).thenReturn("Client-1");
    }

    @AfterEach
    void tearDown() throws Exception {
        server.close();
    }

    @Test
    void testConnectionAddedToActiveConnections() {
        server.onConnect(connection);

        assertTrue(server.getActiveConnections().contains(connection));
    }

    @Test
    void testConnectionRemovedFromActiveConnections() {
        server.start();
        server.onConnect(connection);
        server.onDisconnect(connection);

        assertEquals(0, server.getActiveConnections().size());
    }

    @Test
    void testSendingMessage() {
        server.start();

        String expectedMessage = "[SERVER] echo: Test";

        verify(connection).sendMessage(expectedMessage);
    }
}
