package ua.hillel.app.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ua.hillel.app.server.ConnectionHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ConnectionTest {
    private Connection connection;

    @Mock
    private Socket socketMock;

    @Mock
    private ConnectionHandler serverMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        connection = Connection.builder()
                .connectionHandler(serverMock)
                .socket(socketMock)
                .build();
    }

    @Test
    void runTest_exitOk() throws IOException {
        ByteArrayInputStream testIn = new ByteArrayInputStream("exit".getBytes());
        ByteArrayOutputStream testOut = new ByteArrayOutputStream();

        when(socketMock.getInputStream()).thenReturn(testIn);
        when(socketMock.getOutputStream()).thenReturn(testOut);
        when(socketMock.isClosed()).thenReturn(false);
        doNothing().when(serverMock).onConnect(any());
        doNothing().when(serverMock).onMessage(any(), anyString());

        connection.run();

        assertTrue(testOut.toString().contains("Quit command received"));
        verify(serverMock, times(1)).onDisconnect(any());
    }

    @Test
    void runTest_messageOk() throws IOException {
        String actualEcho = "[SERVER] echo: Test";
        ByteArrayInputStream testIn = new ByteArrayInputStream("Test".getBytes());
        ByteArrayOutputStream testOut = new ByteArrayOutputStream();

        when(socketMock.getInputStream()).thenReturn(testIn);
        when(socketMock.getOutputStream()).thenReturn(testOut);
        when(socketMock.isClosed()).thenReturn(false).thenReturn(true);

        doNothing().when(serverMock).onConnect(any());
        doAnswer(invocation -> {
            Connection testConnection = invocation.getArgument(0);  // Capture the connection argument
            String message = invocation.getArgument(1);  // Capture the message argument

            testConnection.sendMessage("[SERVER] echo: " + message);  // Simulate the echo actualEcho

            return null;
        }).when(serverMock).onMessage(any(), anyString());

        connection.run();

        verify(serverMock, times(1)).onMessage(any(), anyString());
        assertTrue(testOut.toString().contains(actualEcho));
    }
}
