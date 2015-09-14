package model.network.pdu.types;

import java.util.ArrayList;

import model.network.pdu.*;
/**
 * @author c12ton
 * @version 0.0
 *
 * Supply a empty pdu sutiable for the name-server.
 * And parse the returned data.
 */
public class SListPDU implements PDU{
	private final int pduSize = 1500;
	private ArrayList<Integer> sequenceNr;
	private ArrayList<String> hostNames;
	private ArrayList<Integer> ports;
	private ArrayList<Integer> nrClients;
	private ArrayList<String> serverNames;

	public SListPDU() {

	    sequenceNr  = new ArrayList<Integer>();
        hostNames   = new ArrayList<String>();
        ports       = new ArrayList<Integer>();
        nrClients   = new ArrayList<Integer>();
        serverNames = new ArrayList<String>();

	}


	public byte[] toByteArray() {
		byte[] bytes = new byte[pduSize];

		return bytes;
	}

	public int getSize() {
		return pduSize;
	}

	/**
	 * Parser and store data in appropiate list.
	 */
	public void parser(byte[] bytes) {
	    int sequenceIndx   = 0;
	    int nrOfChatIndex  = 2;

	    int addressStart   = 4;
	    int addressEnd     = 8;

	    //serverName length = 0;
	                        //nr of servers
	    //Alt1:

	    //for i = 1; i < nrOfServers; i++


	    //Alt2

	    for(int i = 1; i < bytes.length; i++) {




	        int sequenceNr = (int) bytes[sequenceIndx];
	        int nrOfChats  = (int) bytes[nrOfChatIndex];

	        for(address = i; j < i + 4; j++ ) {

	        }



	        //serverNameLength = length




	    }

	    //sequence nr



	    //nrOfServers
	    //address
	    //getport







	}


	public ArrayList getSequenceNr() {
	    return sequenceNr;
	}

	public ArrayList getaddresses() {
		return hostNames;
	}

	public ArrayList getPorts() {
		return ports;
	}

	public ArrayList nrOfClients() {
		return nrClients;
	}

	public String getServerName() {
		return null;
	}


}
