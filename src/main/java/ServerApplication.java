import server.BaseIOServer;
import server.Server;

public class ServerApplication {

    public static void main(String[] args) {
        Server server = new BaseIOServer(200, 70);
//        Server server = new NioServer();
        server.run("localhost", 8080);
    }
}
