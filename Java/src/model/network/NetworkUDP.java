package model.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import model.network.pdu.PDU;
import model.network.pdu.types.GetListPDU;
import model.network.pdu.types.SListPDU;

import controller.Listener;

/**
 * Connects to the name server, request SLIST, and give any .
 *
 * @author c12ton
 * @version v0.0
 */
public class NetworkUDP {

	private Listener<String>errorListener;
    private Listener listener;
    private DatagramSocket socket;
    private String address;
    private int port;
    private boolean connected;

    public NetworkUDP() {
        try {
			socket = new DatagramSocket();			
		} catch (SocketException e) {
			e.printStackTrace();
			listener.reportErr(e.getMessage());
		}
    }
    
    /**
     *  Connects to name server by sending getlist PDU 
     *  to given address and port.
     *  
     *  @return true if successful else false. 
     */
    public void connect(String address,int port) {
        this.address = address;
        this.port = port;
        connected = sendGetList();
    }
    
    public synchronized void disconnect() {
    	connected = false;
    }
    
    public boolean sendGetList() {

        InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getByName(address);
			
	        GetListPDU pdu = new GetListPDU();
	        DatagramPacket packet = new DatagramPacket(pdu.toByteArray(),
	                                                    pdu.getSize(),
	                                                    inetAddress,port);
			socket.send(packet);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
        return true;
    }
    
    
    public synchronized boolean isConnected() {
    	return connected;
    }

    /**
     *  Retrieves packet from name server.
     *  @return packet from name-server
     */
    public PDU getPDU() {
    	
    		DatagramPacket packet = new DatagramPacket(new byte[PDU.pduSize()], PDU.pduSize());
    		InputStream inStream;
    		PDU pdu = null;
    		try {
                socket.receive(packet);
                inStream = new ByteArrayInputStream(packet.getData());
                pdu = (SListPDU) PDU.fromInputStream(inStream);
                 
    		}catch (IOException e) {
                e.printStackTrace();    
                listener.reportErr(e.getMessage());
                disconnect();
    		}
    		
    	return pdu;
    }

    public void addErrorListener() {
    	
    }
    
    public void addListener(Listener listener) {
        this.listener = listener;
    }
}