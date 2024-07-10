package ta_alpro2;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;

public class SimpleWebSocketServer extends WebSocketServer {

    public SimpleWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        broadcast(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void broadcast(String text) {
        super.broadcast(text);
    }

    @Override
    public void onStart() {
        System.out.println("Server started successfully!");
    }

    public static void main(String[] args) {
        int port = 8888;
        WebSocketServer server = new SimpleWebSocketServer(new InetSocketAddress(port));
        server.start();
        System.out.println("WebSocket server started on port 8887");
    }
}
