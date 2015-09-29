package model.network;


import java.util.ArrayList;

import controller.Listener;

import model.network.pdu.OpCode;
import model.network.pdu.PDU;
import model.network.pdu.types.MessagePDU;
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
    private Listener<String> errorListener;
    private Listener<ServerData> serverListener;
    private Listener<MessageData> msgListener;
    private Thread udpThread;
    private Thread tcpThread;
    private ArrayList<Integer> sequenceNumbs;


	public Network() {
	    udp = new NetworkUDP();
	    tcp = new NetworkTCP();
	    nrOfServers = 0;
	    sequenceNumbs = new ArrayList<Integer>();
	}

	//UDP-related
	public boolean connectToNameServer(String address, int port) {
		udp.connect(address, port);

		if(udp.isConnected()) {
			udpThread = new Thread() {
				public void run() {
					watchServerList();
				}
			};
			udpThread.start();
		}

		return udp.isConnected();
	}

	public void disconnectNameServer() {
		udp.disconnect();
		try {
			udpThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			errorListener.update(e.getMessage());
		}
	}

	public void refreshServers() {
		udp.sendGetList();
        serverListener.update(null);      //Reset serverlist
        sequenceNumbs = new ArrayList<Integer>();  //reset list of sequence numbers
    }

	/**
	 * Read packet from udp, and updates listener with latest servers.
	 */
	private void watchServerList() {
        while(udp.isConnected()) {

        	SListPDU pdu = (SListPDU) udp.getPDU();

            int expectSequenceNr = 0;  //default value
            if(sequenceNumbs.size() > 0) {

                expectSequenceNr = sequenceNumbs.get(sequenceNumbs.size()
                                                         -1).intValue() + 1;
            }

            if(expectSequenceNr == pdu.getSequenceNr()) {
                sequenceNumbs.add(pdu.getSequenceNr());
                /*Update list*/
                for(ServerData server:pdu.getServerData()) { //send whole array list instead, and after ever update remove previouse servers
                    serverListener.update(server);
                }
            }
        }
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
	    	tcpThread = new Thread() {
	    		public void run() {
	    			watchServer();
	    		}
	    	};
	    	tcpThread.run();

	    }
		return tcp.isConnected();
	}

	public void disconnectServer() {
		tcp.disconnect();
		try {
			tcpThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			errorListener.update(e.getMessage());
		}
	}

	public void SendMessage(String msg) {
		tcp.sendPDU(new MessagePDU(msg));
	}

	private void watchServer() {
		while(tcp.isConnected()) {

			System.out.println("waiting");
		    PDU pdu = tcp.getPDU();

		    System.out.println("done");
		    if(pdu != null) {

		        OpCode op = OpCode.getOpCodeBy(pdu.getOpCode());
		        switch(op) {

		        case NICKS:
		            nicksListener.update();
		            break;

		        case MESSAGE:
		        	msgListener.update(((MessagePDU) pdu).getMessageData());
		            break;

		        case UJOIN:
		            break;
		        }
		    }
		}
	}



	public void addServersUsersListListener() {

	}

	public void addUserJoinListener(Listener<String> listener) {

	}

	public void addServerListener(Listener<ServerData> serverListener) {
		this.serverListener = serverListener;
	}

	public void addErrorListener(Listener<String> errorListener) {
	    this.errorListener = errorListener;
	    tcp.addErrorListener(errorListener);
	    udp.addErrorListener(errorListener);
	}

	public void addMessageListener(Listener<MessageData> msgListener) {
		this.msgListener = msgListener;
	}



    public void addNicksListener(Listener<ArrayList<String>> listener) {
        // TODO Auto-generated method stub

    }
}