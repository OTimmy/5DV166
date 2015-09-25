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
    private Listener listener;

	public Network(String NameServerAddress, int port) {
	    udp = new NetworkUDP(NameServerAddress,port);
	    tcp = new NetworkTCP();
	    nrOfServers = 0;
	}

	//UDP-related

	public void ConnectToNameServer(String address, int port) {
	    /*Stop watch servers*/
	    /*init udp*/
	    /*set watch servers to true*/
	}


	public boolean requestServers() {
		return udp.sendGetList();
	}

	/**
	 * Read packet from udp, and updates listener with latest servers.
	 */
	public void watchServers() {
	    while(true) { //Call synchronized method watchCondition

            SListPDU pdu = (SListPDU) udp.getPDU();
            if(pdu == null) {
                System.out.println("FUUUK");
            }
            nrOfServers = (int) ((pdu.toByteArray()[2] << 8)+ pdu.toByteArray()[3]);

            /*Update list*/
            for(ServerData server:pdu.getServerData()) {
                listener.addServer(server);
            }
        }
	}

	public int getNrOfServers() {
	    return nrOfServers;
	}

	//TCP-related
	/**
	 * @param ip address for server and its port.
	 * @return List of clients or null if unsuccessful.
	 *
	 */
	public void ConnectToServer(String address, int port) {
	    tcp.connect(address, port,"nick");
	    tcp.getPDU(); //Should contain a list of nicks
	}

	public void SendMessage(String msg) {

	}

	public void watchServer() {
		while(true) {
		    System.out.println("waiting");
		    PDU pdu = tcp.getPDU();
		    System.out.println("fuuuk");
		    if(pdu != null) {
		        System.out.println("asdasd");
		        /*determine type of packet*/

		        /*Call corresponding listener*/
		    }
		}
	}

	public void addListener(Listener listener) {
	    this.listener = listener;
	}
}