package ua.hillel.app.client;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ua.hillel.app.server.ConnectionHandler;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;


@Slf4j
@Data
@Builder
public class Connection implements Runnable {
    private String name;
    private LocalDateTime time;
    private final Socket socket;
    private final ConnectionHandler connectionHandler;
    private BufferedReader in;
    private PrintWriter out;

    @Override
    public void run() {
        log.debug("Running {}", this);

        try (socket) {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream  = socket.getOutputStream();

            this.in = new BufferedReader(new InputStreamReader(inputStream));
            this.out = new PrintWriter(outputStream);

            out.println("Welcome to local server");
            out.println("Enter \"exit\" to quit");
            out.flush();

            this.connectionHandler.onConnect(this);

            while (!socket.isClosed()) {
                String message = in.readLine();
                if (message.equals("exit")) {
                    out.println("Quit command received");
                    out.flush();
                    log.debug("Closing connection: {}", this);
                    break;
                }
                this.connectionHandler.onMessage(this, message);
            }

            this.in.close();
            this.out.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            this.connectionHandler.onDisconnect(this);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
        out.flush();
    }
}
