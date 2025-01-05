package socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

    private void echoMessage(String body) {
        try {
            BufferedWriter response = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            response.write("HTTP/1.1 200 OK\r\n");
            response.write("Content-type: text/html\r\n");
            response.write("\r\n");
            response.write(body);
            response.flush();
            response.close();
        } catch (IOException ignored) {}
    }

    private String getRequestBody() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        Map<String, String> startLine = getStartLine(bufferedReader);
        Map<String, String> headers = getHeaders(bufferedReader);
        return getBody(bufferedReader, headers);
    }

    private String getBody(BufferedReader bufferedReader, Map<String, String> header) throws IOException {
        String length = header.get("Content-Length");
        if (length != null) {
            return getLengthBody(bufferedReader, Integer.parseInt(length));
        }
        return getNoneLengthBody(bufferedReader);
    }

    private String getLengthBody(BufferedReader reader, int length) throws IOException {
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < length; i++) {
            body.append((char) reader.read());
        }
        return body.toString();
    }

    private String getNoneLengthBody(BufferedReader reader) throws IOException {
        StringBuilder body = new StringBuilder();
        while (reader.ready()) {
            body.append((char) reader.read());
        }
        return body.toString();
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
