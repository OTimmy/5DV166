package model.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import controller.ErrorHandler;
import controller.Listener;

import model.network.pdu.PDU;
import model.network.pdu.types.SListPDU;

/**
 * <h1>Network</h2>
 *
 * Manage the network for the client with the name-server and server.
 *
 * @author c12ton
 * @version 0.0
 *
 */
public class Network extends ErrorHandler {

    private NetworkUDP udp;
    private int nrOfServers;
    private Listener<ServerData>serverListener;
    private ErrorHandler errorHandler;

	public Network(String NameServerAddress,int port) {
	    udp = new NetworkUDP(NameServerAddress,port);
	    nrOfServers = 0;
	}

	public boolean requestServers() {
		return udp.sendGetList();
	}

	/**
	 * Read packet from udp, and updates listener with latest servers.
	 */
	public void updateServers() {
	    try {
	        while(true) {

	            byte[] bytes = udp.getSListPacketData();
	            InputStream inStream = new ByteArrayInputStream(bytes);
	            SListPDU pdu = (SListPDU) PDU.fromInputStream(inStream);
	            nrOfServers = (int) ((bytes[2] << 8)+ bytes[3]);

	            /*listener.update*/
	            for(ServerData server:pdu.getServerData()) {
	                serverListener.update(server);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        reportError(e.getMessage());
	    }
	}

	public int getNrOfServers() {
	    return nrOfServers;
	}

	public void addUDPListener(Listener<ServerData> listener) {
	    serverListener = listener;
	}

	public void addTCPListener() {

	}
}