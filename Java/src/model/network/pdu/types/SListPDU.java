package model.network.pdu.types;

import java.util.ArrayList;

import model.network.ServerData;
import model.network.pdu.*;
/**
 * @author c12ton
 * @version 0.0
 *
 * And parse the returned data.
 */
public class SListPDU extends PDU{
	private final int pduSize = 1500;
	//static sequence hashtable?? Or arrayList?
	private int currentSequence;
	private ArrayList<ServerData> servers;

	public SListPDU(byte[] bytes) {
	    servers = new ArrayList<ServerData>();
        parse(bytes);
	}

	/**
	 * Parser and store data in appropiate list.
	 * @return false if parsing failed.
	 */
	private void parse(byte[] bytes) {

		int index = 4;
		currentSequence = (int) bytes[1];

		while(index < bytes.length) {  //UGLY look for a better solution!

            String address = "";
		    for(int j = index; j < index + 4; j++) {
	                address += "" + ((int) bytes[j] & 0xff);
	                if( j < (index + 3)) {
	                    address += ".";
	                }
	        }

            int port        = (int) ((bytes [index + 4] << 8)+ bytes[index + 5]);
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

	        servers.add(new ServerData(serverName,address,port,nrOfClients));
	    }
	}

	public ArrayList getServerData() {
	    return servers;
	}

	public int getSize() {
	    return pduSize;
	}


    @Override
    public byte[] toByteArray() {
        return null;
    }

}