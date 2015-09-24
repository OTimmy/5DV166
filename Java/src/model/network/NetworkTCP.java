package model.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import controller.Listener;

/**
 * @author c12ton
 * Manage network with server trough tcp.
 *
 *
 */
public class NetworkTCP {

    private Socket socket;
    private Listener<String> errListener;
    private OutputStream out;
    private InputStream in;


    public void connect(String address,int port, String nickName) {
        try {
            socket = new Socket(address,port);
            out = socket.getOutputStream();
            in = socket.getInputStream();
            //sendPDU(bytes)
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            errListener.update(e.getMessage());
        }
    }

    public void sendPDU(byte[] bytes) {

    }

    public void disconnect() {
        try {
            //inStream.close();
            //outStream.close();
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            errListener.update(e.getMessage());
        }
    }

    public void readInputStream() {

    }

    public void addListener(Listener<String> errListener) {
        this.errListener = errListener;
    }

}
