package server;

import handler.AcceptHandler;
import handler.ReadHandler;
import handler.WriteHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer implements Server {

    @Override
    public void run(String host, int port) {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = initServerChannel(selector, new InetSocketAddress(host, port));

            System.out.println("start Server");
            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        AcceptHandler acceptHandler = new AcceptHandler(serverSocketChannel, selector);
                        acceptHandler.handle(key);
                    } else if (key.isReadable()) {
                        ReadHandler readHandler = new ReadHandler();
                        readHandler.handle(key);
                    } else if (key.isWritable()) {
                        WriteHandler writeHandler = new WriteHandler();
                        writeHandler.handle(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ServerSocketChannel initServerChannel(Selector selector, InetSocketAddress inetAddress) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(inetAddress, 200);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        return serverSocketChannel;
    }
}
