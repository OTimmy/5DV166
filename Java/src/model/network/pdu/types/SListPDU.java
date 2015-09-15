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

	    //serverName length = 0;
	                        //nr of servers

		int index = 0;
		int nrOfChatservers   = (int) ((bytes [0 + 2] << 8)+ bytes[0 + 3]);
	    for(int i = 1; i <= nrOfChatservers;i++) {
		
	        int sequenceNr        = (int) bytes[index+1];
            int port              = (int) ((bytes [index + 8] << 8)+ bytes[index + 9]);
	        int nrOfClients       = (int) bytes [index + 10];                
	        int nameLength        = (int) bytes[index + 11]; 
	        	        
	        index += 11 + nameLength;
	        
	        
	        System.out.println("--------------------");
	        System.out.println("index: "+index);
	        
	        
	        //System.out.println("Next index val: "+ index);
	        
	        
	        
	    }
//	        //Address
//	        for(int j = i + 4; j < i + 8; j++ ) {
//
//	        }
//	        
//	        //server name
//	        for(int j = i + 12; j < i + nameLength; j++) {
//	        	
//	        }
//	        System.out.println("s");
//	        i += nameLength;
	     // 
//
//	    }
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
