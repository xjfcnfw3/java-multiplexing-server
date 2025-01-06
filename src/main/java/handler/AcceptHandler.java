package handler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class AcceptHandler implements Handler {
    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;

    public AcceptHandler(ServerSocketChannel serverSocketChannel, Selector selector) {
        this.serverSocketChannel = serverSocketChannel;
        this.selector = selector;
    }

    @Override
    public void handle(SelectionKey key) {
        try {
            SocketChannel clientChannel = serverSocketChannel.accept();
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            key.cancel();
            System.err.println("키 처리 중 에러 발생: " + e.getMessage());
        }
    }
}
