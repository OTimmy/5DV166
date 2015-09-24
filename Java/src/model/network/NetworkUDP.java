package model.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import model.network.pdu.types.GetListPDU;

import controller.Listener;

/**
 * Connects to the name server, request SLIST, and give any .
 *
 * @author c12ton
 * @version v0.0
 */
public class NetworkUDP {

    private final int UDP_BUFF = 16;

    private Listener<String>errListener;
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
		}
    }

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
            return false;
        }

        return true;
    }

    /**
     *  Retrieves packet from name server.
     *  @return packet from name-server
     */
    public byte[] getSListPacketData() {

        DatagramPacket packet = new DatagramPacket(new byte[UDP_BUFF], UDP_BUFF);

        try {
                socket.receive(packet);
            }catch (IOException e) {
                e.printStackTrace();
            }

        return packet.getData();

    }

    public void addListener(Listener<String> listener) {
        this.errListener = listener;
    }
}