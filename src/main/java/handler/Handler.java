package handler;

import java.nio.channels.SelectionKey;

public interface Handler {
    void handle(SelectionKey key);
}
