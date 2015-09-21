package model.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import controller.ErrorManager;

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
public class Network extends ErrorManager {

    private NetworkUDP udp;
    private Thread watchUDPThread;


	public Network(String NameServerAddress,int port) {
	    udp = new NetworkUDP(NameServerAddress,port);
	}

	/**
	 * Trying to retrive the SLIST, if no correct responed is retrived, then null
	 * will be returned.
	 */
	public ArrayList getServers() {

	    if( udp.requestSList() == true) {
	        try {

	            InputStream inStream =
	                               new ByteArrayInputStream(udp.getSListBytes());
	            SListPDU pdu = (SListPDU) PDU.fromInputStream(inStream);

	            //If no new pdu is avalible.
	            if(pdu != null) {
	                //copy original, then nullify it. Before sending.
	                ArrayList servers = (ArrayList) pdu.getServerData().clone();
	                pdu = null;
	            	return servers;
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	            reportError(e.getMessage());
	        }
	    }

	    return null;
	}

	public int getNrOfServers() {
		byte[] bytes = udp.getSListBytes();

		if(bytes != null) {
	        return (int) ((bytes[2] << 8)+ bytes[3]);
	    }

	    return 0;
	}

	public void startWatchUDPThread() {

		watchUDPThread = new Thread() {
			public void run() {

				udp.watchUDP();
			}
		};

		watchUDPThread.start();
	}

	/**
	 *
	 */
	public void stopAllThreads() {
	   // watchUDPThread.
		//thread.terminate
	    //thread.join()
	}
}