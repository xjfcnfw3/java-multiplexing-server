package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import socket.BaseIOSocket;

public class BaseIOServer implements Server {

    @Override
    public void run(String host, int port) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress("localhost", 8080), 500);
            runServer(serverSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runServer(ServerSocket serverSocket) {
        while (true) {
            try {
                Socket connection = serverSocket.accept();
                BaseIOSocket socket = new BaseIOSocket(connection);
                socket.start();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        System.out.println("Server Closed");
    }
}
