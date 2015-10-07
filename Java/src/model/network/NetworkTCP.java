package model.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

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
    //TODO fix a lock for disconnect of send pdu
    private final int TIME_OUT = 20;

    private Socket socket;
    private Listener<String> errorListener;
    private OutputStream outStream;
    private InputStream inStream;
    private boolean connected;
    private Object lock;

    public boolean connect(String address,int port, String nick) {
        try {
            System.out.println("Connecting");
            socket = new Socket();
        	socket.connect(new InetSocketAddress(address,port),TIME_OUT);

            outStream = socket.getOutputStream();
            inStream = socket.getInputStream();

            JoinPDU joinPDU = new JoinPDU(nick);
            sendPDU(joinPDU);
            System.out.println("Connected");

        } catch (IOException e) {
           // e.printStackTrace();
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
            if(outStream != null) {
                QuitPDU quitPDU = new QuitPDU();
                outStream.write(quitPDU.toByteArray(), 0, quitPDU.getSize());
                outStream.flush();
                outStream.close();
                inStream.close();
                socket.close();
            }

        } catch(SocketException e) {
            //IGNORE becuase of disconnect
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
            outStream.write(pdu.toByteArray(),0,pdu.toByteArray().length);
            outStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
            errorListener.update(e.getMessage());
            System.out.println("Error write");
        }
    }

    /**
     * @return pdu packet
     */
    public PDU getPDU() {
        try {
            return PDU.fromInputStream(inStream);
        } catch(SocketException e) {
           if(isConnected()) {
               e.printStackTrace();
               errorListener.update(e.getMessage());
           }

        } catch (IOException e) {
            e.printStackTrace();
        	System.out.println("Exception shit");
            errorListener.update(e.getMessage());
        }

        return null;
    }

    public void addErrorListener(Listener<String> errorListener) {
        this.errorListener = errorListener;
    }
}