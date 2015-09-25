package model.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import model.network.pdu.PDU;
import model.network.pdu.types.GetListPDU;

import controller.Listener;

/**
 * Connects to the name server, request SLIST, and give any .
 *
 * @author c12ton
 * @version v0.0
 */
public class NetworkUDP {

    private Listener listener;
    private String address;
    private int port;
    private DatagramSocket socket;



    public NetworkUDP(String address,int port) {

        this.address = address;
        this.port = port;

        try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			listener.reportErr(e.getMessage());
		}
    }
    //Change sendGetList to connect??
    /**
     *  Sends GETLIST PDU.
     */
    public boolean sendGetList() {
        try {

            InetAddress address = InetAddress.getByName(this.address);
            GetListPDU pdu = new GetListPDU();
            DatagramPacket packet = new DatagramPacket(pdu.toByteArray(),
                                                        pdu.getSize(),
                                                        address,port);
            socket.send(packet);

        } catch(IOException e) {
            e.printStackTrace();
            listener.reportErr(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     *  Retrieves packet from name server.
     *  @return packet from name-server
     */
    public PDU getPDU() {

        DatagramPacket packet = new DatagramPacket(new byte[PDU.pduSize()], PDU.pduSize());
        InputStream inStream;
        PDU SList = null;
        try {

                socket.receive(packet);
                inStream = new ByteArrayInputStream(packet.getData());
                SList = PDU.fromInputStream(inStream);
        }catch (IOException e) {
                e.printStackTrace();
        }

        return SList;

    }

    public void addListener(Listener listener) {
        this.listener = listener;
    }
}