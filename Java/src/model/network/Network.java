package model.network;


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
    private Thread udpThread;
    private Thread tcpThread;
    
	public Network() { 
	    udp = new NetworkUDP();
	    tcp = new NetworkTCP();
	    nrOfServers = 0;
	}

	//UDP-related
	public boolean connectToNameServer(String address, int port) {
		udp.connect(address, port);
		
		if(udp.isConnected()) {
			watchServerList();
		}
		
		return udp.isConnected();
	}
	
	public void disconnectNameServer() {
		udp.disconnect();
		try {
			udpThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			listener.reportErr(e.getMessage());
		}
	}
	
	public void refreshServers() {
		udp.sendGetList();
	}

	/**
	 * Read packet from udp, and updates listener with latest servers.
	 */
	private void watchServerList() {
		udpThread = new Thread() {
			public void run() {
			
				while(udp.isConnected()) { 

					SListPDU pdu = (SListPDU) udp.getPDU();
					//This might be wrong way of doing it. 
					nrOfServers = (int) ((pdu.toByteArray()[2] << 8) 
							| (pdu.toByteArray()[3] & 0xff));

					/*Update list*/
					for(ServerData server:pdu.getServerData()) {
						listener.addServer(server);
					}
				}
			}
		};
		udpThread.start();
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
	public boolean ConnectToServer(String address, int port) {
	    tcp.connect(address, port, "nick");
	    if(tcp.isConnected()) {
	    	watchServer();
	    }
		return tcp.isConnected();
	}

	public void disconnectServer() {
		tcp.disconnect();
		try {
			tcpThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			listener.reportErr(e.getMessage());
		}
	}
	
	public void SendMessage(String msg) {

	}

	private void watchServer() {
		while(tcp.isConnected()) { 
	
			System.out.println("waiting");
		    PDU pdu = tcp.getPDU();
		    System.out.println("done");
		    if(pdu != null) {
		        System.out.println("asdasd");
		        /*determine type of packet*/

		        /*Call corresponding listener*/
		    }
		}
	}

	public void addListener(Listener listener) {
	    this.listener = listener;
	    tcp.addListener(listener);
	    udp.addListener(listener);
	}
}