package model.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import model.network.pdu.PDU;
import model.network.pdu.types.JoinPDU;

import controller.Listener;

/**
 * @author c12ton
 * Manage network with server trough tcp.
 */
public class NetworkTCP {

    private final int pduBuffSize = 65539; //MAX
    private final int timeOut = 250;

    private Socket socket;
    private Listener<String> errListener;
    private OutputStream outStream;
    private InputStream inStream;


    public boolean connect(String address,int port, String nick) {
        try {
            
        	socket = new Socket(address,port);

            outStream = socket.getOutputStream();
            inStream = socket.getInputStream();

            JoinPDU joinPDU = new JoinPDU(nick);
            sendPDU(joinPDU);
           
        } catch (IOException e) {
            e.printStackTrace();
            errListener.update(e.getMessage()); //Socket
            return false;
        }
        return true;
    }


    public void disconnect() {
        try {
        	
        	//QUIT PDU
        	
        	
            outStream.close();
            inStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            errListener.update(e.getMessage());
        }
    }

    public void sendPDU(PDU pdu) {
        try {
            outStream.write(pdu.toByteArray(),0,pdu.getSize());
        } catch (IOException e) {
            e.printStackTrace();
            errListener.update(e.getMessage());
        }
    }

    /**
     * @return pdu packet
     */
    public byte[] getPacket() {
        byte[] bytes = new byte[pduBuffSize];
        try {
            inStream.read(bytes, 0, bytes.length);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }

    public void addListener(Listener<String> errListener) {
        this.errListener = errListener;
    }
}