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

    private final Object lock;


    public NetworkUDP(String address,int port) {

        this.address = address;
        this.port = port;
        packet = new DatagramPacket(new byte[UDP_BUFF],UDP_BUFF);
        lock = new Object();

        try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			reportError(e.getMessage());
		}
    }

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

            socket.send(packet);
  
        } catch(IOException e) {
            e.printStackTrace();
            reportError(e.getMessage());
            return false;
        } 

        return true;
    }

    public byte[] getSListBytes() {
    	DatagramPacket packet;
    	synchronized(lock) {
    		
    		if(this.packet == null) {
				try {
					lock.wait(); //wait tills a new packet is received. Release lock
				} catch (InterruptedException e) {
					e.printStackTrace();
				}   
    		}
    		
    		packet = this.packet;
    		this.packet = null;    			
    	}
    	
    	return packet.getData();
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