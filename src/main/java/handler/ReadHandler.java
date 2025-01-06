package handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ReadHandler implements Handler {

    @Override
    public void handle(SelectionKey key) {
        try {
            SocketChannel clientChannel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            int bytesRead = clientChannel.read(buffer);
            if (bytesRead == -1) {
                clientChannel.close();
                return;
            }

            buffer.flip();
            String request = new String(buffer.array(), 0, bytesRead);
            String[] requestPart = request.split("\r\n\r\n");
            String body = "";

            if (requestPart.length > 1) {
                body = requestPart[1];
            }

            String response = "HTTP/1.1 200 OK\r\nContent-Length: " +  body.length() + "\r\n\r\n" + body;
            key.attach(response);
            key.interestOps(SelectionKey.OP_WRITE);
        } catch (IOException e) {
            key.cancel();
            e.printStackTrace();
        }
    }
}
