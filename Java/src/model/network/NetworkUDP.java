package model.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

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

    private Object lock;


    public NetworkUDP(String address,int port) {

        this.address = address;
        this.port = port;
        packet = new DatagramPacket(new byte[UDP_BUFF],UDP_BUFF);
        lock = new Object();

        try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
		    e.printStackTrace();
			reportError(e.getMessage());
		}
    }

    /**
     *  Sends GETLIST PDU.
     */
    public boolean requestSList() {
        try {

            System.out.println("Sending package");
            InetAddress address = InetAddress.getByName(this.address);
            GetListPDU pdu = new GetListPDU();
            DatagramPacket packet = new DatagramPacket(pdu.toByteArray(),
                                                        pdu.getSize(),
                                                        address,port);
            socket.send(packet);

            synchronized(lock) {
                System.out.println("Waiting on packet");
                lock.wait();
                System.out.println("Done waiting on packet");
            }


        } catch(IOException e) {
            e.printStackTrace();
            reportError(e.getMessage());
            return false;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public byte[] getSListBytes() {

        DatagramPacket packet = this.packet;
        this.packet = null;

        if(packet != null) {
            return packet.getData();
        }

        return null;
    }

    /**
     *  Retrives packet from name server.
     */
    public void watchUDP() {
        while(true) {
            try {

                DatagramPacket packet = new DatagramPacket(new byte[UDP_BUFF],
                                                           UDP_BUFF);
                System.out.println("Waiting socket");
                socket.receive(packet);

                synchronized(lock) {

                    this.packet = packet;
                    lock.notify();
                }

            }catch (IOException e) {
                e.printStackTrace();
                reportError(e.toString());
            }
        }
    }
}