package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
                String body = getRequestBody();
                echoMessage(body);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }
    }

    private void echoMessage(String body) throws IOException {
        PrintStream response = new PrintStream(socket.getOutputStream());
        response.println("HTTP/1.1 200 OK");
        response.println("Content-type: text/html");
        response.println();
        response.print(body);
        response.flush();
        response.close();
    }

    private String getRequestBody() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Map<String, String> startLine = getStartLine(bufferedReader);
        Map<String, String> headers = getHeaders(bufferedReader);
        return getBody(bufferedReader, headers);
    }

    private String getBody(BufferedReader bufferedReader, Map<String, String> header) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int length = Integer.parseInt(header.get("Content-Length"));
        for (int i = 0; i < length; i++) {
            stringBuilder.append((char) bufferedReader.read());
        }
        return stringBuilder.toString();
    }

    private Map<String, String> getStartLine(BufferedReader bufferedReader) throws IOException {
        Map<String, String> startLine = new HashMap<>();
        String[] values = bufferedReader.readLine().split(" ", 3);
        startLine.put("METHOD", values[0]);
        startLine.put("URI", values[1]);
        startLine.put("VERSION", values[2]);
        return startLine;
    }

    private Map<String, String> getHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String header;
        while ((header = bufferedReader.readLine()) != null && !header.isEmpty()) {
            String[] headerKeyValue = header.split(": ", 2);
            headers.put(headerKeyValue[0], headerKeyValue[1]);
        }
        return headers;
    }
}
