package model.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import model.network.pdu.types.GetListPDU;

import controller.ErrorManager;

/**
 * Connects to the name server, request SLIST, and give any .
 *
 * @author c12ton
 * @version v0.0
 */
public class NetworkUDP extends ErrorManager{


    private final int UDP_BUFF = 1500;

    private String address;
    private int port;

    private DatagramSocket socket;
    private DatagramPacket packet;


    public NetworkUDP(String address,int port) {
        this.address = address;
        this.port = port;
        this.packet = new DatagramPacket(new byte[UDP_BUFF],UDP_BUFF);
        
    }

    //TODO sync this method, because of socket
    /**
     *  Sends GETLIST PDU.
     */
    public boolean requestSList() {
        try {

            InetAddress address = InetAddress.getByName(this.address);
            GetListPDU pdu = new GetListPDU();

            DatagramPacket packet = new DatagramPacket(pdu.toByteArray(),
                                                        pdu.getSize(),
                                                        address,port);
            socket = new DatagramSocket();
            socket.send(packet);

        } catch(IOException e) {
            reportError(e.toString());
            return false;
        }

        return true;
    }
    
    //TODO Sync this method, because of socket and packet
    public byte[] getSListBytes() {
        DatagramPacket packet = this.packet;
        this.packet = null;

        if(packet != null) {
            return packet.getData();
        }

        return null;
    }
    
    
    //TODO sync this method.
    /**
     *  Retrives packet from name server.
     */
    public void watchUDP() {
        while(true) {  //remove this!
            try {

                DatagramPacket packet = new DatagramPacket(new byte[UDP_BUFF],
                                                           UDP_BUFF);
                socket.receive(packet);
                this.packet = packet;
                System.out.println("DONE");

            }catch (IOException e) {
                reportError(e.toString());
            }
        }
    }
}
