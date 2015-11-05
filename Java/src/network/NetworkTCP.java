package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import network.pdu.PDU;
import network.pdu.types.JoinPDU;
import network.pdu.types.QuitPDU;

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
    private final String ERROR_CONNECT = "Couldn't connect to server";

    public boolean connect(String address,int port, String nick) {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(address,port),TIME_OUT);

            outStream = socket.getOutputStream();
            inStream = socket.getInputStream();

            connected = true;
            JoinPDU joinPDU = new JoinPDU(nick);
            sendPDU(joinPDU);

        } catch (IOException e) {
            errorListener.update(ERROR_CONNECT);
            return false;
        }

        return true;
    }

    /**
     * Quit by sending QuitPDU, then close out,in stream and the socket.
     */
    public synchronized void disconnect() {
        if(isConnected()) {
        	connected = false;
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
        }


    }

    public synchronized boolean isConnected() {
        return connected;
    }

    public void sendPDU(PDU pdu) {
        try {
            if(isConnected()) {
                outStream.write(pdu.toByteArray(),0,pdu.toByteArray().length);
                outStream.flush();
            }
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
        } catch(SocketException e) {
           if(isConnected()) {
               errorListener.update(e.getMessage());
           }

        } catch (IOException e) {
            errorListener.update(e.getMessage());
        }

        return null;
    }

    public void addErrorListener(Listener<String> errorListener) {
        this.errorListener = errorListener;
    }
}