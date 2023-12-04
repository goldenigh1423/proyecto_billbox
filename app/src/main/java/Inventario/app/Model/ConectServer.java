package Inventario.app.Model;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class ConectServer extends WebSocketClient {

    private static ConectServer instance;
    private String mensajeRecibido;

    public ConectServer(URI serverUri) {
        super(serverUri);
    }

    public static synchronized ConectServer getInstance() {
        if (instance == null) {
            try {
                // Cambia la URL a tu servidor WebSocket
                URI uri = null;
                try {
                    uri = new URI("ws", null, "192.168.68.111", 8554, null, null, null);
                } catch (URISyntaxException e) {
                }
                instance = new ConectServer(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public static void borrar(){
        instance=null;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
    }

    @Override
    public void onMessage(String message) {
        mensajeRecibido=message;
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
    }

    @Override
    public void onError(Exception ex) {
    }

    public String getMensajeRecibido() {
        return mensajeRecibido;
    }
    public void sendmessage(String message){
        send(message);
    }
}
