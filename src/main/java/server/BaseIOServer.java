package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import socket.BaseIOSocket;

public class BaseIOServer implements Server {

    private final ExecutorService executorService;
    private final int backlog;

    public BaseIOServer(int backlog) {
        if (backlog < 1) {
            this.backlog = 50;
        } else {
            this.backlog = backlog;
        }
        this.executorService = Executors.newFixedThreadPool(this.backlog);
    }

    @Override
    public void run(String host, int port) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress("localhost", 8080), backlog);
            runServer(serverSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runServer(ServerSocket serverSocket) {
        System.out.println("start Server");
        while (true) {
            try {
                Socket connection = serverSocket.accept();
                BaseIOSocket socket = new BaseIOSocket(connection);
                executorService.execute(socket::start);
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
