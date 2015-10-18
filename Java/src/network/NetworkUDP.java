package network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import network.pdu.PDU;
import network.pdu.types.GetListPDU;
import network.pdu.types.SListPDU;

import controller.Listener;

/**
 * Connects to the name server, request SLIST, and give any .
 *
 * @author c12ton
 * @version v0.0
 */
public class NetworkUDP {

	private Listener<String>errorListener;
    private DatagramSocket socket;
    private String address;
    private int port;
    private volatile boolean connection;
    private final int buffSize = 65000;

//    public NetworkUDP() {
//
//    }

    /**
     *  Connects to name server by sending getlist PDU
     *  to given address and port.
     *
     *  @return true if successful else false.
     */
    public void connect(String address,int port) {
        this.address = address;
        this.port = port;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        connection = sendGetList();
    }

    public synchronized void disconnect() {
            connection = false;
            socket.close();
    }

    public boolean sendGetList() {
        System.out.println("Seding");
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(address);

            GetListPDU pdu = new GetListPDU();
            DatagramPacket packet = new DatagramPacket(pdu.toByteArray(),
                                                        pdu.getSize(),
                                                        inetAddress,port);
            socket.send(packet);

        } catch (IOException e) {
            e.printStackTrace();
            errorListener.update(e.getMessage());
            return false;
        }
        return true;
    }

    // Change to is socket closed
    public synchronized  boolean isConnected() {
        return connection;
    }

    public void setTimer(int timeout) {
        try {
            socket.setSoTimeout(timeout);
        } catch (SocketException e) {
            e.printStackTrace();
            errorListener.update(e.getMessage());
        }
    }

    /**
     *  Retrieves packet from name server.
     *  @return packet from name-server
     */
    public PDU getPDU() {

        DatagramPacket packet = new DatagramPacket(new byte[buffSize],
                                                   buffSize);
        InputStream inStream;
        PDU pdu = null;
        try {

            socket.receive(packet);
            inStream = new ByteArrayInputStream(packet.getData());
            pdu = (SListPDU) PDU.fromInputStream(inStream);

        }catch (IOException e) {

            if(isConnected()) {
                e.printStackTrace();
                errorListener.update(e.getMessage());
            }

        }

        return pdu;
    }

    public void addErrorListener(Listener<String> errorListener) {
        this.errorListener = errorListener;
    }

}