package handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class WriteHandler implements Handler {
    @Override
    public void handle(SelectionKey key) {
        try {
            SocketChannel clientChannel = (SocketChannel) key.channel();
            String response = (String) key.attachment();

            ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
            clientChannel.write(buffer);

            if (!buffer.hasRemaining()) {
                key.interestOps(SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
