package model.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import model.network.pdu.OpCode;
import model.network.pdu.PDU;
import model.network.pdu.types.JoinPDU;
import model.network.pdu.types.QuitPDU;

import controller.Listener;

/**
 * @author c12ton
 * Manage network with server trough tcp.
 */
public class NetworkTCP {

    private Socket socket;
    private Listener<String> errorListener;
    private OutputStream outStream;
    private InputStream inStream;
    private boolean connected;

    public boolean connect(String address,int port, String nick) {
        try {

        	socket = new Socket(address,port);

            outStream = socket.getOutputStream();
            inStream = socket.getInputStream();

            JoinPDU joinPDU = new JoinPDU(nick,OpCode.JOIN.value);
            sendPDU(joinPDU);

        } catch (IOException e) {
            e.printStackTrace();
            errorListener.update(e.getMessage());
            return false;
        }

        connected = true;
        return true;
    }

    /**
     * Quit by sending QuitPDU, then close out,in stream and the socket.
     */
    public synchronized void disconnect() {
        try {

            QuitPDU quitPDU = new QuitPDU();
            outStream.write(quitPDU.toByteArray(), 0, quitPDU.getSize());
            outStream.flush();
            outStream.close();
            inStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            errorListener.update(e.getMessage());
        }
        connected = false;
    }

    public synchronized boolean isConnected() {
    	return connected;
    }

    public void sendPDU(PDU pdu) {
        try {
            outStream.write(pdu.toByteArray(),0,pdu.getSize());
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            errorListener.update(e.getMessage());
        }
    }


    /**
     * @return pdu packet
     */
    public PDU getPDU() {
        try {
            return PDU.fromInputStream(inStream);
        } catch (IOException e) {
           // e.printStackTrace();
        	System.out.println("Exception shit");
            errorListener.update(e.getMessage());
        }

        return null;
    }

    public void addErrorListener(Listener<String> erroLlistener) {
        this.errorListener = errorListener;
    }
}