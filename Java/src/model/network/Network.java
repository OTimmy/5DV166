package model.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

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
public class Network {

    private NetworkUDP udp;
    private NetworkTCP tcp;
    private int nrOfServers;
    private Listener<ServerData>serverListener;
    private Listener<String>errListener;


	public Network(String NameServerAddress,int port) {
	    udp = new NetworkUDP(NameServerAddress,port);
	    tcp = new NetworkTCP();
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
	        errListener.update(e.getMessage());
	    }
	}

	public int getNrOfServers() {
	    return nrOfServers;
	}


	public void connectToServer(String address, int port) {
	    tcp.connect(address, port);
	}


	public void addUDPListener(Listener<ServerData> listener) {
	    serverListener = listener;
	}

	public void addTCPListener() {

	    //tcp.addErrListener(Listener);
	}

	public void addErrListener(Listener<String> errListener) {
	    this.errListener = errListener;
	    udp.addListener(errListener);
	    tcp.addListener(errListener);
	}
}