package socket;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class BaseIOSocket extends Thread {

    private final Socket socket;

    public BaseIOSocket(Socket socket) {
        super();
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket.isConnected()) {
            try {
                PrintStream response = new PrintStream(socket.getOutputStream());
                response.println("HTTP/1.1 200 OK");
                response.println("Content-type: text/html");
                response.println();
                response.print("It is working.");
                response.flush();
                response.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
