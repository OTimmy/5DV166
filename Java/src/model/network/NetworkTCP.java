package model.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import model.network.pdu.types.JoinPDU;

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


    public void connect(String address,int port, String nick) {
        try {
            socket = new Socket(address,port);
            out = socket.getOutputStream();
            in = socket.getInputStream();

            JoinPDU joinPDU = new JoinPDU(nick);
            out.write(joinPDU.toByteArray(), 0, joinPDU.getSize());

        } catch (IOException e) {
            e.printStackTrace();
            errListener.update(e.getMessage()); //Socket
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