package network;


import java.util.HashSet;


import controller.Listener;

import network.pdu.OpCode;
import network.pdu.PDU;
import network.pdu.types.ChNickPDU;
import network.pdu.types.MessagePDU;
import network.pdu.types.NicksPDU;
import network.pdu.types.SListPDU;
import network.pdu.types.UCNickPDU;
import network.pdu.types.UJoinPDU;
import network.pdu.types.ULeavePDU;

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
    private Listener<SListPDU> sListListener;
    private Listener<MessagePDU> msgListener;
    private Listener<String> nicksListener;
    private Listener<UJoinPDU> uJoinListener;
    private Listener<ULeavePDU> uLeaveListener;
    private Listener<UCNickPDU> uCNickListener;

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
        while(isConnectedToNameServer()) {

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

                        sListListener.update(pdu);
                    }
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
	    System.out.println("Disconnecting!!!");
	}

	public void SendMessage(String msg, String nick) {
		tcp.sendPDU(new MessagePDU(msg));
	}

	public void changeNick(String nick) {
	    tcp.sendPDU(new ChNickPDU(nick));
	}

	//TODO specify the wrong pdu, by checking if it's valid
	private void watchServer() {
		while(isConnectedToServer()) {

			System.out.println("waiting for input");
		    PDU pdu = tcp.getPDU();
		    System.out.println("input recieved");

		    if(pdu != null) {

		        OpCode op = OpCode.getOpCodeBy(pdu.getOpCode());
		        switch(op) {
		        //TODO check if it's a valid pdu padded, else report specific problem
		        case NICKS:
		            NicksPDU nicksPDU = (NicksPDU) pdu;
		            for(String nick:nicksPDU.getNicks()) {
		                nicksListener.update(nick);
		            }
	                break;

		        case MESSAGE:
		        	MessagePDU msgPDU = (MessagePDU) pdu;
		        	if(pdu.isValid()) {
		        		msgListener.update(msgPDU);
		        	} else {
		        		tcpErrorListener.update("invalid message");
		        		for(String error:msgPDU.getErrors()) {
		        			tcpErrorListener.update(error);
		        		}
		        	}

		        	break;

		        case UJOIN:
		            UJoinPDU ujoinPDU = (UJoinPDU) pdu;
		            if(pdu.isValid()) {
		                uJoinListener.update(ujoinPDU);
		            } else {
		                tcpErrorListener.update("Invalid ujoin");
		                for(String error:ujoinPDU.getErrors()) {
		                	tcpErrorListener.update(error);
		                }
		            }

		            break;

		        case ULEAVE:
		            if(pdu.isValid()) {
		                ULeavePDU uLeavePDU = (ULeavePDU) pdu;
			            uLeaveListener.update(uLeavePDU);
		            } else {
		                tcpErrorListener.update("invalid uleave");

		            }

		        	break;

		        case UCNICK:
		        	if(pdu.isValid()) {
		                UCNickPDU uCNickPDU = (UCNickPDU) pdu;
		                uCNickListener.update(uCNickPDU);

		        	} else {
		        		tcpErrorListener.update("invalid ucnick");
		        	}

		            break;

		        case QUIT:
		            tcpErrorListener.update("Disconnected by admin");
		            break;

		        }
		        //If disconnected by any circumstance above.
		        if(!tcp.isConnected()) {
		        	for(String error:pdu.getErrors()) {
		        		tcpErrorListener.update(error);
		        	}
		        }

		    } else {
		        if(tcp.isConnected()) {
	                tcpErrorListener.update("Unknown pdu");
		        }
		    }
		}
	}

	public boolean isConnectedToServer() {
	    return tcp.isConnected();
	}

	public boolean isConnectedToNameServer() {
	    return udp.isConnected();
	}

	public void addServerListener(Listener<SListPDU> sListListener) {
		this.sListListener = sListListener;
	}

	public void addMessageListener(Listener<MessagePDU> msgListener) {
		this.msgListener = msgListener;
	}

    public void addNicksListener(Listener<String> nicksListener) {
        this.nicksListener = nicksListener;
    }

    public void addUJoinListener(Listener<UJoinPDU> uJoinListener) {
        this.uJoinListener = uJoinListener;
    }

    public void addULeaveListener(Listener<ULeavePDU> uLeaveListener) {
        this.uLeaveListener = uLeaveListener;
    }

    public void addUCNickListener(Listener<UCNickPDU> uCNickListener) {
        this.uCNickListener = uCNickListener;
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