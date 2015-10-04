package model.network;

import java.util.ArrayList;
import java.util.HashSet;


import controller.Listener;

import model.network.pdu.OpCode;
import model.network.pdu.PDU;
import model.network.pdu.types.MessagePDU;
import model.network.pdu.types.NicksPDU;
import model.network.pdu.types.SListPDU;
import model.network.pdu.types.UJoinPDU;
//TODO ServerData nrofclients should be in string not integer
//TODO Port 64868 gives -668
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

    private final int udpTimer = 20;

    private NetworkUDP udp;
    private NetworkTCP tcp;
    private Listener<String> udpErrorListener;
    private Listener<String> tcpErrorListener;
    private Listener<ServerData> serverListener;
    private Listener<MessageData> msgListener;
    private Listener<String> nicksListener;
    private Listener<String> userJoinedListener;
    private Listener<String> userLeaveListener;
    private Thread udpThread;
    private Thread tcpThread;
    private HashSet<Integer>seqNumbs;

	public Network() {
	    udp = new NetworkUDP();
	    tcp = new NetworkTCP();
	    seqNumbs = new HashSet<Integer>();
	}

	//UDP-related
	/**
	 * If connection was sucessfull, then a monitoring thead will be used
	 * for reading the udp socket.
	 * @return true if connection was succesfull, otherwise false.
	 */
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

	/**
	 * Close currentudp socket, clear current sequence numbers.
	 */
	public void disconnectNameServer() {
		udp.disconnect();
		try {
			udpThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			udpErrorListener.update(e.getMessage());
		}
		synchronized(seqNumbs) {
			seqNumbs.clear();
		}
	}

	/**
	 *
	 */
	public void refreshServers() {
		udp.sendGetList();
        synchronized(seqNumbs) {
        	seqNumbs.clear();    //Should not create a new instance, thus not ruining lock
        }
	}

    //1. get pdu
    //2. if pdu is null                                 (The cause is that it didn't get any pdu in the given time or pdu is incorrect
         //then, send new request repeat step 1
    //2. remove timer if any is set
    //3. Check sequence number
	//    3.1 If sequence number is zero, then reset hashtable, send a new getlist.
    //3. if any sequence number is missing  /----------> (do this by looping trough list, and if not all numbers between the lowest and higest is found)
         // then set new timer for next package


//TODO synchronize seqNumbs, seqNumbs should be resetted in disconnect, and refresh.
	//TODO keep track of current sequence numbers, if zero appears twice, then clear list.
	  // if theres missing packets, set a timer to wait to receive
	/**
	 * Read packet from udp, and updates listener with latest servers.
	 */
	private void watchServerList() {
        while(udp.isConnected()) {

            SListPDU pdu = (SListPDU) udp.getPDU();

            synchronized(seqNumbs) {
                if(pdu != null) {
                    udp.setTimer(0); // reset timer
                    if(!seqNumbs.contains(pdu.getSequenceNr())) {
                        seqNumbs.add(pdu.getSequenceNr());
                        boolean seqMissed = false;

                        //Check for any missing sequence numbers
                        for(int i = 0; i < seqNumbs.size(); i++) {
                    	    if(!seqNumbs.contains(i)) {
                    		    seqMissed = true;
                    	    }
                        }

                        //If sequence numbers is missed, then to be safe,
                        // a timer will be set just in case
                        if(seqMissed) {
                            //set timer
                            udp.setTimer(udpTimer);
                        }

                        /*Update list*/
                        for(ServerData server:pdu.getServerData()) {
                            serverListener.update(server);
                        }
                    }
                    // else if (seqNr = 0) then reset hashset
                } else {
                    //requestList
                    seqNumbs.clear();   //Reset sequenceNumbers
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
	public boolean ConnectToServer(String address, int port, String nick) {
	    tcp.connect(address, port, nick);
	    if(tcp.isConnected()) {

	    	tcpThread = new Thread() {
	    		public void run() {
	    			watchServer();
	    		}
	    	};

	    	tcpThread.start();
	    }
		return tcp.isConnected();
	}

	public void disconnectServer() {
		tcp.disconnect();
		try {
			tcpThread.join();

		} catch (InterruptedException e) {
			e.printStackTrace();
			tcpErrorListener.update(e.getMessage());
		}
		System.out.println("Disconnecting to server");
	}

	public void SendMessage(String msg, String nick) {
		tcp.sendPDU(new MessagePDU(msg));
	}

	public void changeNick(String nick) {

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
		            NicksPDU nicksPDU = (NicksPDU) pdu;
		            for(String nick:nicksPDU.getNicks()) {
		                nicksListener.update(nick);
		            }
	                break;

		        case MESSAGE:
		        	msgListener.update(((MessagePDU) pdu).getMessageData());
		            break;

		        case UJOIN:
		            System.out.println("Got ujoin");
		            UJoinPDU ujoinPDU = (UJoinPDU) pdu;
		            userJoinedListener.update(ujoinPDU.getNick());
		            break;
		        }
		    } else {
		        tcp.disconnect();
		        tcpErrorListener.update("Disconnect: Invalid pdu");
		    }
		}
	}



	public void addServerListener(Listener<ServerData> serverListener) {
		this.serverListener = serverListener;
	}


	public void addMessageListener(Listener<MessageData> msgListener) {
		this.msgListener = msgListener;
	}

    public void addNicksListener(Listener<String> nickListener) {
        this.nicksListener = nickListener;
    }

    public void addUserJoinListener(Listener<String> userJoinedListener) {
        this.userJoinedListener = userJoinedListener;
    }

    public void addUserLeaveListener(Listener<String> userLeaveListener) {
        this.userLeaveListener = userLeaveListener;
    }

    public void addTCPErrorListener(Listener<String> tcpErrorListener) {
        this.tcpErrorListener = tcpErrorListener;
        tcp.addErrorListener(tcpErrorListener);
    }

    public void addUDPErrorListener(Listener<String> udpErrorListener) {
        this.udpErrorListener = udpErrorListener;
        udp.addErrorListener(udpErrorListener);
    }
}