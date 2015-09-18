package model.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import controller.ErrorManager;

import model.network.pdu.PDU;
import model.network.pdu.types.SListPDU;
// TODO UDP, client should print the current avalible servers, eventhough it's not the correct amount.
//      And if any new sers should arrive later, it will diplsay them. But untill that, gui should point it out, that it's waiting for more servers

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
    //private Thread tcp.............


	Network(String NameServerAddress,int port) {
	    udp = new NetworkUDP(NameServerAddress,port);
	}

	/**
	 * Trying to retrive the SLIST, if no correct responed is retrived, then null
	 * will be returned. 
	 */
	public ArrayList getServerData() {

	    if( udp.requestSList() == true) {
	        try {

	            InputStream inStream =
	                               new ByteArrayInputStream(udp.getSListBytes());
	            SListPDU pdu = (SListPDU) PDU.fromInputStream(inStream);
	            return pdu.getServerData();

	        } catch (IOException e) {
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

	public void startUDPWatchThread() {

	}

	public void stopAllThreads() {
	    //thread.terminate
	    //thread.join()
	}
}