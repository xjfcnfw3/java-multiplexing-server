import server.BaseIOServer;
import server.Server;

public class ServerApplication {

    public static void main(String[] args) {
        Server server = new BaseIOServer();
        server.run("localhost", 8080);
    }
}
