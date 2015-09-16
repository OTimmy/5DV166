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
public class SListPDU extends PDU{
	private final int pduSize = 1500;

	private ArrayList<Integer> sequenceNrs;
	private ArrayList<String> addresses;
	private ArrayList<Integer> ports;
	private ArrayList<Integer> nrClients;
	private ArrayList<String> serverNames;

	public SListPDU(byte[] bytes) {


	    sequenceNrs  = new ArrayList<Integer>();
	    addresses   = new ArrayList<String>();
        ports       = new ArrayList<Integer>();
        nrClients   = new ArrayList<Integer>();
        serverNames = new ArrayList<String>();

        parse(bytes);

	}


	public int getSize() {
		return pduSize;
	}

	/**
	 * Parser and store data in appropiate list.
	 * @return false if parsing failed.
	 */
	private boolean parse(byte[] bytes) {


		int index = 4;
		int nrOfChatServers   = (int) ((bytes [2] << 8)+ bytes[3]);

		if(nrOfChatServers > 0) {
		    sequenceNrs.add((int) bytes[1]);
		}

		for(int i = 1; i <= nrOfChatServers;i++) {


            String serverAddress = "";
		    for(int j = index; j < index + 4; j++) {
	                serverAddress += "" + ((int) bytes[j] & 0xff);
	                if( j < (index + 3)) {
	                    serverAddress += ".";
	                }
	        }

            int port        = (int) ((bytes [index + 4] << 8)+ bytes[index + 5]); //replace with ++?
	        int nrOfClients = (int) bytes [index + 6];
	        int nameLength  = (int) bytes[index + 7];

	        //start index for server name
	        index += 8;

	        // Getting servers name
	        String serverName =  "";
	        for(int j = index; j < (index + nameLength);j++) {
	            serverName += (char) bytes[j];
	        }

	        index += nameLength +  (4 - nameLength % 4) % 4;

	        addresses.add(serverAddress);
	        ports.add(port);
	        nrClients.add(nrOfClients);
	        serverNames.add(serverName);

	        // Not the correct number of servers
	        if(index >= bytes.length) {
	            return false;
	        }

	    }

		for(String name: serverNames) {
		    System.out.println("Name:"+name);
		}

	    for(String address: addresses) {
	            System.out.println("address:"+address);
	    }


	    return true;
	}


	public ArrayList getSequenceNrs() {
	    return sequenceNrs;
	}

	public ArrayList getaddresses() {
		return addresses;
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

    @Override
    public byte[] toByteArray() {
        return null;
    }

}
