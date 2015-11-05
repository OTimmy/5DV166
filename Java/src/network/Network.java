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

    private final int UDP_TIMER = 40;

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
     * Send a request for new servers.
     * And clears sequnce number hashset.
     */
    public void refreshServers(String address, int port) {
        udpErrorListener.update(""); // Reset error message.
        boolean packetSent = udp.sendGetList(address,port);

        //Alt running
        if(udpThread != null) {
            try {
                udpThread.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if((udpThread == null || !udpThread.isAlive() ) && packetSent) {
            startUDPThread();
        }

        synchronized(seqNumbs) {
            seqNumbs.clear();    //Should not create a new instance, thus not ruining lock
        }
    }

    /**
     * If connection was sucessfull, then a monitoring thead will be used
     * for reading the udp socket.
     * @return true if connection was succesfull, otherwise false.
     */
    public void startUDPThread() {
        udpErrorListener.update("");
        udpThread = new Thread() {
            public void run() {
                watchServerList();
                }
        };
        udpThread.start();

    }

    /**
     * Read packet from udp, and updates listener with latest servers.
     * According to following alogrithm:
     *
     * 1. get pdu
     * 2. if pdu is null
     *   2.1 clear all sequence numbers from hashset
     * 3.Remove timer
     * 4.if pdu's sequence number already exist.
     *   4.1 ignore the pdu, and return to step 1.
     * 5.if any sequence number is missed
     *   5.1 set timer for the next coming pdu.
     * 6. Update listener
     * 7. return to step 1.
     *
     */
    private void watchServerList() {
        boolean run = true;
        boolean seqMissed = false;
        while(run) {
            SListPDU pdu = (SListPDU) udp.getPDU();

            synchronized(seqNumbs) {
                if(pdu != null) {
                    udp.setTimer(0); // reset timer
                    if(!seqNumbs.contains(pdu.getSequenceNr())) {
                        seqNumbs.add(pdu.getSequenceNr());
                        seqMissed = false;

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
                            udp.setTimer(UDP_TIMER);
                        }

                        sListListener.update(pdu);
                    }
                } else {

                    if(seqMissed) {
                        udpErrorListener.update("Didn't recieve server " +
                                                "before timeout");
                    }

                    seqNumbs.clear();   //Reset sequenceNumbers
                    run = false;
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
    }

    public void SendMessage(String msg, String nick) {
        tcp.sendPDU(new MessagePDU(msg));
    }

    public void changeNick(String nick) {
        tcp.sendPDU(new ChNickPDU(nick));
    }

    private void watchServer() {
        while(isConnectedToServer()) {

            PDU pdu = tcp.getPDU();

            if(pdu != null && pdu.getError() == null) {

                OpCode op = OpCode.getOpCodeBy(pdu.getOpCode());
                switch(op) {

                case NICKS:
                    NicksPDU nicksPDU = (NicksPDU) pdu;
                    for(String nick:nicksPDU.getNicks()) {
                        nicksListener.update(nick);
                    }
                    break;

                case MESSAGE:
                    msgListener.update((MessagePDU) pdu);
                    break;

                case UJOIN:
                    uJoinListener.update((UJoinPDU) pdu);
                    break;

                case ULEAVE:
                    uLeaveListener.update((ULeavePDU) pdu);
                    break;

                case UCNICK:
                    uCNickListener.update((UCNickPDU) pdu);
                    break;

                case QUIT:
                    tcpErrorListener.update("Disconnected by admin");
                    break;
                }

            } else if(pdu == null && isConnectedToServer()){
                tcpErrorListener.update("Unknown pdu");
            } else if(pdu != null){
                tcpErrorListener.update("\n" + pdu.getClass().getSimpleName()
                                        + ": " +pdu.getError());
            }
        }
    }
    public boolean isConnectedToServer() {
        return tcp.isConnected();
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